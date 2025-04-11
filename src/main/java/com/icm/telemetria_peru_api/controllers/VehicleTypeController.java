package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.VehicleTypeModel;
import com.icm.telemetria_peru_api.services.VehicleTypeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping
    public ResponseEntity<List<VehicleTypeModel>> getAllVehicletypes() {
        List<VehicleTypeModel> vehicletypes = vehicleTypeService.findAll();
        return ResponseEntity.ok(vehicletypes);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<VehicleTypeModel>> getAllVehicletypes(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleTypeModel> vehicletypes = vehicleTypeService.findAll(pageable);
        return ResponseEntity.ok(vehicletypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeModel> getVehicletypeById(@PathVariable @NotNull Long id) {
        Optional<VehicleTypeModel> vehicletype = vehicleTypeService.findById(id);
        return vehicletype
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<VehicleTypeModel> createVehicletype(@RequestBody @Valid VehicleTypeModel vehicletypeModel) {
        VehicleTypeModel createdVehicletype = vehicleTypeService.save(vehicletypeModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicletype);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleTypeModel> updateVehicletype(
            @PathVariable @NotNull Long id,
            @RequestBody @Valid VehicleTypeModel vehicletypeModel) {
        try {
            VehicleTypeModel updatedVehicletype = vehicleTypeService.update(id, vehicletypeModel);
            return ResponseEntity.ok(updatedVehicletype);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
