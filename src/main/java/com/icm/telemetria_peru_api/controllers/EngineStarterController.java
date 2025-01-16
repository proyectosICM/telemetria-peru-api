package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import com.icm.telemetria_peru_api.services.EngineStarterService;
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
@RequestMapping("api/engine-starter")
@RequiredArgsConstructor
public class EngineStarterController {
    private final EngineStarterService engineStarterService;

    @GetMapping("/{id}")
    public ResponseEntity<EngineStarterModel> findById(@PathVariable @NotNull Long id) {
        return engineStarterService.findById(id)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<EngineStarterModel> findAll(){
        return engineStarterService.findAll();
    }

    @GetMapping("/paged")
    public Page<EngineStarterModel> findAll(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return engineStarterService.findAll(pageable);
    }

    @GetMapping("/by-battery/{batteryId}")
    public ResponseEntity<List<EngineStarterModel>> findByVehicleModelId(@PathVariable Long batteryId){
        try {
            List<EngineStarterModel> data =  engineStarterService.findByBatteryModelId(batteryId);

            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-battery-paged/{batteryId}")
    public ResponseEntity<Page<EngineStarterModel>> findByVehicleModelId(@PathVariable Long batteryId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<EngineStarterModel> data = engineStarterService.findByBatteryModelId(batteryId, pageable);

            if (data.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Page.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
