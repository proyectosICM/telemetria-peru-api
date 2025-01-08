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

    /**
     * Calculates the active durations (time intervals where the vehicle ignition was active)
     * for a given vehicle ID.
     *
     * This method processes ignition records to compute the duration of "ON" states.
     * The durations are formatted as HH:MM and also provided as decimal hours.
     *
     * @param vehicleId the ID of the vehicle whose ignition durations are to be calculated.
     * @return a list of {@link IgnitionDuration} objects representing the start time,
     *         end time, formatted duration, and decimal duration of each active period.
     *
     * @deprecated This method is no longer recommended due to performance issues
     *             when processing large datasets. Consider implementing a more
     *             efficient solution or a database query to handle this logic.
     */
    @Deprecated
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

    /**
     * Retrieves the consolidated ignition counts for a given vehicle across different time periods:
     * day, week, month, and year.
     * This method fetches ignition event counts for the current day, week, month, and year,
     * then consolidates them into a single map that is returned.
     *
     * The returned map contains keys for each time period ("day", "week", "month", "year")
     * with their respective ignition counts.
     * If no data is available for a given period, the count will be set to 0.
     *
     * @param vehicleId the ID of the vehicle for which the ignition counts are to be retrieved.
     * @return a map with consolidated ignition counts for the vehicle:
     *         - "day": A map containing the count for the current day.
     *         - "week": A map containing the sum of counts for the current week.
     *         - "month": A map containing the sum of counts for the current month.
     *         - "year": A map containing the sum of counts for the current year.
     */
    public Map<String, Object> getCounts(Long vehicleId) {
        List<Map<String, Object>> countsDay = vehicleIgnitionRepository.countsDay(vehicleId);
        List<Map<String, Object>> countsWeek = vehicleIgnitionRepository.countsWeek(vehicleId);
        List<Map<String, Object>> countsMonth = vehicleIgnitionRepository.countsMonth(vehicleId);
        List<Map<String, Object>> countsYear = vehicleIgnitionRepository.countsYear(vehicleId);

        // Create a map to return all results
        Map<String, Object> consolidatedData = new HashMap<>();

        // Día actual
        if (!countsDay.isEmpty()) {
            Map<String, Object> dayData = countsDay.get(0);
            consolidatedData.put("day", Map.of("counts", dayData.get("count")));
        } else {
            consolidatedData.put("day", Map.of("counts", 0));
        }

        // Semana
        if (!countsWeek.isEmpty()) {
            long weekCount = countsWeek.stream()
                    .mapToLong(item -> Long.parseLong(item.get("count").toString()))
                    .sum();
            consolidatedData.put("week", Map.of("counts", weekCount));
        } else {
            consolidatedData.put("week", Map.of("counts", 0));
        }

        // Mes
        if (!countsMonth.isEmpty()) {
            long monthCount = countsMonth.stream()
                    .mapToLong(item -> Long.parseLong(item.get("count").toString()))
                    .sum();
            consolidatedData.put("month", Map.of("counts", monthCount));
        } else {
            consolidatedData.put("month", Map.of("counts", 0));
        }

        // Año
        if (!countsYear.isEmpty()) {
            long yearCount = countsYear.stream()
                    .mapToLong(item -> Long.parseLong(item.get("count").toString()))
                    .sum();
            consolidatedData.put("year", Map.of("counts", yearCount));
        } else {
            consolidatedData.put("year", Map.of("counts", 0));
        }

        return consolidatedData;
    }

    public List<Map<String, Object>> getIgnitionCountsByMonth(Long vehicleId) {
        return vehicleIgnitionRepository.countsAllMonths(vehicleId);
    }

    public VehicleIgnitionModel save(VehicleIgnitionModel vehicleIgnitionModel){
        return vehicleIgnitionRepository.save(vehicleIgnitionModel);
    }
}
