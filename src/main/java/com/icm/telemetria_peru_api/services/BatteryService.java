package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.models.BatteryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BatteryService {
    BatteryDTO findById(Long id);
    List<BatteryDTO> findAll();
    Page<BatteryDTO> findAll(Pageable pageable);
    List<BatteryDTO> findByVehicleId(Long vehicleId);
    Page<BatteryDTO> findByVehicleId(Long vehicleId, Pageable pageable);
    List<BatteryDTO> findByCompanyId(Long companyId);
    Page<BatteryDTO> findByCompanyId(Long companyId, Pageable pageable);
    BatteryModel save(BatteryModel batteryModel);
    BatteryModel update(Long id, BatteryModel batteryModel);
    void deleteById(Long id);

}
