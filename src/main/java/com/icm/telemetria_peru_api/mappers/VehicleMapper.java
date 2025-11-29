package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {
    public VehicleDTO mapToDTO(VehicleModel vehicleModel) {
        Long vehicleTypeId = vehicleModel.getVehicletypeModel().getId();
        String vehicleTypeName = vehicleModel.getVehicletypeModel().getName();

        Long companyId = vehicleModel.getCompanyModel().getId();
        String companyName = vehicleModel.getCompanyModel().getName();

        return new VehicleDTO(vehicleModel.getId(), vehicleModel.getLicensePlate(), vehicleModel.getStatus(), vehicleModel.getImei(),
                vehicleTypeId, vehicleTypeName, companyId, companyName, vehicleModel.getMaxSpeed(), vehicleModel.getFuelType().toString(), vehicleModel.getDvrPhone(),
                vehicleModel.getVideoChannels());
    }
}
