package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.FuelReportSummaryDTORecord;
import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleFuelReportService {
    VehicleFuelReportModel findById(Long id);
    List<VehicleFuelReportModel> findAll();
    Page<VehicleFuelReportModel> findAll(Pageable pageable);
    List<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
    FuelReportSummaryDTORecord getSummary(Long vehicleId, Integer year, Integer month, Integer day);
    VehicleFuelReportModel save(VehicleFuelReportModel vehicleFuelReportModel);
    VehicleFuelReportModel update(Long id, VehicleFuelReportModel vehicleFuelReportModel);
    void deleteById(Long id);
}
