package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.BatteryRecordDTO;
import com.icm.telemetria_peru_api.mappers.BatteryRecordMapper;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import com.icm.telemetria_peru_api.repositories.BatteryRecordRepository;
import com.icm.telemetria_peru_api.utils.DateUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatteryRecordService {
    private final BatteryRecordMapper batteryRecordMapper;
    private final BatteryRecordRepository batteryRecordRepository;
    private final DateUtils dateUtils;

    public BatteryRecordDTO findById(Long id){
        BatteryRecordModel batteryRecordModel = batteryRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
        return batteryRecordMapper.mapToDTO(batteryRecordModel);
    }

    public List<BatteryRecordDTO> findAll(){
        List<BatteryRecordModel> batteryRecordModel = batteryRecordRepository.findAll();
        return batteryRecordModel.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
    }

    public Page<BatteryRecordDTO> findAll(Pageable pageable){
        Page<BatteryRecordModel> batteryRecordModelsPage = batteryRecordRepository.findAll(pageable);
        List<BatteryRecordDTO> batteryRecordDTOs = batteryRecordModelsPage.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryRecordDTOs, pageable, batteryRecordModelsPage.getTotalElements());
    }

    public List<BatteryRecordDTO> findByBatteryId(Long batteryId){
        List<BatteryRecordModel> batteryRecordModels = batteryRecordRepository.findByBatteryModelId(batteryId);
        return batteryRecordModels.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
    }

    public Page<BatteryRecordDTO> findByBatteryId(Long batteryId, Pageable pageable){
        Page<BatteryRecordModel> batteryRecordModelsPage = batteryRecordRepository.findByBatteryModelId(batteryId, pageable);
        List<BatteryRecordDTO> batteryRecordDTOs = batteryRecordModelsPage.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryRecordDTOs, pageable, batteryRecordModelsPage.getTotalElements());
    }

    public List<BatteryRecordDTO> findByBatteryModelVehicleModelId(Long vehicleId){
        List<BatteryRecordModel> batteryRecordModels = batteryRecordRepository.findByBatteryModelVehicleModelId(vehicleId);
        return batteryRecordModels.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
    }

    public Page<BatteryRecordDTO> findByBatteryModelVehicleModelId(Long vehicleId, Pageable pageable){
        Page<BatteryRecordModel> batteryRecordModelsPage = batteryRecordRepository.findByBatteryModelVehicleModelId(vehicleId, pageable);
        List<BatteryRecordDTO> batteryRecordDTOs = batteryRecordModelsPage.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryRecordDTOs, pageable, batteryRecordModelsPage.getTotalElements());
    }

    public List<BatteryRecordDTO> findByBatteryModelVehicleModelIdAndBatteryModelId(Long vehicleId, Long batteryId){
        List<BatteryRecordModel> batteryRecordModels = batteryRecordRepository.findByBatteryModelVehicleModelIdAndBatteryModelId(vehicleId, batteryId);
        return batteryRecordModels.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
    }

    public Page<BatteryRecordDTO> findByBatteryModelVehicleModelIdAndBatteryModelId(Long vehicleId, Long batteryId, Pageable pageable){
        Page<BatteryRecordModel> batteryRecordModelsPage = batteryRecordRepository.findByBatteryModelVehicleModelIdAndBatteryModelId(vehicleId, batteryId, pageable);
        List<BatteryRecordDTO> batteryRecordDTOs = batteryRecordModelsPage.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryRecordDTOs, pageable, batteryRecordModelsPage.getTotalElements());
    }

    public List<Map<String, Object>> getDataMonth(Long vehicleId, Integer year, Integer month) {
        List<Map<String, Object>> timestamps = dateUtils.getMonthTimestamps(year, month);
        long startTimestampSeconds = (long) timestamps.get(0).get("startTimestamp");
        long endTimestampSeconds = (long) timestamps.get(0).get("endTimestamp");

        // Convertir los timestamps de segundos a ZonedDateTime en la zona horaria adecuada
        ZonedDateTime startTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(startTimestampSeconds), ZoneId.of("America/Lima"));
        ZonedDateTime endTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(endTimestampSeconds), ZoneId.of("America/Lima"));

        // Llamar a findByVehicleModelIdAndCreatedAtBetween para obtener los datos en el rango de tiempo
        List<BatteryRecordModel> records = batteryRecordRepository.findByBatteryModelVehicleModelIdAndCreatedAtBetween(vehicleId, startTimestamp, endTimestamp);

        // Agrupar los registros por día y calcular los promedios de voltaje y corriente
        Map<LocalDate, Map<String, Double>> groupedByDay = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreatedAt()
                                .withZoneSameInstant(ZoneId.of("America/Lima"))
                                .toLocalDate(), // Agrupar por día
                        TreeMap::new, // Ordenar por fechas
                        Collectors.collectingAndThen(
                                Collectors.toList(), // Agrupar registros de un día
                                dayRecords -> {
                                    double averageVoltage = dayRecords.stream()
                                            .filter(r -> r.getVoltage() != null)
                                            .mapToDouble(BatteryRecordModel::getVoltage)
                                            .average()
                                            .orElse(0.0);
                                    double averageCurrent = dayRecords.stream()
                                            .filter(r -> r.getCurrent() != null)
                                            .mapToDouble(BatteryRecordModel::getCurrent)
                                            .average()
                                            .orElse(0.0);
                                    // Retornar un mapa con ambos promedios
                                    Map<String, Double> averages = new HashMap<>();
                                    averages.put("averageVoltage", averageVoltage);
                                    averages.put("averageCurrent", averageCurrent);
                                    return averages;
                                }
                        )
                ));

        // Transformar el resultado en la estructura deseada
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Double>> entry : groupedByDay.entrySet()) {
            Map<String, Object> result = new HashMap<>();
            result.put("day", entry.getKey().atStartOfDay(ZoneId.of("America/Lima")).toEpochSecond());
            result.putAll(entry.getValue()); // Agregar los promedios al resultado
            results.add(result);
        }

        return results;
    }

    public BatteryRecordModel save(BatteryRecordModel batteryRecordModel){
        return batteryRecordRepository.save(batteryRecordModel);
    }
}