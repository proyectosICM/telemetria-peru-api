package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class FuelRecordHandler {
    private final FuelRecordRepository fuelRecordRepository;
    private final VehicleRepository vehicleRepository;
    /**
     * Analyzes the received timestamp to determine if it falls within a specific interval
     * (the first 2 minutes of every tenth of the hour) and, if so, records a fuel data
     * entry associated with the vehicle.
     *
     * @param data         the DTO containing the fuel information and timestamp (in Unix format as a String)
     * @param vehicleModel the vehicle model to which the data belongs
     */
    public void analyzeFuelTimestamp(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        try {
            VehicleModel dataVehicle = vehicleRepository.findByImei(data.getImei()).orElse(null);

            if (data.getFuelInfo() == null && data.getTimestamp() == null) {
                return;
            }

            if (!dataVehicle.getFuelType().equals("DIESEL")) {
                return;
            }

            long unixTimestamp = Long.parseLong(data.getTimestamp());

            LocalTime time = Instant.ofEpochSecond(unixTimestamp).atZone(ZoneId.systemDefault()).toLocalTime();

            int minute = time.getMinute();
            if (minute % 10 >= 0 && minute  % 10 <= 2) {
                //System.out.println("Initial hour detected: " + time);
                FuelRecordModel fuelRecordModel = new FuelRecordModel();
                fuelRecordModel.setValueData(data.getFuelInfo());
                fuelRecordModel.setVehicleModel(vehicleModel);
                fuelRecordRepository.save(fuelRecordModel);
            }
        } catch (Exception e) {
            System.out.println("Error analyzing the timestamp: " + e.getMessage());
        }
    }
}
