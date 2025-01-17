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

import java.util.List;

@RestController
@RequestMapping("api/alternator")
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
