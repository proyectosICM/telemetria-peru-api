package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.BatteryModel;
import com.icm.telemetria_peru_api.services.BatteryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/batteries")
public class BatteryController {
    @Autowired
    private BatteryService batteryService;

    @GetMapping
    public List<BatteryModel> findAll() {
        return batteryService.findAll();
    }

    @GetMapping("/paged")
    public Page<BatteryModel> findAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return batteryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatteryModel> findById(@PathVariable @NotNull Long id) {
        try {
            BatteryModel gasRecord = batteryService.findById(id);
            return new ResponseEntity<>(gasRecord, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByVehicleId/{vehicleId}")
    public ResponseEntity<?> findByVehicleId(@PathVariable Long vehicleId){
        try {
            List<BatteryModel> data = batteryService.findByVehicleId(vehicleId);
            if (data.isEmpty()) {
                return new ResponseEntity<>("No gas records found for vehicle with id " + vehicleId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping ("/findByVehicleId-page/{vehicleId}")
    public ResponseEntity<?> findByVehicleIdPage(@PathVariable Long vehicleId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BatteryModel> data = batteryService.findByVehicleId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody BatteryModel batteryModel){
        try {
            BatteryModel data = batteryService.save(batteryModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            batteryService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
