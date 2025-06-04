package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleSnapshotRepository extends JpaRepository<VehicleSnapshotModel, Long> {
    List<VehicleSnapshotModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleSnapshotModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<VehicleSnapshotModel> findByCompanyModelId(Long companyId);
    Page<VehicleSnapshotModel> findByCompanyModelId(Long companyId, Pageable pageable);
}
