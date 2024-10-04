package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.dto.BatteryRecordDTO;
import com.icm.telemetria_peru_api.dto.CompanyDTO;
import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import org.springframework.stereotype.Component;

@Component
public class BatteryRecordMapper {
    /** Maps the BatteryRecord data to BatteryRecordDTO. **/
    public BatteryRecordDTO mapToDTO(BatteryRecordModel batteryRecordModel) {
        VehicleDTO vehicleDTO = new VehicleDTO(
                batteryRecordModel.getBatteryModel().getVehicleModel().getId(),
                batteryRecordModel.getBatteryModel().getVehicleModel().getLicensePlate()
        );

        CompanyDTO companyDTO = new CompanyDTO(
                batteryRecordModel.getBatteryModel().getCompanyModel().getId(),
                batteryRecordModel.getBatteryModel().getCompanyModel().getName()
        );

        BatteryDTO batteryDTO = new BatteryDTO(
                batteryRecordModel.getBatteryModel().getId(),
                batteryRecordModel.getBatteryModel().getName(),
                vehicleDTO,
                companyDTO
        );

        return new BatteryRecordDTO(
                batteryRecordModel.getId(),
                batteryRecordModel.getVoltage(),
                batteryDTO
        );
    }
}