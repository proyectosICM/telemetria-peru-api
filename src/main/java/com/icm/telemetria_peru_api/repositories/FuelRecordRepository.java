package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelRecordRepository extends JpaRepository<FuelRecordModel, Long> {
    List<FuelRecordModel> findByVehicleModelId(Long vehicleId);
    Page<FuelRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
}
