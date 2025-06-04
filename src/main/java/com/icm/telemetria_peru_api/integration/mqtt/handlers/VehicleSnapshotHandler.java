package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.dto.VehicleSnapshotDTO;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import com.icm.telemetria_peru_api.repositories.VehicleSnapshotRepository;
import com.icm.telemetria_peru_api.services.VehicleSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleSnapshotHandler {
    //private final MqttMessagePublisher mqttMessagePublisher;
    private final VehicleSnapshotRepository vehicleSnapshotRepository;
    private final VehicleSnapshotService vehicleSnapshotService;
    public void saveVehicleSnapshot(VehicleSnapshotDTO data, VehicleModel vehicle) {
        VehicleSnapshotModel snapshot = new VehicleSnapshotModel();
        snapshot.setVehicleModel(vehicle);
        snapshot.setCompanyModel(vehicle.getCompanyModel());
        snapshot.setSnapshotIgnitionStatus(data.getIgnitionInfo());
        snapshot.setSnapshotAlarmStatus(data.getAlarmInfo());
        snapshot.setSnapshotLatitude(data.getSnapshotLatitude());
        snapshot.setSnapshotLongitude(data.getSnapshotLongitude());
        snapshot.setSnapshotSpeed(data.getSnapshotSpeed());
        snapshot.setSnapshotFuelLevel(data.getFuelInfo());

        VehicleSnapshotModel savedSnapshot = vehicleSnapshotService.updateOrCreateSnapshotByVehicleId(vehicle.getId(), snapshot);
        //vehicleSnapshotRepository.save(snapshot);
        //mqttMessagePublisher.snapshot(vehicle.getId(), snapshot);
    }
}
