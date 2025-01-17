package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.BatteryRecordDTO;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import com.icm.telemetria_peru_api.services.BatteryRecordService;
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
@RequestMapping("api/batteries-records")
@RequiredArgsConstructor
public class BatteryRecordController {
    private final BatteryRecordService batteryRecordService;
    @GetMapping("/{id}")
    public ResponseEntity<BatteryRecordDTO> findById(@PathVariable @NotNull Long id) {
        try {
            BatteryRecordDTO gasRecord = batteryRecordService.findById(id);
            return new ResponseEntity<>(gasRecord, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<BatteryRecordDTO> findAll() {
        return batteryRecordService.findAll();
    }

    @GetMapping("/paged")
    public Page<BatteryRecordDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return batteryRecordService.findAll(pageable);
    }

    @GetMapping("/by-battery/{batteryId}")
    public ResponseEntity<List<BatteryRecordDTO>> findByVehicleId(@PathVariable Long batteryId){
        try {
            List<BatteryRecordDTO> data = batteryRecordService.findByBatteryId(batteryId);
            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping ("/by-battery-paged/{batteryId}")
    public ResponseEntity<Page<BatteryRecordDTO>> findByVehicleIdPage(@PathVariable Long batteryId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "3") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BatteryRecordDTO> data = batteryRecordService.findByBatteryId(batteryId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<BatteryRecordDTO>> findByBatteryModelVehicleModelId(@PathVariable Long vehicleId){
        try {
            List<BatteryRecordDTO> data = batteryRecordService.findByBatteryModelVehicleModelId(vehicleId);
            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping ("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<Page<BatteryRecordDTO>> findByBatteryModelVehicleModelId(@PathVariable Long vehicleId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "3") int size){
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BatteryRecordDTO> data = batteryRecordService.findByBatteryModelVehicleModelId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle-battery/{vehicleId}")
    public ResponseEntity<List<BatteryRecordDTO>> findByBatteryModelVehicleModelIdAndBatteryModelId(@PathVariable Long vehicleId, @RequestParam Long batteryId){
        try {
            List<BatteryRecordDTO> data = batteryRecordService.findByBatteryModelVehicleModelIdAndBatteryModelId(vehicleId, batteryId);
            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping ("/by-vehicle-battery-paged/{vehicleId}")
    public ResponseEntity<Page<BatteryRecordDTO>> findByBatteryModelVehicleModelIdAndBatteryModelId(@PathVariable Long vehicleId,
                                                                                   @RequestParam Long batteryId,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<BatteryRecordDTO> data = batteryRecordService.findByBatteryModelVehicleModelIdAndBatteryModelId(vehicleId,batteryId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody BatteryRecordModel batteryRecordModel){
        try {
            BatteryRecordModel data = batteryRecordService.save(batteryRecordModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}