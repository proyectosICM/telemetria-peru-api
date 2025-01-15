package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import com.icm.telemetria_peru_api.services.ImpactIncidentLoggingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/impact_incident_logging")
@RequiredArgsConstructor
public class ImpactIncidentLoggingController {
    private final ImpactIncidentLoggingService impactIncidentLoggingService;

    @GetMapping("/{id}")
    public ResponseEntity<ImpactIncidentLoggingModel> findById(@PathVariable @NotNull Long id) {
        try {
            ImpactIncidentLoggingModel data = impactIncidentLoggingService.findById(id);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<ImpactIncidentLoggingModel> findAll(){
        return impactIncidentLoggingService.findAll();
    }
    @GetMapping("/paged")
    public Page<ImpactIncidentLoggingModel> findAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return impactIncidentLoggingService.findAll(pageable);
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<?> findByVehicleId(@PathVariable Long vehicleId){
        try {
            List<ImpactIncidentLoggingModel> data = impactIncidentLoggingService.findByVehicleId(vehicleId);
            if (data.isEmpty()) {
                return new ResponseEntity<>("No records found for vehicle with id " + vehicleId, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping ("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<?> findByVehicleIdPage(@PathVariable Long vehicleId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ImpactIncidentLoggingModel> data = impactIncidentLoggingService.findByVehicleId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ImpactIncidentLoggingModel impactIncidentLoggingModel){
        try {
            ImpactIncidentLoggingModel data = impactIncidentLoggingService.save(impactIncidentLoggingModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            impactIncidentLoggingService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
