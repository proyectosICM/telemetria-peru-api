package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.services.VehicleFuelReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicle_fuel_report")
@RequiredArgsConstructor
public class VehicleFuelReportController {
    private final VehicleFuelReportService vehicleFuelReportService;

    @GetMapping
    public ResponseEntity<List<VehicleFuelReportModel>> findAll() {
        return ResponseEntity.ok(vehicleFuelReportService.findAll());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<VehicleFuelReportModel>> findAllPaged(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(vehicleFuelReportService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleFuelReportModel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleFuelReportService.findById(id));
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleFuelReportModel>> findByVehicleModelId(@PathVariable Long vehicleId){

        return ResponseEntity.ok(vehicleFuelReportService.findByVehicleModelId(vehicleId));
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<Page<VehicleFuelReportModel>> findByVehicleModelId(@PathVariable Long vehicleId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(vehicleFuelReportService.findByVehicleModelId(vehicleId, pageable));
    }

    @PostMapping
    public ResponseEntity<VehicleFuelReportModel> create(@RequestBody VehicleFuelReportModel vehicleFuelReportModel) {
        return ResponseEntity.ok(vehicleFuelReportService.save(vehicleFuelReportModel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleFuelReportModel> update(@PathVariable Long id, @RequestBody VehicleFuelReportModel updatedModel) {
        return ResponseEntity.ok(vehicleFuelReportService.update(id, updatedModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleFuelReportService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
