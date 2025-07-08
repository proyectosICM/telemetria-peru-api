package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.TirePositioningModel;
import com.icm.telemetria_peru_api.services.TirePositioningService;
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
@RequestMapping("api/tire-positioning")
@RequiredArgsConstructor
public class TirePositioningController {
    private final TirePositioningService tirePositioningService;

    @GetMapping
    public ResponseEntity<List<TirePositioningModel>> findAll() {
        try {
            List<TirePositioningModel> data = tirePositioningService.findAll();
            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<TirePositioningModel>> findByVehicleTypeModelId(@PathVariable Long vehicleId) {
        try {
            List<TirePositioningModel> data = tirePositioningService.findByVehicleTypeModelId(vehicleId);
            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<TirePositioningModel> save(@RequestBody TirePositioningModel tirePositioningModel) {
        try {
            TirePositioningModel savedModel = tirePositioningService.save(tirePositioningModel);
            return new ResponseEntity<>(savedModel, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TirePositioningModel> update(@PathVariable Long id, @RequestBody TirePositioningModel tirePositioningModel) {
        try {
            tirePositioningModel.setId(id);
            TirePositioningModel updatedModel = tirePositioningService.update(tirePositioningModel);
            return new ResponseEntity<>(updatedModel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
