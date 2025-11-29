package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.dto.VehicleOptionsDTO;
import com.icm.telemetria_peru_api.dto.VehicleVideoDTO;
import com.icm.telemetria_peru_api.enums.FuelType;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/vehicles")
@Tag(name = "Vehicle Controller", description = "Vehicle-related operations")
@RequiredArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;

    @Operation(summary = "Get fuel types", description = "Returns all available fuel types in list format.")
    @GetMapping("/fuel-types")
    public List<String> getTaskPriorities() {
        return Arrays.asList(FuelType.values())
                .stream()
                .map(Enum::name)
                .toList();
    }

    @Operation(summary = "Get all vehicles in the system")
    @GetMapping
    public List<VehicleDTO> findAll() {
        return vehicleService.findAll();
    }

    @Operation(summary = "Get vehicle by ID")
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDTO> findById(@PathVariable @NotNull Long vehicleId) {
        VehicleDTO vehicleModel = vehicleService.findById(vehicleId);
        return new ResponseEntity<>(vehicleModel, HttpStatus.OK);
    }

    @GetMapping("/by-company/{companyId}")
    public List<VehicleDTO> findByCompanyModelId(@PathVariable @NotNull Long companyId) {
        return vehicleService.findByCompanyModelId(companyId);
    }

    @GetMapping("/by-company-paged/{companyId}")
    public ResponseEntity<Page<VehicleDTO>> findByCompanyModelId (@PathVariable @NotNull Long companyId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findByCompanyModelId(companyId, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/by-status")
    public List<VehicleDTO> findByStatus(@RequestParam @NotNull Boolean status) {
        return vehicleService.findByStatus(status);
    }
    @GetMapping("/by-status-paged")
    public ResponseEntity<Page<VehicleDTO>> findByStatus (@RequestParam @NotNull Boolean status,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VehicleDTO> dataModel = vehicleService.findByStatus(status, pageable);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @GetMapping("/options-data/{vehicleId}")
    public ResponseEntity<VehicleOptionsDTO> findByIdOptions(@PathVariable @NotNull Long vehicleId) {
        VehicleOptionsDTO vehicleModel = vehicleService.findByIdOptions(vehicleId);
        return new ResponseEntity<>(vehicleModel, HttpStatus.OK);
    }

    @GetMapping("/{vehicleId}/video-config")
    public ResponseEntity<VehicleVideoDTO> getVehicleVideoConfig(@PathVariable Long vehicleId) {
        VehicleVideoDTO dto = vehicleService.getVideoConfig(vehicleId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<VehicleModel> save(@RequestBody @Valid VehicleModel vehicleModel){
        VehicleModel dataModel = vehicleService.save(vehicleModel);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @Operation(summary = "Update vehicle data",
            description = "Updates the main data of a vehicle given its ID. Returns 404 if the vehicle does not exist.")
    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleModel> update(@PathVariable @NotNull Long vehicleId, @RequestBody @Valid VehicleModel vehicleModel){
        VehicleModel dataModel = vehicleService.updateMainData(vehicleId, vehicleModel);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update")
    @PutMapping("/options-update/{vehicleId}")
    public ResponseEntity<VehicleModel> vehicleOptionsUpdate(@PathVariable @NotNull Long vehicleId,
                                                             @RequestParam @NotNull String type,
                                                             @RequestParam @NotNull Boolean status){
        VehicleModel dataModel = vehicleService.vehicleOptionsUpdate(vehicleId, type, status);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @PatchMapping("/status-toggle/{vehicleId}")
    public ResponseEntity<VehicleModel> statusToggle(@PathVariable @NotNull Long vehicleId){
        VehicleModel dataModel = vehicleService.statusToggle(vehicleId);
        return new ResponseEntity<>(dataModel, HttpStatus.OK);
    }

    @PutMapping("/update-driver/{vehicleId}")
    public ResponseEntity<VehicleModel> updateDriver(@PathVariable @NotNull Long vehicleId, @RequestParam @NotNull Long driverId){
        VehicleModel dataModel = vehicleService.updateDriver(vehicleId, driverId);
        return dataModel != null ?
                new ResponseEntity<>(dataModel, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<?> delete(@PathVariable @NotNull Long vehicleId){
        vehicleService.deleteById(vehicleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
