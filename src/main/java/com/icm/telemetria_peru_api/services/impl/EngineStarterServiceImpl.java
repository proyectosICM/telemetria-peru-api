package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.dto.EngineStarterDTO;
import com.icm.telemetria_peru_api.mappers.EngineStarterMapper;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import com.icm.telemetria_peru_api.repositories.EngineStarterRepository;
import com.icm.telemetria_peru_api.services.EngineStarterService;
import com.icm.telemetria_peru_api.utils.DateUtils;
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
public class EngineStarterServiceImpl implements EngineStarterService {
    private final EngineStarterRepository engineStarterRepository;
    private final EngineStarterMapper engineStarterMapper;
    private final DateUtils dateUtils;

    @Override
    public List<EngineStarterDTO> findAll(){
        List<EngineStarterModel> engineStarterModels = engineStarterRepository.findAll();
        return engineStarterModels.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
    }

    @Override
    public Page<EngineStarterDTO> findAll(Pageable pageable){
        Page<EngineStarterModel> engineStarterModelPage = engineStarterRepository.findAll(pageable);
        List<EngineStarterDTO> engineStarterDTOs = engineStarterModelPage.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
        return new PageImpl<>(engineStarterDTOs, pageable, engineStarterModelPage.getTotalElements());
    }

    @Override
    public List<EngineStarterDTO> findByVehicleModelId(Long vehicleId) {
        List<EngineStarterModel> engineStarterModels = engineStarterRepository.findByVehicleModelId(vehicleId);
        return engineStarterModels.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
    }

    @Override
    public Page<EngineStarterDTO> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        Page<EngineStarterModel> engineStarterModels = engineStarterRepository.findByVehicleModelId(vehicleId, pageable);
        List<EngineStarterDTO> engineStarterDTOS = engineStarterModels.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
        return new PageImpl<>(engineStarterDTOS, pageable, engineStarterModels.getTotalElements());
    }

    @Override
    public List<Map<String, Object>> getDataMonth(Long vehicleId, Integer year, Integer month) {
        List<Map<String, Object>> timestamps = dateUtils.getMonthTimestamps(year, month);
        long startTimestampSeconds = (long) timestamps.get(0).get("startTimestamp");
        long endTimestampSeconds = (long) timestamps.get(0).get("endTimestamp");

        // Convertir los timestamps de segundos a ZonedDateTime en la zona horaria adecuada
        ZonedDateTime startTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(startTimestampSeconds), ZoneId.of("America/Lima"));
        ZonedDateTime endTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(endTimestampSeconds), ZoneId.of("America/Lima"));

        // Llamar a findByVehicleModelIdAndCreatedAtBetween para obtener los datos en el rango de tiempo
        List<EngineStarterModel> records = engineStarterRepository.findByVehicleModelIdAndCreatedAtBetween(vehicleId, startTimestamp, endTimestamp);

        // Agrupar los registros por día y calcular el promedio de voltaje
        Map<LocalDate, Double> groupedByDay = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreatedAt()
                                .withZoneSameInstant(ZoneId.of("America/Lima")) // Ajustar la zona horaria correctamente
                                .toLocalDate(), // Convertir a LocalDate para agrupar por día
                        TreeMap::new, // Mantener ordenado por fechas (orden natural)
                        Collectors.averagingDouble(record -> record.getCurrent() != null ? record.getCurrent() : 0.0)
                ));

        // Transformar el resultado en la estructura deseada
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map.Entry<LocalDate, Double> entry : groupedByDay.entrySet()) {
            Map<String, Object> result = new HashMap<>();
            result.put("day", entry.getKey().atStartOfDay(ZoneId.of("America/Lima")).toEpochSecond()); // Timestamp del día
            result.put("averageCurrent", entry.getValue()); // Promedio de voltaje
            results.add(result);
        }

        return results;
    }

    @Override
    public EngineStarterModel save(EngineStarterModel alternatorModel){
        return engineStarterRepository.save(alternatorModel);
    }
}
