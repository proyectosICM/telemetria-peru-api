package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import com.icm.telemetria_peru_api.services.VehicleSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/vehicle-snapshots")
@RequiredArgsConstructor
public class VehicleSnapshotController {
    private final VehicleSnapshotService vehicleSnapshotService;

    @GetMapping("/{id}")
    public VehicleSnapshotModel getSnapshotById(@PathVariable Long id) {
        return vehicleSnapshotService.getSnapshotById(id);
    }

    @GetMapping
    public List<VehicleSnapshotModel> getAllSnapshots() {
        return vehicleSnapshotService.getAllSnapshots();
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public List<VehicleSnapshotModel> getSnapshotsByVehicleId(@PathVariable Long vehicleId) {
        return vehicleSnapshotService.getSnapshotsByVehicleId(vehicleId);
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public Page<VehicleSnapshotModel> getSnapshotsByVehicleIdPaged(@PathVariable Long vehicleId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleSnapshotService.getSnapshotsByVehicleId(vehicleId, pageable);
    }

    @GetMapping("/by-company/{companyId}")
    public List<VehicleSnapshotModel> getSnapshotsByCompanyId(@PathVariable Long companyId) {
        return vehicleSnapshotService.getSnapshotsByCompanyId(companyId);
    }

    @GetMapping("/by-company-paged/{companyId}")
    public Page<VehicleSnapshotModel> getSnapshotsByCompanyIdPaged(@PathVariable Long companyId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleSnapshotService.getSnapshotsByCompanyId(companyId, pageable);
    }

    @PostMapping
    public VehicleSnapshotModel saveSnapshot(@RequestBody VehicleSnapshotModel snapshot) {
        vehicleSnapshotService.saveSnapshot(snapshot);
        return snapshot;
    }
}
