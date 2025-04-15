package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.VehicleFuelReportRepositpory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FuelReportHandler {
    private final VehicleFuelReportRepositpory vehicleFuelReportRepositpory;

    public void saveFuelReport(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        try {
            Optional<VehicleFuelReportModel> optionalLast = vehicleFuelReportRepositpory
                    .findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

            if (optionalLast.isEmpty()){
                VehicleFuelReportModel newReport = new VehicleFuelReportModel();
                newReport.setVehicleModel(vehicleModel);
                newReport.setCurrentFuelDetected(data.getFuelInfo());
                newReport.setInitialFuel(data.getFuelInfo());
                newReport.setIdleTime(Duration.ZERO);
                newReport.setParkedTime(Duration.ZERO);
                newReport.setOperatingTime(Duration.ZERO);
                vehicleFuelReportRepositpory.save(newReport);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Saving Fuel Report");
        }
    }

