package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.AlternatorDTO;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.models.BatteryModel;
import com.icm.telemetria_peru_api.services.AlternatorService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/alternator")
@RequiredArgsConstructor
public class AlternatorController {
    private final AlternatorService alternatorService;

    @GetMapping
    public List<AlternatorDTO> findAll(){
        return alternatorService.findAll();
    }

    @GetMapping("/paged")
    public Page<AlternatorDTO> findAll(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return alternatorService.findAll(pageable);
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<AlternatorDTO>> findByVehicleModelId(@PathVariable Long vehicleId){
        try {
            List<AlternatorDTO> data =  alternatorService.findByVehicleModelId(vehicleId);

            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<Page<AlternatorDTO>> findByVehicleModelId(@PathVariable Long vehicleId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "3") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<AlternatorDTO> data = alternatorService.findByVehicleModelId(vehicleId, pageable);

            if (data.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(Page.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/counts-by-days/{vehicleId}")
    public ResponseEntity<List<Map<String, Object>>> getVehicleIgnitionRecords(@PathVariable Long vehicleId,
                                                                               @RequestParam(required = false) Integer year,
                                                                               @RequestParam(required = false) Integer month) {
        try {
            List<Map<String, Object>> records = alternatorService.getDataMonth(vehicleId, year, month);

            if (records.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(Map.of("error", e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<AlternatorModel> save(@RequestBody AlternatorModel alternatorModel){
        try {
            AlternatorModel data = alternatorService.save(alternatorModel);
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
