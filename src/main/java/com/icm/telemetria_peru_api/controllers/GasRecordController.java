package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.services.GasRecordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/gas-records")
public class GasRecordController {
    @Autowired
    private GasRecordService gasRecordService;

    @GetMapping
    public List<GasRecordModel> findAll(){
        return gasRecordService.findAll();
    }
    @GetMapping("/page")
    public Page<GasRecordModel> findAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return gasRecordService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GasRecordModel> findById(@PathVariable @NotNull Long id) {
        try {
            GasRecordModel gasRecord = gasRecordService.findById(id);
            return new ResponseEntity<>(gasRecord, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByVehicleId/{vehicleId}")
    public ResponseEntity<?> findByVehicleId(@PathVariable Long vehicleId){
        try {
            List<GasRecordModel> data = gasRecordService.findByVehicleId(vehicleId);
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
            Page<GasRecordModel> data = gasRecordService.findByVehicleId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody GasRecordModel gasRecordModel){
        try {
            GasRecordModel data = gasRecordService.save(gasRecordModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        try {
            gasRecordService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
