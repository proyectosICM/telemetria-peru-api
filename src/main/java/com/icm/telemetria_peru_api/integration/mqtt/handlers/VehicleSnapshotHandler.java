package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.dto.VehicleSnapshotDTO;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import com.icm.telemetria_peru_api.repositories.VehicleSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleSnapshotHandler {
    //private final MqttMessagePublisher mqttMessagePublisher;
    private final VehicleSnapshotRepository vehicleSnapshotRepository;
    public void saveVehicleSnapshot(VehicleSnapshotDTO data, VehicleModel vehicle) {
        VehicleSnapshotModel snapshot = new VehicleSnapshotModel();
        snapshot.setVehicleModel(vehicle);
        snapshot.setCompanyModel(vehicle.getCompanyModel());
        snapshot.setSnapshotLatitude(data.getSnapshotLatitude());
        snapshot.setSnapshotLongitude(data.getSnapshotLongitude());
        snapshot.setSnapshotSpeed(data.getSnapshotSpeed());

        vehicleSnapshotRepository.save(snapshot);
        System.out.println("Vehicle snapshot saved: " + snapshot.getId());
        //mqttMessagePublisher.snapshot(vehicle.getId(), snapshot);
    }
}
