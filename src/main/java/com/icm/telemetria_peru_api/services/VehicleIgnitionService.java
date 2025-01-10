package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.IgnitionDuration;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.repositories.VehicleIgnitionRepository;
import com.icm.telemetria_peru_api.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleIgnitionService {
    private final VehicleIgnitionRepository vehicleIgnitionRepository;
    private final DateUtils dateUtils;

    public Optional<VehicleIgnitionModel> findById(Long id) {
        return vehicleIgnitionRepository.findById(id);
    }

    public List<VehicleIgnitionModel> findAll(){ return vehicleIgnitionRepository.findAll(); }

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
    @Deprecated(since = "v1.2.1", forRemoval = false)
    public List<IgnitionDuration> calculateActiveDurations(Long vehicleId) {
        List<VehicleIgnitionModel> records = vehicleIgnitionRepository.findByVehicleModelIdOrderByCreatedAt(vehicleId);

        List<IgnitionDuration> durations = new ArrayList<>();
        ZonedDateTime lastStart = null;

        for (VehicleIgnitionModel record : records) {
            if (record.getStatus()) {
                lastStart = record.getCreatedAt();
            } else if (lastStart != null) {
                Duration duration = Duration.between(lastStart, record.getCreatedAt());
                long hours = duration.toHours();
                long minutes = duration.toMinutesPart();

                    String durationFormatted = String.format("%02d:%02d", hours, minutes);

                double durationInDecimal = hours + (minutes / 60.0);

                durations.add(new IgnitionDuration(lastStart, record.getCreatedAt(), durationFormatted, durationInDecimal));
                lastStart = null;
            }
        }
        return durations;
    }

    /**
     * Retrieves the ignition counts for a vehicle over different time periods.
     *
     * This method fetches and aggregates ignition event counts for a given vehicle
     * across day, week, month, and year. If no data is available for a period,
     * the count defaults to 0.
     *
     * @param vehicleId the ID of the vehicle to retrieve ignition counts for.
     * @return a map with ignition counts:
     *         - "day": Count for the current day.
     *         - "week": Sum of counts for the current week.
     *         - "month": Sum of counts for the current month.
     *         - "year": Sum of counts for the current year.
     */
    public Map<String, Object> getCounts(Long vehicleId) {
        List<Map<String, Object>> countsDay = vehicleIgnitionRepository.countsDay(vehicleId);
        List<Map<String, Object>> countsWeek = vehicleIgnitionRepository.countsWeek(vehicleId);
        List<Map<String, Object>> countsMonth = vehicleIgnitionRepository.countsMonth(vehicleId);
        List<Map<String, Object>> countsYear = vehicleIgnitionRepository.countsYear(vehicleId);

        // Create a map to store all results
        Map<String, Object> consolidatedData = new HashMap<>();

        // Auxiliary method for calculating the sum of counters
        consolidatedData.put("day", calculateCount(countsDay));
        consolidatedData.put("week", calculateCount(countsWeek));
        consolidatedData.put("month", calculateCount(countsMonth));
        consolidatedData.put("year", calculateCount(countsYear));

        return consolidatedData;
    }

    /**
     * Helper method to calculate the total ignition count from a list of count maps.
     *
     * This method takes a list of maps containing ignition counts and sums them up.
     * If there are no counts, it returns 0.
     *
     * @param counts a list of maps containing the ignition counts.
     * @return a map with the total ignition counts:
     *         - "counts": the aggregated total count for the provided period.
     */
    private Map<String, Object> calculateCount(List<Map<String, Object>> counts) {
        if (!counts.isEmpty()) {
            long totalCount = counts.stream()
                    .mapToLong(item -> Long.parseLong(item.get("count").toString()))
                    .sum();
            return Map.of("counts", totalCount);
        } else {
            return Map.of("counts", 0);
        }
    }

    /**
     * Retrives the ignition counts for all months for a specific vehicle.
     *
     * @param vehicleId the ID of the vehicle.
     * @param year      the year for which to retrieve the ignition count.
     * @return a list of ignition counts per month, or an empty list if no data is found.
     * */
    public List<Map<String, Object>> getIgnitionCountsByMonth(Long vehicleId, Integer year) {
        int yearToQuery = (year != null) ? year : Year.now().getValue();
        return vehicleIgnitionRepository.countsAllMonths(vehicleId, yearToQuery);
    }

    /**
     * Returns the ignition counts for specific days in a given month and year for a specified vehicle.
     *
     * @param vehicleId the ID of the vehicle.
     * @param year      the year for which to retrieve the ignition count.
     * @param month     the month for which to retrieve the ignition count.
     * @return a list of ignition counts for that month, or an empty list if no data is found.
     */
    public List<Map<String, Object>> getCountByMonth(Long vehicleId, Integer year, Integer month) {
        // Obtener los timestamps de inicio y fin del mes
        List<Map<String, Object>> timestamps = dateUtils.getMonthTimestamps(year, month);
        // Extraer los valores de los timestamps
        long startTimestampSeconds = (long) timestamps.get(0).get("startTimestamp");
        long endTimestampSeconds = (long) timestamps.get(0).get("endTimestamp");

        // Convertir los timestamps de segundos a ZonedDateTime en la zona horaria adecuada
        ZonedDateTime startTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(startTimestampSeconds), ZoneId.of("America/Lima"));
        ZonedDateTime endTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(endTimestampSeconds), ZoneId.of("America/Lima"));

        // Llamar a findByVehicleModelIdAndCreatedAtBetween para obtener los datos en el rango de tiempo
        List<VehicleIgnitionModel> records = vehicleIgnitionRepository.findByVehicleModelIdAndCreatedAtBetween(vehicleId, startTimestamp, endTimestamp);

        // Agrupar los registros por día y contar los que tienen estado 'true'
        Map<LocalDate, Long> groupedByDay = records.stream()
                .filter(record -> Boolean.TRUE.equals(record.getStatus())) // Filtrar solo los registros con estado 'true'
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

    public VehicleIgnitionModel save(VehicleIgnitionModel vehicleIgnitionModel){
        return vehicleIgnitionRepository.save(vehicleIgnitionModel);
    }
}
