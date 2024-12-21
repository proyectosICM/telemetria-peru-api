package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.FuelEfficiencyDTO;
import com.icm.telemetria_peru_api.dto.FuelEfficiencySummary;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelEfficiencyService {

    private final FuelEfficiencyRepository fuelEfficiencyRepository;
    private final MqttMessagePublisher mqttMessagePublisher;

    public Optional<FuelEfficiencyModel> findById(Long id) {
        return fuelEfficiencyRepository.findById(id);
    }

    public List<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId) {
        return fuelEfficiencyRepository.findByVehicleModelId(vehicleId);
    }

    public List<FuelEfficiencyDTO> findByVehicleModelId2(Long vehicleId) {
        List<FuelEfficiencyModel> records = fuelEfficiencyRepository.findByVehicleModelId(vehicleId);

        if (records.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron registros para el vehículo con ID " + vehicleId);
        }

        return records.stream()
                .map(FuelEfficiencyDTO::new)
                .toList();
    }

    public Page<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return fuelEfficiencyRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public Page<FuelEfficiencyDTO> findByVehicleModelId2(Long vehicleId, Pageable pageable) {
        Page<FuelEfficiencyModel> records = fuelEfficiencyRepository.findByVehicleModelId(vehicleId, pageable);

        if (records.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron registros para el vehículo con ID " + vehicleId);
        }

        return records.map(FuelEfficiencyDTO::new); // Usar el método map de Page
    }

    /**
     * STAST
     */

    public List<Map<String, Object>> getDailyAveragesForMonth(Long vehicleId, Integer month, Integer year) {
        return fuelEfficiencyRepository.findDailyAveragesForMonth(vehicleId, month, year);
    }

    public List<Map<String, Object>> getMonthlyAveragesForYear(Long vehicleId, String status, Integer year) {
        return fuelEfficiencyRepository.findMonthlyAveragesForYear(vehicleId, status, year);
    }


    public List<FuelEfficiencySummary> getFuelEfficiencyByVehicleAndTime(
            Long vehicleId, Integer year, Integer month, Integer day) {

        List<Object[]> results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(vehicleId, year, month, day);
        List<FuelEfficiencySummary> summaries = new ArrayList<>();

        for (Object[] row : results) {
            summaries.add(new FuelEfficiencySummary(
                    FuelEfficiencyStatus.valueOf((String) row[0]), // status
                    (Double) row[1], // totalHours
                    (Double) row[2], // totalFuelConsumed
                    (Double) row[3]  // avgFuelEfficiency
            ));
        }

        return summaries;
    }

    /**
     * STAST
     */

    public List<FuelEfficiencySummary> getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(Long vehicleId, Integer year, Integer month, Integer day) {
        List<Object[]> results;

        if (year != null && month == null && day == null) {
            results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByYear(vehicleId, year);
        } else if (year != null && month != null && day == null) {
            results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByMonth(vehicleId, month, year);
        } else if (year != null && month != null && day != null) {
            results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByDay(vehicleId, day, month, year);
        } else {
            results = null;  // O manejar el caso en que no se pasan filtros
        }

        // Mapeo manual de Object[] a FuelEfficiencySummary
        if (results != null && !results.isEmpty()) {
            List<FuelEfficiencySummary> summaries = results.stream().map(result -> {
                FuelEfficiencyStatus status = FuelEfficiencyStatus.valueOf(result[0].toString());
                Double totalHours = Math.max(0.0, Double.valueOf(result[1].toString()));  // Asegurarse de que no sea negativo
                Double totalFuelConsumed = Math.max(0.0, Double.valueOf(result[2].toString()));  // Asegurarse de que no sea negativo
                Double avgFuelEfficiency = Math.max(0.0, Double.valueOf(result[3].toString()));  // Asegurarse de que no sea negativo


                return new FuelEfficiencySummary(status, totalHours, totalFuelConsumed, avgFuelEfficiency);
            }).collect(Collectors.toList());

            return summaries;
        } else {
            // Si no hay resultados, devolver los 3 estados con valores en 0.0
            List<FuelEfficiencySummary> defaultSummary = new ArrayList<>();

            // Estado ESTACIONADO
            defaultSummary.add(new FuelEfficiencySummary(
                    FuelEfficiencyStatus.ESTACIONADO,  // Estado estacionado
                    0.0,  // totalHours
                    0.0,  // totalFuelConsumed
                    0.0   // avgFuelEfficiency
            ));

            // Estado OPERACION
            defaultSummary.add(new FuelEfficiencySummary(
                    FuelEfficiencyStatus.OPERACION,  // Estado operación
                    0.0,  // totalHours
                    0.0,  // totalFuelConsumed
                    0.0   // avgFuelEfficiency
            ));

            // Estado RALENTI
            defaultSummary.add(new FuelEfficiencySummary(
                    FuelEfficiencyStatus.RALENTI,  // Estado ralentí
                    0.0,  // totalHours
                    0.0,  // totalFuelConsumed
                    0.0   // avgFuelEfficiency
            ));

            return defaultSummary;
        }
    }

    public FuelEfficiencyModel save(FuelEfficiencyModel fuelEfficiencyModel) {

        FuelEfficiencyModel savedData = fuelEfficiencyRepository.save(fuelEfficiencyModel);
        if (savedData.getVehicleModel() != null) {
            mqttMessagePublisher.fuelEfficient(savedData.getId(), savedData.getVehicleModel().getId());
        } else {
            System.err.println("VehicleModel es nulo, no se puede enviar el mensaje MQTT.");
        }
        return savedData;
    }

    public FuelEfficiencyModel editEfficiency(Long id) {
        Optional<FuelEfficiencyModel> existing = fuelEfficiencyRepository.findById(id);
        if (existing.isPresent()) {
            FuelEfficiencyModel fuelEfficiencyModel = existing.get();
            //fuelEfficiencyModel.setFuelEfficiency(0.00);
            //fuelEfficiencyModel.setFuelConsumptionPerHour(0.00);
            //fuelEfficiencyModel.setAccumulatedHours();
            return fuelEfficiencyRepository.save(fuelEfficiencyModel);
        } else {
            throw new EntityNotFoundException("Registro con ID " + id + " no encontrado.");
        }
    }

    public List<FuelEfficiencyModel> resetNonOperationalEfficiencies() {
        // Obtener todos los registros cuyo estado no sea OPERACION
        List<FuelEfficiencyModel> nonOperationalRecords = fuelEfficiencyRepository.findByFuelEfficiencyStatusNot(FuelEfficiencyStatus.OPERACION);

        if (nonOperationalRecords.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron registros con estados diferentes a OPERACION.");
        }

        // Actualizar cada registro
        nonOperationalRecords.forEach(record -> {
            record.setFuelEfficiency(0.00);
            record.setFuelConsumptionPerHour(0.00);
        });

        // Guardar los cambios en la base de datos
        return fuelEfficiencyRepository.saveAll(nonOperationalRecords);
    }

    public void deleteById(Long id) {
        fuelEfficiencyRepository.deleteById(id);
    }
}
