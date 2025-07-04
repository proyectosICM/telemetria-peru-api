package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleSnapshotService {
    VehicleSnapshotModel getSnapshotById(Long id);
    VehicleSnapshotModel getLatestSnapshotByVehicleId(Long vehicleId);
    List<VehicleSnapshotModel> getAllSnapshots();
    List<VehicleSnapshotModel> getSnapshotsByVehicleId(Long vehicleId);
    Page<VehicleSnapshotModel> getSnapshotsByVehicleId(Long vehicleId, Pageable pageable);
    List<VehicleSnapshotModel> getSnapshotsByCompanyId(Long companyId);
    Page<VehicleSnapshotModel> getSnapshotsByCompanyId(Long companyId, Pageable pageable);
    void saveSnapshot(VehicleSnapshotModel snapshot);
    VehicleSnapshotModel updateOrCreateSnapshotByVehicleId(Long vehicleId, VehicleSnapshotModel newSnapshotData);

}
