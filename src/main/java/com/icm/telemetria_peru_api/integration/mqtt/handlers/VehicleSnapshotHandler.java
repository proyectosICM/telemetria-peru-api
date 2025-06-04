package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.dto.VehicleSnapshotDTO;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleSnapshotHandler {
    private final MqttMessagePublisher mqttMessagePublisher;
    public void saveVehicleSnapshot(VehicleSnapshotDTO data, VehicleModel vehicle) {
        VehicleSnapshotModel snapshot = new VehicleSnapshotModel();
        snapshot.getVehicleModel().setId(vehicle.getId());
        snapshot.setCompanyModel(vehicle.getCompanyModel());
        snapshot.setSnapshotLatitude(data.getSnapshotLatitude());
        snapshot.setSnapshotLongitude(data.getSnapshotLongitude());
        snapshot.setVehicleModel(vehicle);

        mqttMessagePublisher.snapshot(vehicle.getId(), snapshot);
    }
}
