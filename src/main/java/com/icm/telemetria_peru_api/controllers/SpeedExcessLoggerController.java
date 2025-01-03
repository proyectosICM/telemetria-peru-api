package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
import com.icm.telemetria_peru_api.services.SpeedExcessLoggerService;
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

import java.util.List;

@RestController
@RequestMapping("api/speed_excess_logger")
@RequiredArgsConstructor
public class SpeedExcessLoggerController {
    private final SpeedExcessLoggerService speedExcessLoggerService;

    @GetMapping
    public List<SpeedExcessLoggerModel> findAll(){
        return speedExcessLoggerService.findAll();
    }
    @GetMapping("/page")
    public Page<SpeedExcessLoggerModel> findAll(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return speedExcessLoggerService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpeedExcessLoggerModel> findById(@PathVariable @NotNull Long id) {
        try {
            SpeedExcessLoggerModel data = speedExcessLoggerService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByVehicleId/{vehicleId}")
    public ResponseEntity<?> findByVehicleId(@PathVariable Long vehicleId){
        try {
            List<SpeedExcessLoggerModel> data = speedExcessLoggerService.findByVehicleId(vehicleId);
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
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<SpeedExcessLoggerModel> data = speedExcessLoggerService.findByVehicleId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody SpeedExcessLoggerModel speedExcessLoggerModel){
        try {
            SpeedExcessLoggerModel data = speedExcessLoggerService.save(speedExcessLoggerModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            speedExcessLoggerService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
