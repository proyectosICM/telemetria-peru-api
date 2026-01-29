package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.repositories.projections.FuelEfficiencySumView;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fuel-efficiency")
public class FuelEfficiencyController {

    private final FuelEfficiencyService fuelEfficiencyService;

    // ====== DTOs ======

    @Data
    public static class UpsertDailyRequest {
        @NotNull
        private Long vehicleId;

        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate day;

        @Min(0)
        private Long parkedSeconds;

        @Min(0)
        private Long idleSeconds;

        @Min(0)
        private Long operationSeconds;
    }

    @Data
    public static class AddSecondsRequest {
        @NotNull
        private Long vehicleId;

        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate day;

        // deltas pueden ser 0; si no quieres permitir negativos, deja @Min(0)
        @Min(0)
        private Long parkedSecondsDelta;

        @Min(0)
        private Long idleSecondsDelta;

        @Min(0)
        private Long operationSecondsDelta;
    }

    @Data
    public static class SumResponse {
        private Long parkedSeconds;
        private Long idleSeconds;
        private Long operationSeconds;

        public static SumResponse from(FuelEfficiencySumView v) {
            SumResponse r = new SumResponse();
            r.setParkedSeconds(v.getParkedSeconds());
            r.setIdleSeconds(v.getIdleSeconds());
            r.setOperationSeconds(v.getOperationSeconds());
            return r;
        }
    }

    // ====== Endpoints ======

    @GetMapping("/{id}")
    public ResponseEntity<FuelEfficiencyModel> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelEfficiencyService.getById(id));
    }

    // GET /api/fuel-efficiency/vehicle/{vehicleId}/day/2026-01-28
    @GetMapping("/vehicle/{vehicleId}/day/{day}")
    public ResponseEntity<FuelEfficiencyModel> getByVehicleAndDay(
            @PathVariable Long vehicleId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        return ResponseEntity.ok(fuelEfficiencyService.getByVehicleAndDay(vehicleId, day));
    }

    // GET /api/fuel-efficiency/day/2026-01-28
    @GetMapping("/day/{day}")
    public ResponseEntity<List<FuelEfficiencyModel>> listByDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        return ResponseEntity.ok(fuelEfficiencyService.listByDay(day));
    }

    // GET /api/fuel-efficiency/company/{companyId}/day/2026-01-28
    @GetMapping("/company/{companyId}/day/{day}")
    public ResponseEntity<List<FuelEfficiencyModel>> listByCompanyAndDay(
            @PathVariable Long companyId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        return ResponseEntity.ok(fuelEfficiencyService.listByCompanyAndDay(companyId, day));
    }

    // GET /api/fuel-efficiency/vehicle/{vehicleId}/range?start=2026-01-01&end=2026-01-31
    @GetMapping("/vehicle/{vehicleId}/range")
    public ResponseEntity<List<FuelEfficiencyModel>> listByVehicleRange(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(fuelEfficiencyService.listByVehicleAndRange(vehicleId, start, end));
    }

    // GET /api/fuel-efficiency/company/{companyId}/range?start=2026-01-01&end=2026-01-31
    @GetMapping("/company/{companyId}/range")
    public ResponseEntity<List<FuelEfficiencyModel>> listByCompanyRange(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(fuelEfficiencyService.listByCompanyAndRange(companyId, start, end));
    }

    // ✅ GET /api/fuel-efficiency/vehicle/{vehicleId}/day/{day}/paged?page=0&size=20
    @GetMapping("/vehicle/{vehicleId}/day/{day}/paged")
    public ResponseEntity<Page<FuelEfficiencyModel>> listByVehicleDayPaged(
            @PathVariable Long vehicleId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("day").descending().and(Sort.by("id").descending()));
        return ResponseEntity.ok(fuelEfficiencyService.findAllByVehicleModel_IdAndDay(vehicleId, day, pageable));
    }

    // ✅ GET /api/fuel-efficiency/vehicle/{vehicleId}/range/paged?start=2026-01-01&end=2026-01-31&page=0&size=20
    @GetMapping("/vehicle/{vehicleId}/range/paged")
    public ResponseEntity<Page<FuelEfficiencyModel>> listByVehicleRangePaged(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("day").descending().and(Sort.by("id").descending()));
        return ResponseEntity.ok(fuelEfficiencyService.findAllByVehicleModel_IdAndDayBetween(vehicleId, start, end, pageable));
    }

    // ✅ GET /api/fuel-efficiency/company/{companyId}/day/{day}/paged?page=0&size=20
    @GetMapping("/company/{companyId}/day/{day}/paged")
    public ResponseEntity<Page<FuelEfficiencyModel>> listByCompanyDayPaged(
            @PathVariable Long companyId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("day").descending().and(Sort.by("id").descending())
        );

        return ResponseEntity.ok(
                fuelEfficiencyService.findAllByVehicleModel_CompanyModel_IdAndDay(companyId, day, pageable)
        );
    }

    // ✅ GET /api/fuel-efficiency/company/{companyId}/range/paged?start=2026-01-01&end=2026-01-31&page=0&size=20
    @GetMapping("/company/{companyId}/range/paged")
    public ResponseEntity<Page<FuelEfficiencyModel>> listByCompanyRangePaged(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("day").descending().and(Sort.by("id").descending())
        );

        return ResponseEntity.ok(
                fuelEfficiencyService.findAllByVehicleModel_CompanyModel_IdAndDayBetween(companyId, start, end, pageable)
        );
    }


    // ===== SUM por rango =====

    // GET /api/fuel-efficiency/vehicle/{vehicleId}/sum?start=2026-01-01&end=2026-01-31
    @GetMapping("/vehicle/{vehicleId}/sum")
    public ResponseEntity<SumResponse> sumByVehicleRange(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        FuelEfficiencySumView sum = fuelEfficiencyService.sumByVehicleAndRange(vehicleId, start, end);
        return ResponseEntity.ok(SumResponse.from(sum));
    }

    // GET /api/fuel-efficiency/company/{companyId}/sum?start=2026-01-01&end=2026-01-31
    @GetMapping("/company/{companyId}/sum")
    public ResponseEntity<SumResponse> sumByCompanyRange(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        FuelEfficiencySumView sum = fuelEfficiencyService.sumByCompanyAndRange(companyId, start, end);
        return ResponseEntity.ok(SumResponse.from(sum));
    }

    // ===== CREATE / UPSERT =====

    // POST /api/fuel-efficiency/daily/upsert
    @PostMapping("/daily/upsert")
    public ResponseEntity<FuelEfficiencyModel> upsertDaily(@Valid @RequestBody UpsertDailyRequest req) {
        FuelEfficiencyModel saved = fuelEfficiencyService.upsertDaily(
                req.getVehicleId(),
                req.getDay(),
                req.getParkedSeconds(),
                req.getIdleSeconds(),
                req.getOperationSeconds()
        );
        return ResponseEntity.ok(saved);
    }

    // POST /api/fuel-efficiency/daily/add-seconds
    @PostMapping("/daily/add-seconds")
    public ResponseEntity<FuelEfficiencyModel> addSeconds(@Valid @RequestBody AddSecondsRequest req) {
        FuelEfficiencyModel saved = fuelEfficiencyService.addSeconds(
                req.getVehicleId(),
                req.getDay(),
                req.getParkedSecondsDelta(),
                req.getIdleSecondsDelta(),
                req.getOperationSecondsDelta()
        );
        return ResponseEntity.ok(saved);
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelEfficiencyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
