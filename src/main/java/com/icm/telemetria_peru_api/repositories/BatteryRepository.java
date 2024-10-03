package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.BatteryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRepository extends JpaRepository<BatteryModel, Long> {
    List<BatteryModel> findByVehicleModelId(Long vehicleId);
    Page<BatteryModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<BatteryModel> findByCompanyModelId(Long companyId);
    Page<BatteryModel> findByCompanyModelId(Long companyId, Pageable pageable);
}
