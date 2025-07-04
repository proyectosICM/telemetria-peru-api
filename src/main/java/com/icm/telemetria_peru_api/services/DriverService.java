package com.icm.telemetria_peru_api.services;


import com.icm.telemetria_peru_api.models.DriverModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DriverService {
    // Get a driver by ID
    Optional<DriverModel> findById(Long driverId);

    // List all drivers
    List<DriverModel> findAll();
    Page<DriverModel> findAll(Pageable pageable);

    // List by state
    List<DriverModel> findByStatus(Boolean status);
    Page<DriverModel> findByStatus(Boolean status, Pageable pageable);

    // List by company
    List<DriverModel> findByCompanyModelId(Long companyId);
    Page<DriverModel> findByCompanyModelId(Long companyId, Pageable pageable);

    // List by company and status
    List<DriverModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status);
    Page<DriverModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status, Pageable pageable);

    // Save new driver
    DriverModel save(DriverModel driverModel);

    // Update main data
    DriverModel updateMainData(Long driverId, DriverModel driverModel);

    // Update RFID
    DriverModel updateRFID(Long driverId, String newRFId);

    // Change status
    DriverModel statusToggle(Long driverId);
}
