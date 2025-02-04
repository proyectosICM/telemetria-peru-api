package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.TruckLoadRecordModel;
import com.icm.telemetria_peru_api.services.TruckLoadRecordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/truck-loads")
@RequiredArgsConstructor
public class TruckLoadRecordController {
    private final TruckLoadRecordService truckLoadRecordService;

    @GetMapping("/{id}")
    public ResponseEntity<Optional<TruckLoadRecordModel>> findById(@PathVariable @NotNull Long id) {
        try {
            Optional<TruckLoadRecordModel> data = truckLoadRecordService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<TruckLoadRecordModel> findAll() {
        return truckLoadRecordService.findAll();
    }

    @GetMapping("/paged")
    public Page<TruckLoadRecordModel> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return truckLoadRecordService.findAll(pageable);
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public List<TruckLoadRecordModel> findByvehicleId(@PathVariable Long vehicleId) {
        return truckLoadRecordService.findByVehicleId(vehicleId);
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public Page<TruckLoadRecordModel> findByvehicleId(@PathVariable Long vehicleId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return truckLoadRecordService.findByVehicleId(vehicleId, pageable);
    }

    /** */
    @GetMapping("/count-day/{vehicleId}")
    public long countRecordsByVehicleAndToday(@PathVariable Long vehicleId) {
        LocalDate today = LocalDate.now();
        return truckLoadRecordService.countRecordsByVehicleAndDate(vehicleId, today);
    }

    /** */
    @GetMapping("/count-month/{vehicleId}")
    public ResponseEntity<Map<String, Object>> getDataForMonth(
            @PathVariable Long vehicleId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Map<String, Object>> data = truckLoadRecordService.getDataMonth(vehicleId, year, month);

            if (data.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // Ordenar en orden inverso (último primero)
            List<Map<String, Object>> sortedData = data.stream()
                    .sorted((a, b) -> Long.compare((long) b.get("day"), (long) a.get("day")))
                    .collect(Collectors.toList());

            // Paginación manual
            int start = page * size;
            int end = Math.min(start + size, sortedData.size());
            if (start >= sortedData.size()) {
                return ResponseEntity.ok(Collections.singletonMap("message", "No more data"));
            }

            List<Map<String, Object>> paginatedData = sortedData.subList(start, end);

            // Construir la respuesta paginada
            Map<String, Object> response = new HashMap<>();
            response.put("data", paginatedData);
            response.put("currentPage", page);
            response.put("totalPages", (int) Math.ceil((double) sortedData.size() / size));
            response.put("totalRecords", sortedData.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/daily-load-counts/{vehicleId}")
    public Page<Map<String, Object>> getDailyLoadCountsByVehicle(@PathVariable Long vehicleId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return truckLoadRecordService.getDailyLoadCountsByVehicle(vehicleId, pageable);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody TruckLoadRecordModel truckLoadRecordModel) {
        try {
            TruckLoadRecordModel data = truckLoadRecordService.save(truckLoadRecordModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
