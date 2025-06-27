package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface GasRecordRepository extends JpaRepository<GasRecordModel, Long> {
    List<GasRecordModel> findByVehicleModelId(Long vehicleId);
    Page<GasRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<GasRecordModel> findByVehicleModelIdOrderByCreatedAtDesc(Long vehicleId);
    List<GasRecordModel> findByVehicleModelIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long vehicleId,
            ZonedDateTime startOfDay,
            ZonedDateTime endOfDay
    );
    GasRecordModel findTopByVehicleModelIdOrderByCreatedAtDesc(Long vehicleId);


}
