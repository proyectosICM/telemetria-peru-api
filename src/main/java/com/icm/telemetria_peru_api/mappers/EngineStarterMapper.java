package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.EngineStarterDTO;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class EngineStarterMapper {
    public EngineStarterDTO mapToDTO(EngineStarterModel engineStarterModel){
        Long vehicleId = engineStarterModel.getVehicleModel().getId();
        String licensePlate = engineStarterModel.getVehicleModel().getLicensePlate();

        ZonedDateTime createdAt = engineStarterModel.getCreatedAt();

        return new EngineStarterDTO(engineStarterModel.getId(), engineStarterModel.getCurrent(), vehicleId, licensePlate, createdAt);
    }
}
