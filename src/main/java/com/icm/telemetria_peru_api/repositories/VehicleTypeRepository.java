package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicleTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleTypeModel, Long> {
}
