package com.icm.telemetria_peru_api.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
@Component
public class DateUtils {
    public static List<Map<String, Object>> getDayTimeStamps(Integer day, Integer month, Integer year){
        int dayToQuery = (month != null) ? month : LocalDateTime.now().getDayOfMonth();
        int monthToQuery = (month != null) ? month : LocalDateTime.now().getMonthValue();
        int yearToQuery = (year != null) ? year : LocalDateTime.now().getYear();

        // Inicio del día (00:00:00)
        LocalDateTime startOfDay = LocalDateTime.of(yearToQuery, monthToQuery, dayToQuery, 0, 0, 0);
        ZonedDateTime startOfDayWithZone = startOfDay.atZone(ZoneId.of("America/Lima"));
        long startTimestamp = startOfDayWithZone.toEpochSecond();

        // Fin del día (23:59:59)
        LocalDateTime endOfDay = LocalDateTime.of(yearToQuery, monthToQuery, dayToQuery, 23, 59, 59);
        ZonedDateTime endOfDayWithZone = endOfDay.atZone(ZoneId.of("America/Lima"));
        long endTimestamp = endOfDayWithZone.toEpochSecond();


        // Retornar los timestamps en una lista con un mapa
        return List.of(
                Map.of("startTimestamp", startTimestamp, "endTimestamp", endTimestamp)
        );
    }

    /**
     * Returns a list containing a map with the start and end timestamps for the given month and year.
     *
     * <p> If no year or month is provided, the current year and month will be used by default. </p>
     *
     * @param year  the year for which timestamps are to be calculated (can be {@code null}).
     * @param month the month for which timestamps are to be calculated (can be {@code null}).
     * @return a list containing a single map with keys "startTimestamp" and "endTimestamp" representing the
     *         start and end timestamps of the month in seconds since epoch.
     */
    public static List<Map<String, Object>> getMonthTimestamps(Integer year, Integer month) {
        // Verificar si se proporcionaron mes y año, si no, usar el mes y año actual por defecto
        int yearToQuery = (year != null) ? year : LocalDateTime.now().getYear();
        int monthToQuery = (month != null) ? month : LocalDateTime.now().getMonthValue();

        // Calculamos el inicio del mes
        LocalDateTime startOfMonth = LocalDateTime.of(yearToQuery, monthToQuery, 1, 0, 0);
        ZonedDateTime startOfMonthWithZone = startOfMonth.atZone(ZoneId.of("America/Lima"));
        long startTimestamp = startOfMonthWithZone.toEpochSecond();

        // Calculamos el fin del mes
        LocalDateTime endOfMonth = LocalDateTime.of(yearToQuery, monthToQuery, startOfMonth.toLocalDate().lengthOfMonth(), 23, 59, 59);
        ZonedDateTime endOfMonthWithZone = endOfMonth.atZone(ZoneId.of("America/Lima"));
        long endTimestamp = endOfMonthWithZone.toEpochSecond();

        return List.of(
                Map.of("startTimestamp", startTimestamp, "endTimestamp", endTimestamp)
        );
    }
}