package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.FuelEfficiencyDTO;
import com.icm.telemetria_peru_api.dto.FuelEfficiencySummaryDTO;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/fuel-efficiency")
@RequiredArgsConstructor
public class FuelEfficiencyController {
    private final FuelEfficiencyService fuelEfficiencyService;
    private final MqttMessagePublisher mqttMessagePublisher;


    @GetMapping("/{id}")
    public ResponseEntity<Optional<FuelEfficiencyModel>> findById(@PathVariable Long id){
        Optional<FuelEfficiencyModel> data = fuelEfficiencyService.findById(id);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/findByVehicle/{vehicleModelId}")
    public ResponseEntity<List<FuelEfficiencyModel>> findByVehicleModelId(
            @PathVariable Long vehicleModelId) {
        List<FuelEfficiencyModel> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/findByVehicle-paged/{vehicleModelId}")
    public ResponseEntity<Page<FuelEfficiencyDTO>> findByVehicleModelId(
            @PathVariable Long vehicleModelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FuelEfficiencyDTO> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId, pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /** STAST */

    @GetMapping("/daily-averages/{vehicleId}")
    public List<Map<String, Object>> getDailyAveragesForMonth(@PathVariable Long vehicleId,
                                                              @RequestParam Integer month,
                                                              @RequestParam(defaultValue = "") Integer year) {
        if (year == null || year == 0) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        return fuelEfficiencyService.getDailyAveragesForMonth(vehicleId, month, year);
    }

    @GetMapping("/monthly-averages/{vehicleId}")
    public List<Map<String, Object>> getMonthlyAveragesForYear(@PathVariable Long vehicleId,
                                                                      @RequestParam String status,
                                                                      @RequestParam(defaultValue = "") Integer year) {
        if (year == null || year == 0) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        return fuelEfficiencyService.getMonthlyAveragesForYear(vehicleId, status, year);
    }

    @GetMapping("/summary/{vehicleId}")
    public ResponseEntity<List<FuelEfficiencySummaryDTO>> getFuelEfficiencyByVehicle(
            @PathVariable Long vehicleId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day) {

        List<FuelEfficiencySummaryDTO> summary = fuelEfficiencyService.getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(vehicleId, year, month, day);
        return ResponseEntity.ok(summary);
    }
    /** STAST */
    @PostMapping
    public ResponseEntity<FuelEfficiencyModel> save(@RequestBody FuelEfficiencyModel fuelEfficiencyModel){
        FuelEfficiencyModel saveData = fuelEfficiencyService.save(fuelEfficiencyModel);
        return new ResponseEntity<>(saveData, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FuelEfficiencyModel> delete(@PathVariable Long id){
        fuelEfficiencyService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
