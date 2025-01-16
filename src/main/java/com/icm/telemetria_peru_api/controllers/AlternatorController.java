package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import com.icm.telemetria_peru_api.services.AlternatorService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlternatorController {
    private final AlternatorService alternatorService;

    @GetMapping("/{id}")
    public ResponseEntity<AlternatorModel> findById(@PathVariable @NotNull Long id) {
        return alternatorService.findById(id)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<AlternatorModel> findAll(){
        return alternatorService.findAll();
    }

    @GetMapping("/paged")
    public Page<AlternatorModel> findAll(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return alternatorService.findAll(pageable);
    }

    @GetMapping("/by-battery/{batteryId}")
    public ResponseEntity<List<AlternatorModel>> findByVehicleModelId(@PathVariable Long batteryId){
        try {
            List<AlternatorModel> data =  alternatorService.findByBatteryModelId(batteryId);

            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-battery-paged/{batteryId}")
    public ResponseEntity<Page<AlternatorModel>> findByVehicleModelId(@PathVariable Long batteryId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<AlternatorModel> data = alternatorService.findByBatteryModelId(batteryId, pageable);

            if (data.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Page.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
