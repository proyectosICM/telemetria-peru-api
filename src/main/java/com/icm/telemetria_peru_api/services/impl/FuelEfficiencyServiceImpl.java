package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import com.icm.telemetria_peru_api.repositories.projections.FuelEfficiencySumView;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;


@Service
@RequiredArgsConstructor
public class FuelEfficiencyServiceImpl implements FuelEfficiencyService {

    private final FuelEfficiencyRepository fuelEfficiencyRepository;
    private final VehicleRepository vehicleRepository;

    // ===== Helpers =====
    private static long nz(Long v) { return v == null ? 0L : v; }

    private VehicleModel requireVehicle(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + vehicleId));
    }

    private FuelEfficiencyModel requireDaily(Long vehicleId, LocalDate day) {
        return fuelEfficiencyRepository.findByVehicleModel_IdAndDay(vehicleId, day)
                .orElseThrow(() -> new EntityNotFoundException(
                        "FuelEfficiency daily row not found for vehicleId=" + vehicleId + " day=" + day
                ));
    }

    // ===== CRUD / Upsert =====

    @Override
    @Transactional
    public FuelEfficiencyModel upsertDaily(Long vehicleId, LocalDate day,
                                           Long parkedSeconds, Long idleSeconds, Long operationSeconds) {

        if (day == null) throw new IllegalArgumentException("day is required");

        VehicleModel vehicle = requireVehicle(vehicleId);

        Optional<FuelEfficiencyModel> existingOpt =
                fuelEfficiencyRepository.findByVehicleModel_IdAndDay(vehicleId, day);

        FuelEfficiencyModel row = existingOpt.orElseGet(FuelEfficiencyModel::new);
        row.setVehicleModel(vehicle);
        row.setDay(day);

        row.setParkedSeconds(nz(parkedSeconds));
        row.setIdleSeconds(nz(idleSeconds));
        row.setOperationSeconds(nz(operationSeconds));

        return fuelEfficiencyRepository.save(row);
    }

    @Override
    @Transactional
    public FuelEfficiencyModel addSeconds(Long vehicleId, LocalDate day,
                                          Long parkedSecondsDelta, Long idleSecondsDelta, Long operationSecondsDelta) {

        if (day == null) throw new IllegalArgumentException("day is required");

        VehicleModel vehicle = requireVehicle(vehicleId);

        FuelEfficiencyModel row =
                fuelEfficiencyRepository.findByVehicleModel_IdAndDay(vehicleId, day)
                        .orElseGet(() -> {
                            FuelEfficiencyModel r = new FuelEfficiencyModel();
                            r.setVehicleModel(vehicle);
                            r.setDay(day);
                            r.setParkedSeconds(0L);
                            r.setIdleSeconds(0L);
                            r.setOperationSeconds(0L);
                            return r;
                        });

        row.setParkedSeconds(row.getParkedSeconds() + nz(parkedSecondsDelta));
        row.setIdleSeconds(row.getIdleSeconds() + nz(idleSecondsDelta));
        row.setOperationSeconds(row.getOperationSeconds() + nz(operationSecondsDelta));

        // Evita negativos si por algún bug llega delta negativo
        if (row.getParkedSeconds() < 0) row.setParkedSeconds(0L);
        if (row.getIdleSeconds() < 0) row.setIdleSeconds(0L);
        if (row.getOperationSeconds() < 0) row.setOperationSeconds(0L);

        return fuelEfficiencyRepository.save(row);
    }

    @Override
    public FuelEfficiencyModel getById(Long id) {
        return fuelEfficiencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FuelEfficiency not found: " + id));
    }

    @Override
    public FuelEfficiencyModel getByVehicleAndDay(Long vehicleId, LocalDate day) {
        if (day == null) throw new IllegalArgumentException("day is required");
        return requireDaily(vehicleId, day);
    }

    @Override
    public List<FuelEfficiencyModel> listByDay(LocalDate day) {
        if (day == null) throw new IllegalArgumentException("day is required");
        return fuelEfficiencyRepository.findAllByDay(day);
    }

    @Override
    public List<FuelEfficiencyModel> listByVehicleAndDay(Long vehicleId, LocalDate day) {
        if (day == null) throw new IllegalArgumentException("day is required");
        return fuelEfficiencyRepository.findAllByVehicleModel_IdAndDay(vehicleId, day);
    }

    @Override
    public List<FuelEfficiencyModel> listByCompanyAndDay(Long companyId, LocalDate day) {
        if (day == null) throw new IllegalArgumentException("day is required");
        // ✅ FIX AQUÍ
        return fuelEfficiencyRepository.findAllByVehicleModel_CompanyModel_IdAndDay(companyId, day);
    }

    @Override
    public List<FuelEfficiencyModel> listByVehicleAndRange(Long vehicleId, LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end are required");
        if (end.isBefore(start)) throw new IllegalArgumentException("end must be >= start");
        return fuelEfficiencyRepository.findAllByVehicleModel_IdAndDayBetween(vehicleId, start, end);
    }

    @Override
    public List<FuelEfficiencyModel> listByCompanyAndRange(Long companyId, LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end are required");
        if (end.isBefore(start)) throw new IllegalArgumentException("end must be >= start");
        // ✅ FIX AQUÍ
        return fuelEfficiencyRepository.findAllByVehicleModel_CompanyModel_IdAndDayBetween(companyId, start, end);
    }

    @Override
    public Page<FuelEfficiencyModel> findAllByVehicleModel_IdAndDay(Long vehicleId, LocalDate day, Pageable pageable) {
        if (day == null) throw new IllegalArgumentException("day is required");

        // fuerza orden por day DESC si no viene sort
        Pageable p = pageable;
        if (p.getSort().isUnsorted()) {
            p = PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.by("day").descending());
        }

        return fuelEfficiencyRepository.findAllByVehicleModel_IdAndDay(vehicleId, day, p);
    }

    @Override
    public Page<FuelEfficiencyModel> findAllByVehicleModel_IdAndDayBetween(Long vehicleId, LocalDate start, LocalDate end, Pageable pageable) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end are required");
        if (end.isBefore(start)) throw new IllegalArgumentException("end must be >= start");

        Pageable p = pageable;
        if (p.getSort().isUnsorted()) {
            p = PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.by("day").descending());
        }

        return fuelEfficiencyRepository.findAllByVehicleModel_IdAndDayBetween(vehicleId, start, end, p);
    }

    @Override
    public Page<FuelEfficiencyModel> findAllByVehicleModel_CompanyModel_IdAndDay(Long companyId, LocalDate day, Pageable pageable) {
        if (day == null) throw new IllegalArgumentException("day is required");

        Pageable p = pageable;
        if (p.getSort().isUnsorted()) {
            p = PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.by("day").descending());
        }

        return fuelEfficiencyRepository.findAllByVehicleModel_CompanyModel_IdAndDay(companyId, day, p);
    }

    @Override
    public Page<FuelEfficiencyModel> findAllByVehicleModel_CompanyModel_IdAndDayBetween(Long companyId, LocalDate start, LocalDate end, Pageable pageable) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end are required");
        if (end.isBefore(start)) throw new IllegalArgumentException("end must be >= start");

        Pageable p = pageable;
        if (p.getSort().isUnsorted()) {
            p = PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.by("day").descending());
        }

        return fuelEfficiencyRepository.findAllByVehicleModel_CompanyModel_IdAndDayBetween(companyId, start, end, p);
    }

    @Override
    public FuelEfficiencySumView sumByVehicleAndRange(Long vehicleId, LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end are required");
        if (end.isBefore(start)) throw new IllegalArgumentException("end must be >= start");
        return fuelEfficiencyRepository.sumByVehicleAndRange(vehicleId, start, end);
    }

    @Override
    public FuelEfficiencySumView sumByCompanyAndRange(Long companyId, LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end are required");
        if (end.isBefore(start)) throw new IllegalArgumentException("end must be >= start");
        return fuelEfficiencyRepository.sumByCompanyAndRange(companyId, start, end);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!fuelEfficiencyRepository.existsById(id)) {
            throw new EntityNotFoundException("FuelEfficiency not found: " + id);
        }
        fuelEfficiencyRepository.deleteById(id);
    }
}
