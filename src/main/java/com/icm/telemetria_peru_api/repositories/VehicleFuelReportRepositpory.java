package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleFuelReportRepositpory extends JpaRepository<VehicleFuelReportModel, Long> {
    Optional<VehicleFuelReportModel> findTopByVehicleModelIdOrderByCreatedAtDesc(Long vehicleId);

    List<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleFuelReportModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
}
