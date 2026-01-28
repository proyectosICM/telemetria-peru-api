package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.models.FuelTheftAlertModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import com.icm.telemetria_peru_api.repositories.FuelTheftAlertRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuelTheftDetectionService {

    private final FuelRecordRepository fuelRecordRepository;
    private final FuelTheftAlertRepository alertRepository;

    @PersistenceContext
    private EntityManager em;

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    // ===== Ajusta a tu negocio =====
    // Si fuelInfo es "litros", pon por ej 10-30 litros.
    // Si es "%", pon por ej 5-15%.
    private static final double DROP_THRESHOLD = 10.0;

    // tolerancia de “variación” para decir que se mantiene abajo
    private static final double STABILITY_TOL = 1.0;

    // evita spam: no crear otra alerta OPEN en últimos X minutos
    private static final int COOLDOWN_MINUTES = 30;

    @Transactional
    public void analyzeVehicle(Long vehicleId) {
        if (vehicleId == null) return;

        List<FuelRecordModel> rows =
                fuelRecordRepository.findTop10ByVehicleModel_IdOrderByCreatedAtDesc(vehicleId);

        if (rows.size() < 8) return;

        // vienen DESC, pasa a ASC
        Collections.reverse(rows);

        // valores
        List<Double> values = new ArrayList<>();
        for (FuelRecordModel r : rows) {
            if (r.getValueData() != null) values.add(r.getValueData());
        }
        if (values.size() < 8) return;

        // filtro glitch 0: si 0 está entre dos valores >0, ignóralo
        List<Double> clean = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            double v = values.get(i);
            if (v == 0.0 && i > 0 && i < values.size() - 1) {
                double prev = values.get(i - 1);
                double next = values.get(i + 1);
                if (prev > 0.0 && next > 0.0) continue; // glitch
            }
            clean.add(v);
        }
        if (clean.size() < 8) return;

        // baseline: primeros 5 (mediana)
        double baseline = median(clean.subList(0, Math.min(5, clean.size())));

        // recent: últimos 3 (mediana)
        int n = clean.size();
        double recent = median(clean.subList(n - 3, n));

        double drop = baseline - recent;
        if (drop < DROP_THRESHOLD) return;

        // persistencia: últimos 3 no “rebotan”
        boolean stable = true;
        for (int i = n - 3; i < n; i++) {
            if (clean.get(i) > recent + STABILITY_TOL) {
                stable = false;
                break;
            }
        }
        if (!stable) return;

        // cooldown anti-spam
        ZonedDateTime since = ZonedDateTime.now(ZONE).minusMinutes(COOLDOWN_MINUTES);
        if (alertRepository.existsOpenSince(vehicleId, since)) return;

        VehicleModel vehicleRef = em.getReference(VehicleModel.class, vehicleId);

        FuelTheftAlertModel alert = new FuelTheftAlertModel();
        alert.setVehicleModel(vehicleRef);
        alert.setDetectedAt(ZonedDateTime.now(ZONE));
        alert.setBaselineValue(baseline);
        alert.setCurrentValue(recent);
        alert.setDropValue(drop);
        alert.setStatus("OPEN");
        alert.setEvidence("last10=" + clean);

        alertRepository.save(alert);

        log.warn("[FUEL_THEFT] vehicle={} baseline={} recent={} drop={}", vehicleId, baseline, recent, drop);
    }

    private static double median(List<Double> list) {
        List<Double> a = new ArrayList<>(list);
        a.sort(Double::compareTo);
        int m = a.size() / 2;
        return (a.size() % 2 == 1) ? a.get(m) : (a.get(m - 1) + a.get(m)) / 2.0;
    }
}
