package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.models.BatteryModel;
import org.springframework.stereotype.Component;

@Component
public class BatteryMapper {
    /**
     * Maps the BatteryModel data to BatteryDTO.
     **/
    public BatteryDTO mapToDTO(BatteryModel batteryModel) {
        Long vehicleId = batteryModel.getVehicleModel().getId();
        String licensePlate = batteryModel.getVehicleModel().getLicensePlate();

        Long companyId = batteryModel.getCompanyModel().getId();
        String companyName = batteryModel.getCompanyModel().getName();

        return new BatteryDTO(batteryModel.getId(), batteryModel.getName(), vehicleId, licensePlate, companyId, companyName);
    }
}
