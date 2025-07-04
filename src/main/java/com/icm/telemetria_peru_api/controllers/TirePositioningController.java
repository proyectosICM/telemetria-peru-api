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

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<TirePositioningModel>> findByVehicleId(@PathVariable Long vehicleId) {
        try {
            List<TirePositioningModel> data = tirePositioningService.findByVehicleModelId(vehicleId);
            if (data.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<List<TirePositioningModel>> findByVehicleIdPaged(@PathVariable Long vehicleId,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<TirePositioningModel> data = tirePositioningService.findByVehicleModelId(vehicleId, pageable);
            if (data.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(data.getContent(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
