package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.PositioningModel;
import com.icm.telemetria_peru_api.services.PositioningService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/positioning")
@RequiredArgsConstructor
public class PositioningController {
    private final PositioningService positioningService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<PositioningModel> positionings = positioningService.findAll();
            return new ResponseEntity<>(positionings, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositioningModel> findById(@PathVariable Long id) {
        try {
            PositioningModel data = positioningService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/vehicleType/{vehicleTypeId}")
    public ResponseEntity<?> findByVehicleTypeId(@PathVariable Long vehicleTypeId) {
        try {
            List<PositioningModel> positionings = positioningService.findByVehicleTypeId(vehicleTypeId);
            if (positionings.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(positionings);
        } catch (Exception e){
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody PositioningModel positioningModel){
        try {
            PositioningModel data = positioningService.save(positioningModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            positioningService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
