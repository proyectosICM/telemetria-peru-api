package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.repositories.VehicleFuelReportRepositpory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleFuelReportService {
    private final VehicleFuelReportRepositpory vehicleFuelReportRepositpory;

    public VehicleFuelReportModel findById(Long id){
        return vehicleFuelReportRepositpory.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + "not found"));
    }

    public List<VehicleFuelReportModel> findAll() {
        return vehicleFuelReportRepositpory.findAll();
    }

    public Page<VehicleFuelReportModel> findAll(Pageable pageable){
        return vehicleFuelReportRepositpory.findAll(pageable);
    }

    public VehicleFuelReportModel save(VehicleFuelReportModel vehicleFuelReportModel){
        return vehicleFuelReportRepositpory.save(vehicleFuelReportModel);
    }

    public VehicleFuelReportModel update(Long id, VehicleFuelReportModel vehicleFuelReportModel){
        VehicleFuelReportModel existing = findById(id);

        return vehicleFuelReportRepositpory.save(existing);
    }

    public void deleteById(Long id){
        vehicleFuelReportRepositpory.deleteById(id);
    }
}
