package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.VehicleIgnitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IgnitionHandler {
    private final VehicleIgnitionRepository vehicleIgnitionRepository;

    /**
     * Handles ignition status updates by checking the current ignition state
     * and comparing it with the last recorded state. If the state has changed
     * or there are no previous records, a new ignition record is saved.
     *
     * @param vehicleModel  the vehicle model associated with the ignition status
     * @param currentStatus the current ignition status (true for on, false for off)
     */
    public void updateIgnitionStatus(VehicleModel vehicleModel, Boolean currentStatus) {
        if (currentStatus == null) {
            return;
        }

        VehicleIgnitionModel lastRecord = vehicleIgnitionRepository.findTopByVehicleModelOrderByCreatedAtDesc(vehicleModel);

        if (lastRecord == null || !lastRecord.getStatus().equals(currentStatus)) {
            VehicleIgnitionModel newRecord = new VehicleIgnitionModel();
            newRecord.setVehicleModel(vehicleModel);
            newRecord.setStatus(currentStatus);
            vehicleIgnitionRepository.save(newRecord);
        }
    }
}
