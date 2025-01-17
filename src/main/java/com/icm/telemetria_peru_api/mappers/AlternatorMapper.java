package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.AlternatorDTO;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
@Component
public class AlternatorMapper {
    public AlternatorDTO mapToDTO(AlternatorModel alternatorModel){
        Long vehicleId = alternatorModel.getVehicleModel().getId();
        String licensePlate = alternatorModel.getVehicleModel().getLicensePlate();

        ZonedDateTime createdAt = alternatorModel.getCreatedAt();

        return new AlternatorDTO(alternatorModel.getId(), alternatorModel.getVoltage(), vehicleId, licensePlate, createdAt);
    }
}
