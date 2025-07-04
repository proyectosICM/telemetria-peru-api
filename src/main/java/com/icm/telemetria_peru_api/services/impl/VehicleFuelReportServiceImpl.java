package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.FuelReportSummaryDTORecord;
import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.repositories.VehicleFuelReportRepositpory;
import com.icm.telemetria_peru_api.services.VehicleFuelReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleFuelReportServiceImpl implements VehicleFuelReportService {
    private final VehicleFuelReportRepositpory vehicleFuelReportRepositpory;

    @Override
    public VehicleFuelReportModel findById(Long id){
        return vehicleFuelReportRepositpory.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + "not found"));
    }

    @Override
    public List<VehicleFuelReportModel> findAll() {
        return vehicleFuelReportRepositpory.findAll();
    }

    @Override
    public Page<VehicleFuelReportModel> findAll(Pageable pageable){
        return vehicleFuelReportRepositpory.findAll(pageable);
    }

    @Override
    public List<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId){
        return vehicleFuelReportRepositpory.findByVehicleModelId(vehicleId);
    }

    @Override
    public Page<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId, Pageable pageable){
        return vehicleFuelReportRepositpory.findByVehicleModelId(vehicleId, pageable);
    }

    @Override
    public FuelReportSummaryDTORecord getSummary(Long vehicleId, Integer year, Integer month, Integer day) {
        Object result = vehicleFuelReportRepositpory.findFuelReportSummaryRaw(vehicleId, year, month, day);
        if (result == null) return null;

        Object[] row = (Object[]) result;

        Double averageFuel = row[0] != null ? ((Number) row[0]).doubleValue() : null;
        Duration idle = row[1] != null ? Duration.ofSeconds(((Number) row[1]).longValue()) : null;
        Duration parked = row[2] != null ? Duration.ofSeconds(((Number) row[2]).longValue()) : null;
        Duration operating = row[3] != null ? Duration.ofSeconds(((Number) row[3]).longValue()) : null;

        return new FuelReportSummaryDTORecord(averageFuel, idle, parked, operating);
    }

    @Override
    public VehicleFuelReportModel save(VehicleFuelReportModel vehicleFuelReportModel){
        return vehicleFuelReportRepositpory.save(vehicleFuelReportModel);
    }

    @Override
    public VehicleFuelReportModel update(Long id, VehicleFuelReportModel vehicleFuelReportModel){
        VehicleFuelReportModel existing = findById(id);

        return vehicleFuelReportRepositpory.save(existing);
    }

    @Override
    public void deleteById(Long id){
        vehicleFuelReportRepositpory.deleteById(id);
    }
}
