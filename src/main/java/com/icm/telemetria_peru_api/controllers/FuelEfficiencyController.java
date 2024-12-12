package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.FuelEfficiencyDTO;
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

import java.util.List;
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
    public ResponseEntity<List<FuelEfficiencyDTO>> findByVehicleModelId(
            @PathVariable Long vehicleModelId) {
        List<FuelEfficiencyDTO> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId);
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

    @PostMapping
    public ResponseEntity<FuelEfficiencyModel> save(@RequestBody FuelEfficiencyModel fuelEfficiencyModel){
        FuelEfficiencyModel saveData = fuelEfficiencyService.save(fuelEfficiencyModel);
        return new ResponseEntity<>(saveData, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuelEfficiencyModel> editEffi(@PathVariable Long id){
        FuelEfficiencyModel data = fuelEfficiencyService.editEfficiency(id);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PutMapping("/reset-non-operational")
    public ResponseEntity<List<FuelEfficiencyModel>> resetNonOperationalEfficiencies() {
        try {
            List<FuelEfficiencyModel> updatedRecords = fuelEfficiencyService.resetNonOperationalEfficiencies();
            return ResponseEntity.ok(updatedRecords);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FuelEfficiencyModel> delete(@PathVariable Long id){
        fuelEfficiencyService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
