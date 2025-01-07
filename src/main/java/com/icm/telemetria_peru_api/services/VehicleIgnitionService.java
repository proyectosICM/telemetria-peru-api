package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.IgnitionCountByDate;
import com.icm.telemetria_peru_api.dto.IgnitionDuration;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.repositories.AlarmRecordRepository;
import com.icm.telemetria_peru_api.repositories.VehicleIgnitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleIgnitionService {
    private final VehicleIgnitionRepository vehicleIgnitionRepository;

    public List<VehicleIgnitionModel> findAll(){
        return vehicleIgnitionRepository.findAll();
    }

    public Page<VehicleIgnitionModel> findAll(Pageable pageable){
        return vehicleIgnitionRepository.findAll(pageable);
    }

    public List<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId){
        return vehicleIgnitionRepository.findByVehicleModelId(vehicleId);
    }

    public Page<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId, Pageable pageable){
        return vehicleIgnitionRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public List<IgnitionDuration> calculateActiveDurations(Long vehicleId) {
        List<VehicleIgnitionModel> records = vehicleIgnitionRepository.findByVehicleModelIdOrderByCreatedAt(vehicleId);

        List<IgnitionDuration> durations = new ArrayList<>();
        ZonedDateTime lastStart = null;

        for (VehicleIgnitionModel record : records) {
            if (record.getStatus()) {
                // Encendido: guardar la hora de inicio
                lastStart = record.getCreatedAt();
            } else if (lastStart != null) {
                // Apagado: calcular la duración y agregar a la lista
                Duration duration = Duration.between(lastStart, record.getCreatedAt());
                long hours = duration.toHours();
                long minutes = duration.toMinutesPart();

                // Formatear duración como HH:MM
                String durationFormatted = String.format("%02d:%02d", hours, minutes);

                // Convertir duración a formato decimal
                double durationInDecimal = hours + (minutes / 60.0);

                durations.add(new IgnitionDuration(lastStart, record.getCreatedAt(), durationFormatted, durationInDecimal));
                lastStart = null;
            }
        }
        return durations;
    }

    public Map<String, Object> getCounts(Long vehicleId) {
        // Obtener los conteos para el día actual
        List<Map<String, Object>> countsDay = vehicleIgnitionRepository.countsDay(vehicleId);

        // Obtener los conteos para la última semana
        List<Map<String, Object>> countsWeek = vehicleIgnitionRepository.countsWeek(vehicleId);

        // Crear un mapa para devolver ambos resultados
        Map<String, Object> consolidatedData = new HashMap<>();

        // Verificar si se obtuvieron datos para el día actual
        if (!countsDay.isEmpty()) {
            Map<String, Object> dayData = countsDay.get(0);  // Se asume que solo habrá un registro para el día actual
            consolidatedData.put("day", Map.of("counts", dayData.get("count")));
        } else {
            consolidatedData.put("day", Map.of("counts", 0));
        }

        // Verificar si se obtuvieron datos para la última semana
        if (!countsWeek.isEmpty()) {
            // Sumar los conteos de los días de la última semana
            long weekCount = countsWeek.stream()
                    .mapToLong(item -> Long.parseLong(item.get("count").toString()))
                    .sum();
            consolidatedData.put("week", Map.of("counts", weekCount));
        } else {
            consolidatedData.put("week", Map.of("counts", 0));
        }

        return consolidatedData;
    }

    public List<IgnitionCountByDate> countIgnitionsByWeek(Long vehicleId) {
        List<Map<String, Object>> results = vehicleIgnitionRepository.countIgnitionsByWeek(vehicleId);

        return results.stream()
                .map(result -> new IgnitionCountByDate(
                        result.get("date").toString(),
                        Long.valueOf(result.get("count").toString())
                ))
                .collect(Collectors.toList());
    }

    public VehicleIgnitionModel save(VehicleIgnitionModel vehicleIgnitionModel){
        return vehicleIgnitionRepository.save(vehicleIgnitionModel);
    }

}
