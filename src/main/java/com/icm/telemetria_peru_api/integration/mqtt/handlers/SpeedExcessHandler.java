package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import com.icm.telemetria_peru_api.services.SpeedExcessLoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpeedExcessHandler {
    private final VehicleRepository vehicleRepository;
    private final SpeedExcessLoggerService speedExcessLoggerService;

    /**
     * Logs an entry when the vehicle exceeds its maximum allowed speed.
     * The method checks if the vehicle's current speed surpasses the defined speed limit.
     * If the speed is exceeded, a new entry is created and saved in the SpeedExcessLogger.
     *
     * @param vehicleModel the vehicle model to check for speed excess
     * @param jsonNode     the payload containing the current speed of the vehicle
     */
    public void logSpeedExcess(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode) {
        try{
            Optional<VehicleModel> vehicle = vehicleRepository.findById(vehicleModel.getId());
            if (vehicle.isPresent()) {
                if (vehicle.get().getMaxSpeed() < jsonNode.getSpeed()) {
                    SpeedExcessLoggerModel speedExcessLoggerModel = new SpeedExcessLoggerModel();
                    speedExcessLoggerModel.setDescription("Maximum speed exceeded at " + jsonNode.getSpeed() + " km/h");
                    speedExcessLoggerModel.setVehicleModel(vehicle.get());
                    speedExcessLoggerService.save(speedExcessLoggerModel);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing speed excess log: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
