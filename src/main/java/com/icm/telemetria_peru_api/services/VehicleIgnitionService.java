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

    public Map<String, Object> getCountsByDay(Long vehicleId) {
        List<Map<String, Object>> counts = vehicleIgnitionRepository.counts(vehicleId);

        if (counts.isEmpty()) {
            throw new RuntimeException("No se encontraron igniciones para el día de hoy.");
        }

        // Retornamos el primer registro, ya que debería haber solo un día actual.
        return counts.get(0);
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
