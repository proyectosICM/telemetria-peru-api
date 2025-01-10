package com.icm.telemetria_peru_api.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
@Component
public class DateUtils {

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