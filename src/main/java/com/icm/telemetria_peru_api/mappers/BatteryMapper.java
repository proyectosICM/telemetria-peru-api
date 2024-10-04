package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.dto.CompanyDTO;
import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.models.BatteryModel;
import org.springframework.stereotype.Component;

@Component
public class BatteryMapper {
    /** Maps the BatteryModel data to BatteryDTO. **/
    public BatteryDTO mapToDTO(BatteryModel batteryModel) {
        VehicleDTO vehicleDTO = new VehicleDTO(batteryModel.getVehicleModel().getId(),
                batteryModel.getVehicleModel().getLicensePlate());
        CompanyDTO companyDTO = new CompanyDTO(batteryModel.getCompanyModel().getId(),
                batteryModel.getCompanyModel().getName());
        return new BatteryDTO(batteryModel.getId(), batteryModel.getName(), vehicleDTO, companyDTO);
    }
}
