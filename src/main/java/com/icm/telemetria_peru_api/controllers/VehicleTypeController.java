package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.VehicleTypeModel;
import com.icm.telemetria_peru_api.services.VehicleTypeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/vehicle-type")
public class VehicleTypeController {
    @Autowired
    private VehicleTypeService vehicletypeService;

    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeModel> getVehicletypeById(@PathVariable @NotNull Long id) {
        Optional<VehicleTypeModel> vehicletype = vehicletypeService.findById(id);
        return vehicletype
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<List<VehicleTypeModel>> getAllVehicletypes() {
        List<VehicleTypeModel> vehicletypes = vehicletypeService.findAll();
        return ResponseEntity.ok(vehicletypes);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<VehicleTypeModel>> getAllVehicletypes(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleTypeModel> vehicletypes = vehicletypeService.findAll(pageable);
        return ResponseEntity.ok(vehicletypes);
    }

    @PostMapping
    public ResponseEntity<VehicleTypeModel> createVehicletype(@RequestBody @Valid VehicleTypeModel vehicletypeModel) {
        VehicleTypeModel createdVehicletype = vehicletypeService.save(vehicletypeModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicletype);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleTypeModel> updateVehicletype(
            @PathVariable @NotNull Long id,
            @RequestBody @Valid VehicleTypeModel vehicletypeModel) {
        try {
            VehicleTypeModel updatedVehicletype = vehicletypeService.update(id, vehicletypeModel);
            return ResponseEntity.ok(updatedVehicletype);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
