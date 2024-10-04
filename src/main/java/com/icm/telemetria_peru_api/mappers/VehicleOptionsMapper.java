package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.dto.VehicleOptionsDTO;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.stereotype.Component;

@Component
public class VehicleOptionsMapper {
    public VehicleOptionsDTO mapToDTO(VehicleModel vehicleModel) {


        return new VehicleOptionsDTO(vehicleModel.getId(), vehicleModel.getLicensePlate(),
                vehicleModel.getEngineStatus() ,vehicleModel.getAlarmStatus(), vehicleModel.getLockStatus());
    }
}
