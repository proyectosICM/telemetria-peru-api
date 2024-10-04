package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.services.GasRecordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/gas-records")
public class GasRecordController {
    private final GasRecordService gasRecordService;

    public GasRecordController(GasRecordService gasRecordService) {
        this.gasRecordService = gasRecordService;
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

    @GetMapping
    public List<GasRecordModel> findAll(){
        return gasRecordService.findAll();
    }

    @GetMapping("/page")
    public Page<GasRecordModel> findAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "8") int size){
        Pageable pageable = PageRequest.of(page, size);
        return gasRecordService.findAll(pageable);
    }

    @GetMapping("/findByVehicleId/{vehicleId}")
    public ResponseEntity<List<GasRecordModel>> findByVehicleId(@PathVariable Long vehicleId){
        try {
            List<GasRecordModel> data = gasRecordService.findByVehicleId(vehicleId);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping ("/findByVehicleId-page/{vehicleId}")
    public ResponseEntity<Page<GasRecordModel>> findByVehicleIdPage(@PathVariable Long vehicleId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "8") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<GasRecordModel> data = gasRecordService.findByVehicleId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody GasRecordModel gasRecordModel){
        try {
            GasRecordModel data = gasRecordService.save(gasRecordModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id){
        try {
            gasRecordService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
