package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GasRecordRepository extends JpaRepository<GasRecordModel, Long> {
    List<GasRecordModel> findByVehicleModelId(Long vehicleId);
    Page<GasRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
}
