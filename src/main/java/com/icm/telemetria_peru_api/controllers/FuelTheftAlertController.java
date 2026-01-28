package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.FuelTheftAlertModel;
import com.icm.telemetria_peru_api.services.FuelTheftAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

@RestController
@RequestMapping("/api/fuel-theft-alerts")
@RequiredArgsConstructor
public class FuelTheftAlertController {

    private final FuelTheftAlertService service;

    @GetMapping("/{id}")
    public FuelTheftAlertModel getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<FuelTheftAlertModel> search(
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String status,

            // period: day|week|month|year
            @RequestParam(required = false) String period,

            // date: YYYY-MM-DD
            @RequestParam(required = false) String date,

            @RequestParam(defaultValue = "America/Lima") String tz,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ZoneId zoneId = ZoneId.of(tz);

        Range range = computeRange(period, date, zoneId);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "detectedAt") // más reciente primero
        );

        return service.search(vehicleId, status, range.start, range.end, pageable);
    }

    private static class Range {
        ZonedDateTime start;
        ZonedDateTime end;
        Range(ZonedDateTime start, ZonedDateTime end) { this.start = start; this.end = end; }
    }

    private Range computeRange(String period, String date, ZoneId zoneId) {
        // Si no piden period, no filtramos por tiempo
        if (period == null || period.isBlank()) {
            return new Range(null, null);
        }

        LocalDate baseDate = (date == null || date.isBlank())
                ? LocalDate.now(zoneId)
                : LocalDate.parse(date);

        period = period.trim().toLowerCase();

        return switch (period) {
            case "day" -> {
                ZonedDateTime start = baseDate.atStartOfDay(zoneId);
                ZonedDateTime end = baseDate.plusDays(1).atStartOfDay(zoneId);
                yield new Range(start, end);
            }
            case "week" -> {
                // Semana ISO (lunes-domingo). Si quieres domingo->sábado, se cambia aquí.
                WeekFields wf = WeekFields.of(Locale.getDefault());
                LocalDate weekStart = baseDate.with(wf.dayOfWeek(), 1);
                LocalDate weekEndExclusive = weekStart.plusDays(7);

                ZonedDateTime start = weekStart.atStartOfDay(zoneId);
                ZonedDateTime end = weekEndExclusive.atStartOfDay(zoneId);
                yield new Range(start, end);
            }
            case "month" -> {
                LocalDate monthStart = baseDate.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate monthEndExclusive = monthStart.plusMonths(1);

                ZonedDateTime start = monthStart.atStartOfDay(zoneId);
                ZonedDateTime end = monthEndExclusive.atStartOfDay(zoneId);
                yield new Range(start, end);
            }
            case "year" -> {
                LocalDate yearStart = baseDate.with(TemporalAdjusters.firstDayOfYear());
                LocalDate yearEndExclusive = yearStart.plusYears(1);

                ZonedDateTime start = yearStart.atStartOfDay(zoneId);
                ZonedDateTime end = yearEndExclusive.atStartOfDay(zoneId);
                yield new Range(start, end);
            }
            default -> new Range(null, null);
        };
    }
}
