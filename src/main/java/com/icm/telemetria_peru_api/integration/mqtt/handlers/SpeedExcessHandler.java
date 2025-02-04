package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
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
     * Logs an entry when the vehicle exceeds its maximum speed.
     * It checks if the vehicle's speed exceeds the defined limit and, if so,
     * records a new entry in the SpeedExcessLogger.
     *
     * @param vehicleId the ID of the vehicle being checked
     * @param speed     the current speed of the vehicle
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
            // Log the exception (you can use a logger like Logback or SLF4J)
            System.err.println("Error processing speed excess log: " + e.getMessage());
            e.printStackTrace(); // Optionally print the full stack trace for debugging
        }

    }
}
