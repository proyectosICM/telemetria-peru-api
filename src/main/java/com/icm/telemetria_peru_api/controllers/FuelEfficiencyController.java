package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/fuel-efficiency")
@RequiredArgsConstructor
public class FuelEfficiencyController {
    private final FuelEfficiencyService fuelEfficiencyService;
    private final MqttMessagePublisher mqttMessagePublisher;

    @GetMapping("/findByVehicle/{vehicleModelId}")
    public ResponseEntity<List<FuelEfficiencyModel>> findByVehicleModelId(
            @PathVariable Long vehicleModelId) {
        List<FuelEfficiencyModel> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/findByVehicle-paged/{vehicleModelId}")
    public ResponseEntity<Page<FuelEfficiencyModel>> findByVehicleModelId(
            @PathVariable Long vehicleModelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FuelEfficiencyModel> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId, pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FuelEfficiencyModel> save(@RequestBody FuelEfficiencyModel fuelEfficiencyModel){
        FuelEfficiencyModel saveData = fuelEfficiencyService.save(fuelEfficiencyModel);
        return new ResponseEntity<>(saveData, HttpStatus.CREATED);
    }
}
