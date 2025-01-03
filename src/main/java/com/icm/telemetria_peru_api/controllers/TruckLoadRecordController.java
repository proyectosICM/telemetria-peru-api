package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.BatteryRecordDTO;
import com.icm.telemetria_peru_api.dto.DailyLoadCountDTO;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import com.icm.telemetria_peru_api.models.TruckLoadRecordModel;
import com.icm.telemetria_peru_api.repositories.TruckLoadRecordRepository;
import com.icm.telemetria_peru_api.services.TruckLoadRecordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/truck-loads")
@RequiredArgsConstructor
public class TruckLoadRecordController {
    private final TruckLoadRecordService truckLoadRecordService;

    @GetMapping("/count-day/{vehicleId}")
    public long countRecordsByVehicleAndToday(@PathVariable Long vehicleId) {
        LocalDate today = LocalDate.now();
        return truckLoadRecordService.countRecordsByVehicleAndDate(vehicleId, today);
    }

    @GetMapping("/daily-load-counts/{vehicleId}")
    public Page<Map<String, Object>> getDailyLoadCountsByVehicle(@PathVariable Long vehicleId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return truckLoadRecordService.getDailyLoadCountsByVehicle(vehicleId, pageable);
    }

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

    @GetMapping("/page")
    public Page<TruckLoadRecordModel> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return truckLoadRecordService.findAll(pageable);
    }

    @GetMapping("/findByVehicle/{vehicleId}")
    public List<TruckLoadRecordModel> findByvehicleId(@PathVariable Long vehicleId) {
        return truckLoadRecordService.findByVehicleId(vehicleId);
    }

    @GetMapping("/findByVehicle-paged/{vehicleId}")
    public Page<TruckLoadRecordModel> findByvehicleId(@PathVariable Long vehicleId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return truckLoadRecordService.findByVehicleId(vehicleId, pageable);
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
