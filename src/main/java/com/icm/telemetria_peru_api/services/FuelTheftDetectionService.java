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
    private static final double DROP_THRESHOLD = 10.0;   // caída mínima para alertar
    private static final double STABILITY_TOL = 1.0;     // tolerancia para “se mantiene abajo”
    private static final int COOLDOWN_MINUTES = 30;      // evita spam (por mismo mensaje)
    private static final int MIN_POINTS = 8;             // mínimo puntos válidos

    // ===== Reglas anti-cero =====
    private static final int MAX_ZEROS_IN_WINDOW = 3;    // si hay >= 3 ceros en la ventana, ignora
    private static final int MAX_TRAILING_ZEROS = 2;     // si termina con >=2 ceros, ignora (glitch típico)

    @Transactional
    public void analyzeVehicle(Long vehicleId) {
        if (vehicleId == null) return;

        List<FuelRecordModel> rows =
                fuelRecordRepository.findTop10ByVehicleModel_IdOrderByCreatedAtDesc(vehicleId);

        if (rows.size() < MIN_POINTS) return;

        // vienen DESC, pasa a ASC
        Collections.reverse(rows);

        // extraer valores
        List<Double> values = new ArrayList<>();
        for (FuelRecordModel r : rows) {
            if (r.getValueData() != null) values.add(r.getValueData());
        }
        if (values.size() < MIN_POINTS) return;

        // ===== Anti-cero global =====
        long zerosCount = values.stream().filter(v -> v != null && v == 0.0).count();
        if (zerosCount >= MAX_ZEROS_IN_WINDOW) {
            // demasiados ceros en la ventana: sensores/mensaje raro
            return;
        }

        // ===== Ignorar si termina en ceros (0,0,0...) =====
        int trailingZeros = 0;
        for (int i = values.size() - 1; i >= 0; i--) {
            Double v = values.get(i);
            if (v != null && v == 0.0) trailingZeros++;
            else break;
        }
        if (trailingZeros >= MAX_TRAILING_ZEROS) {
            return;
        }

        // ===== filtro glitch 0: si 0 está entre dos valores >0, ignóralo =====
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
        if (clean.size() < MIN_POINTS) return;

        // baseline: primeros 5 (mediana)
        double baseline = median(clean.subList(0, Math.min(5, clean.size())));

        // recent: últimos 3 (mediana)
        int n = clean.size();
        double recent = median(clean.subList(n - 3, n));

        // ===== regla anti-cero adicional =====
        // si baseline o recent están muy cerca de 0, no tiene sentido alertar
        if (baseline <= 0.0 || recent <= 0.0) return;

        double drop = baseline - recent;
        if (drop < DROP_THRESHOLD) return;

        // persistencia: últimos 3 no “rebotan” (se mantienen cerca del recent)
        boolean stable = true;
        for (int i = n - 3; i < n; i++) {
            if (clean.get(i) > recent + STABILITY_TOL) {
                stable = false;
                break;
            }
        }
        if (!stable) return;

        // ===== Mensaje (lo que quieres ver en tu tabla) =====
        String message = "Caída brusca de combustible";

        // cooldown anti-spam por mensaje
        ZonedDateTime since = ZonedDateTime.now(ZONE).minusMinutes(COOLDOWN_MINUTES);
        if (alertRepository.existsMessageSince(vehicleId, message, since)) return;

        VehicleModel vehicleRef = em.getReference(VehicleModel.class, vehicleId);

        FuelTheftAlertModel alert = new FuelTheftAlertModel();
        alert.setVehicleModel(vehicleRef);
        alert.setDetectedAt(ZonedDateTime.now(ZONE));
        alert.setMessage(message);

        alertRepository.save(alert);

        log.warn("[FUEL_THEFT] vehicle={} baseline={} recent={} drop={} msg={}",
                vehicleId, baseline, recent, drop, message);
    }

    private static double median(List<Double> list) {
        List<Double> a = new ArrayList<>(list);
        a.sort(Double::compareTo);
        int m = a.size() / 2;
        return (a.size() % 2 == 1) ? a.get(m) : (a.get(m - 1) + a.get(m)) / 2.0;
    }
}
