package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.AlternatorDTO;
import com.icm.telemetria_peru_api.mappers.AlternatorMapper;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.repositories.AlternatorRepository;
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
public class AlternatorService {
    private final AlternatorRepository alternatorRepository;
    private final AlternatorMapper alternatorMapper;
    private final DateUtils dateUtils;

    public List<AlternatorDTO> findAll(){
        List<AlternatorModel> alternatorModels =  alternatorRepository.findAll();
        return alternatorModels.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
    }

    public Page<AlternatorDTO> findAll(Pageable pageable){
        Page<AlternatorModel> alternatorPage = alternatorRepository.findAll(pageable);
        List<AlternatorDTO> alternatorDTOS = alternatorPage.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
        return new PageImpl<>(alternatorDTOS, pageable, alternatorPage.getTotalElements());
    }

    public List<AlternatorDTO> findByVehicleModelId(Long vehicleId) {
        List<AlternatorModel> alternatorModels =  alternatorRepository.findByVehicleModelId(vehicleId);
        return alternatorModels.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
    }

    public Page<AlternatorDTO> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        Page<AlternatorModel> alternatorModelPage =  alternatorRepository.findByVehicleModelId(vehicleId, pageable);
        List<AlternatorDTO> alternatorDTOs = alternatorModelPage.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
        return new PageImpl<>(alternatorDTOs, pageable, alternatorModelPage.getTotalElements());
    }

    public List<Map<String, Object>> getDataMonth(Long vehicleId, Integer year, Integer month){
        List<Map<String, Object>> timestamps = dateUtils.getMonthTimestamps(year, month);
        long startTimestampSeconds = (long) timestamps.get(0).get("startTimestamp");
        long endTimestampSeconds = (long) timestamps.get(0).get("endTimestamp");

        // Convertir los timestamps de segundos a ZonedDateTime en la zona horaria adecuada
        ZonedDateTime startTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(startTimestampSeconds), ZoneId.of("America/Lima"));
        ZonedDateTime endTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(endTimestampSeconds), ZoneId.of("America/Lima"));

        // Llamar a findByVehicleModelIdAndCreatedAtBetween para obtener los datos en el rango de tiempo
        List<AlternatorModel> records = alternatorRepository.findByVehicleModelIdAndCreatedAtBetween(vehicleId, startTimestamp, endTimestamp);

        // Agrupar los registros por día y contar los que tienen estado 'true'
        Map<LocalDate, Long> groupedByDay = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getCreatedAt()
                                .withZoneSameInstant(ZoneId.of("America/Lima")) // Ajustar la zona horaria correctamente
                                .toLocalDate(), // Convertir a LocalDate para agrupar por día
                        TreeMap::new, // Mantener ordenado por fechas (orden natural)
                        Collectors.counting() // Contar los registros en cada día
                ));

        // Transformar el resultado en la estructura deseada
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> entry : groupedByDay.entrySet()) {
            Map<String, Object> result = new HashMap<>();
            result.put("day", entry.getKey().atStartOfDay(ZoneId.of("America/Lima")).toEpochSecond()); // Timestamp del día
            result.put("counts", entry.getValue()); // Cantidad de registros con estado 'true'
            results.add(result);
        }

        return results;
    }

    public AlternatorModel save(AlternatorModel alternatorModel){
        return alternatorRepository.save(alternatorModel);
    }
}
