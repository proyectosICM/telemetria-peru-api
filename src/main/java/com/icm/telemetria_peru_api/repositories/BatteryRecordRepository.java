package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRecordRepository extends JpaRepository<BatteryRecordModel, Long> {
    List<BatteryRecordModel> findByBatteryModelId(Long vehicleId);
    Page<BatteryRecordModel> findByBatteryModelId(Long vehicleId, Pageable pageable);
}
