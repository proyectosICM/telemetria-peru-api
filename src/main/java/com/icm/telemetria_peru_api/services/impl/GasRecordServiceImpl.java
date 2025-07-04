package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.repositories.GasRecordRepository;
import com.icm.telemetria_peru_api.services.GasRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GasRecordServiceImpl implements GasRecordService {
    private final GasRecordRepository gasRecordRepository;

    @Override
    public Optional<GasRecordModel> findById(Long id) {
        return gasRecordRepository.findById(id);
    }

    @Override
    public List<GasRecordModel> findByVehicleId(Long vehicleId){
        return gasRecordRepository.findByVehicleModelId(vehicleId);
    }

    @Override
    public Page<GasRecordModel> findByVehicleId(Long vehicleId, Pageable pageable) {
        return gasRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    @Override
    public List<GasRecordModel> findByVehicleIdOrdered(Long vehicleId) {
        return gasRecordRepository.findByVehicleModelIdOrderByCreatedAtDesc(vehicleId);
    }

    @Override
    public List<GasRecordModel> findTodayByVehicleId(Long vehicleId) {
        ZonedDateTime startOfDay = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return gasRecordRepository.findByVehicleModelIdAndCreatedAtBetweenOrderByCreatedAtAsc(
                vehicleId, startOfDay, endOfDay
        );
    }

    @Override
    public List<GasRecordModel> findByVehicleIdAndDate(Long vehicleId, String viewType, int year, Integer month, Integer day) {
        ZonedDateTime start;
        ZonedDateTime end;

        ZoneId zone = ZoneId.systemDefault();

        switch (viewType.toLowerCase()) {
            case "day":
                if (month == null || day == null)
                    throw new IllegalArgumentException("Mes y día son requeridos para vista diaria.");
                start = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, zone);
                end = start.plusDays(1).minusNanos(1);
                break;

            case "month":
                if (month == null)
                    throw new IllegalArgumentException("Mes es requerido para vista mensual.");
                start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, zone);
                end = start.plusMonths(1).minusNanos(1);
                break;

            case "year":
                start = ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, zone);
                end = start.plusYears(1).minusNanos(1);
                break;

            default:
                throw new IllegalArgumentException("Tipo de vista no válido: " + viewType);
        }

        return gasRecordRepository.findByVehicleModelIdAndCreatedAtBetweenOrderByCreatedAtAsc(vehicleId, start, end);
    }

    @Override
    public GasRecordModel save(GasRecordModel gasRecordModel) {
        return gasRecordRepository.save(gasRecordModel);
    }

    @Override
    public void deleteById(Long id) {
        gasRecordRepository.deleteById(id);
    }
}
