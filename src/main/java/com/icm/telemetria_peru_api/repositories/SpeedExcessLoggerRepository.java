package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeedExcessLoggerRepository extends JpaRepository<SpeedExcessLoggerModel, Long> {
    List<SpeedExcessLoggerModel> findByVehicleModelId(Long id);
    Page<SpeedExcessLoggerModel> findByVehicleModelId(Long id, Pageable pageable);
}
