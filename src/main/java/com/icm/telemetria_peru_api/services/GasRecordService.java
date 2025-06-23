package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.repositories.GasRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GasRecordService {
    private final GasRecordRepository gasRecordRepository;

    public Optional<GasRecordModel> findById(Long id) {
        return gasRecordRepository.findById(id);
    }

    public List<GasRecordModel> findByVehicleId(Long vehicleId){
        return gasRecordRepository.findByVehicleModelId(vehicleId);
    }

    public Page<GasRecordModel> findByVehicleId(Long vehicleId, Pageable pageable) {
        return gasRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public List<GasRecordModel> findByVehicleIdOrdered(Long vehicleId) {
        return gasRecordRepository.findByVehicleModelIdOrderByCreatedAtDesc(vehicleId);
    }

    public List<GasRecordModel> findTodayByVehicleId(Long vehicleId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return gasRecordRepository.findByVehicleModelIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                vehicleId, startOfDay, endOfDay
        );
    }

    public GasRecordModel save(GasRecordModel gasRecordModel) {
        return gasRecordRepository.save(gasRecordModel);
    }

    public void deleteById(Long id) {
        gasRecordRepository.deleteById(id);
    }
}
