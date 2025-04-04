package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FuelEfficiencyHandler {
    private final FuelEfficiencyRepository fuelEfficiencyRepository;
    private final FuelEfficiencyService fuelEfficiencyService;

    /**
     * Processes fuel efficiency information based on the data received.
     *
     * @param vehicleModel Model of the related vehicle.
     * @param jsonNode Vehicle telemetry data received via MQTT.
     */
    public void processFuelEfficiencyInfo(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode) {
        FuelEfficiencyStatus determinate = determinateStatus(jsonNode.getIgnitionInfo(), jsonNode.getSpeed());

        FuelEfficiencyModel lastRecord = fuelEfficiencyRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        if (lastRecord == null) {
            createNewFuelEfficiencyRecord(vehicleModel, jsonNode, determinate);
            return;
        }

        // Convertir el timestamp de jsonNode a LocalDate
        LocalDate currentRecordDate = Instant.ofEpochMilli(Long.parseLong(jsonNode.getTimestamp()))
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Convertir la fecha del último registro a LocalDate
        LocalDate lastRecordDate = lastRecord.getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
/*
        if (currentRecordDate.getDayOfMonth() != lastRecordDate.getDayOfMonth()) {
            closeLastRecord(lastRecord, jsonNode); // Cierra el registro actual
            createNewFuelEfficiencyRecord(vehicleModel, jsonNode, determinate); // Crea un nuevo registro
            retur
        }
*/
        if (lastRecord.getFuelEfficiencyStatus() != determinate) {
            closeLastRecord(lastRecord, jsonNode);
            createNewFuelEfficiencyRecord(vehicleModel, jsonNode, determinate);
        } else {
            addNewSpeedToRecord(lastRecord, jsonNode);
        }
    }

    /**
     * Determines fuel efficiency status based on ignition and speed.
     *
     * @param ignitionInfo Ignition status of the vehicle.
     * @param speed Vehicle speed in km/h.
     * @return The corresponding fuel efficiency status.
     */
    private FuelEfficiencyStatus determinateStatus(Boolean ignitionInfo, Double speed) {
        if (ignitionInfo == null && speed == 0) {
            return FuelEfficiencyStatus.ESTACIONADO;
        }

        if (ignitionInfo != null && ignitionInfo) {
            if (speed == 0) {
                return FuelEfficiencyStatus.RALENTI;
            } else if (speed > 0) {
                return FuelEfficiencyStatus.OPERACION;
            }
        }
        return FuelEfficiencyStatus.ESTACIONADO;
    }

    /**
     * Creates a new fuel efficiency record for a vehicle.
     *
     * @param vehicleModel Vehicle model associated with the record.
     * @param jsonNode Vehicle telemetry data including fuel information.
     * @param status Initial fuel efficiency status (e.g., OPERATION, RALENTI).
     */
    private void createNewFuelEfficiencyRecord(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode, FuelEfficiencyStatus status) {
        FuelEfficiencyModel newRecord = new FuelEfficiencyModel();
        newRecord.setFuelEfficiencyStatus(status);
        newRecord.setVehicleModel(vehicleModel);
        newRecord.setInitialFuel(jsonNode.getFuelInfo());
        fuelEfficiencyRepository.save(newRecord);
    }

    /**
     * Close a record by adding the final data and performing fuel efficiency calculations.
     *
     * @param lastRecord model of the last saved record
     * @param jsonNode Vehicle telemetry data including fuel information.
     */
    private void closeLastRecord(FuelEfficiencyModel lastRecord, VehiclePayloadMqttDTO jsonNode) {
        if(jsonNode.getFuelInfo() > lastRecord.getInitialFuel()){
            lastRecord.setIsVisible(false);
        } else {
            lastRecord.setIsVisible(true);
        }

        lastRecord.setEndTime(ZonedDateTime.now());
        lastRecord.setFinalFuel(jsonNode.getFuelInfo());
        lastRecord.setCoordinates(jsonNode.getCoordinates());

        double accumulatedHours = calculateElapsedTimeInHours(lastRecord.getStartTime(), ZonedDateTime.now());
        lastRecord.setAccumulatedHours(accumulatedHours);
        calculateDistanceAndEfficiency(lastRecord, accumulatedHours, jsonNode.getFuelInfo());
        calculateEfficiencyByHour(lastRecord, accumulatedHours, jsonNode.getFuelInfo());
        fuelEfficiencyRepository.save(lastRecord);

        fuelEfficiencyService.deleteInvisibleRecords();
    }

    /**
     * Adds a new speed value to the last fuel efficiency record if valid.
     *
     * @param lastRecord The last fuel efficiency record to update.
     * @param jsonNode Vehicle telemetry data including the speed.
     */
    private void addNewSpeedToRecord(FuelEfficiencyModel lastRecord, VehiclePayloadMqttDTO jsonNode) {
        if (jsonNode.getSpeed() != null && jsonNode.getSpeed() >= 0.2) {
            if (lastRecord.getSpeeds() == null) {
                lastRecord.setSpeeds(new ArrayList<>());
            }
            lastRecord.getSpeeds().add(jsonNode.getSpeed());
            fuelEfficiencyRepository.save(lastRecord);
        }
    }

    private double calculateElapsedTimeInHours(ZonedDateTime startTime, ZonedDateTime endTime) {
        if (startTime != null && endTime != null) {
            long elapsedMillis = java.time.Duration.between(startTime, endTime).toMillis();
            return elapsedMillis / 3600000.0;  // Convertir milisegundos a horas
        }
        return 0.0; 
    }

    private void calculateDistanceAndEfficiency(FuelEfficiencyModel record, double hours, double finalFuel) {
        if(record.getFuelEfficiencyStatus() != FuelEfficiencyStatus.OPERACION){
            record.setFuelEfficiency(0.00);
            return;
        }

        List<Double> speeds = record.getSpeeds();
        if (speeds != null && !speeds.isEmpty()) {
            double totalSpeed = speeds.stream().mapToDouble(Double::doubleValue).sum();
            double averageSpeed = totalSpeed / speeds.size();
            double distance = averageSpeed * hours;
            record.setDistance(distance);

            double fuelUsed = record.getInitialFuel() - finalFuel;
            if (fuelUsed > 0) {
                double fuelEfficiency = distance / fuelUsed;
                record.setFuelEfficiency(fuelEfficiency);
            } else {
                record.setFuelEfficiency(0.0);
            }
        }
    }

    private void calculateEfficiencyByHour(FuelEfficiencyModel record, double hours, double finalFuel) {
        if(record.getFuelEfficiencyStatus() != FuelEfficiencyStatus.OPERACION){
            record.setFuelConsumptionPerHour(0.00);
            return;
        }

        if (hours > 0) {
            double fuelUsed = record.getInitialFuel() - finalFuel;
            if (fuelUsed > 0) {
                double efficiencyByHour = fuelUsed / hours;
                record.setFuelConsumptionPerHour(efficiencyByHour);
            } else {
                record.setFuelConsumptionPerHour(0.0);
            }
        } else {
            record.setFuelConsumptionPerHour(0.0);
        }
    }
}
