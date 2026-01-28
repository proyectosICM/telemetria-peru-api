package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicleStateCurrentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleStateCurrentRepository extends JpaRepository<VehicleStateCurrentModel, Long> {
    Optional<VehicleStateCurrentModel> findByVehicleModel_Id(Long vehicleId);
}