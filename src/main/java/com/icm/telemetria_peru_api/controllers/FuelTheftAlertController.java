package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.FuelTheftAlertDTO;
import com.icm.telemetria_peru_api.models.FuelTheftAlertModel;
import com.icm.telemetria_peru_api.services.FuelTheftAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

@RestController
@RequestMapping("/api/fuel-theft-alerts")
@RequiredArgsConstructor
public class FuelTheftAlertController {

    private final FuelTheftAlertService service;

    @GetMapping("/{id}")
    public ResponseEntity<FuelTheftAlertDTO> getById(@PathVariable Long id) {
        FuelTheftAlertModel a = service.getById(id);
        return ResponseEntity.ok(toDTO(a));
    }

    @GetMapping
    public ResponseEntity<Page<FuelTheftAlertDTO>> search(
            @RequestParam(required = false) Long vehicleId,

            // period: day|week|month|year (opcional)
            @RequestParam(required = false) String period,

            // date: YYYY-MM-DD (opcional; si no viene, usa hoy)
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

        Page<FuelTheftAlertDTO> dtoPage = service
                .search(vehicleId, range.start, range.end, pageable)
                .map(FuelTheftAlertController::toDTO);

        return ResponseEntity.ok(dtoPage);
    }

    // ===== Helpers =====

    private static FuelTheftAlertDTO toDTO(FuelTheftAlertModel a) {
        return new FuelTheftAlertDTO(
                a.getId(),
                a.getVehicleModel() != null ? a.getVehicleModel().getId() : null,
                a.getVehicleModel() != null ? a.getVehicleModel().getLicensePlate() : null,
                a.getDetectedAt(),
                a.getMessage()
        );
    }

    private static class Range {
        ZonedDateTime start;
        ZonedDateTime end;
        Range(ZonedDateTime start, ZonedDateTime end) {
            this.start = start;
            this.end = end;
        }
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
                WeekFields wf = WeekFields.ISO; // lunes-domingo
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
