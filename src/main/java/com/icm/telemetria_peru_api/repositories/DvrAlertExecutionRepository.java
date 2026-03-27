package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.DvrAlertExecutionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DvrAlertExecutionRepository extends JpaRepository<DvrAlertExecutionModel, Long> {
    List<DvrAlertExecutionModel> findTop20ByVehicleModelIdOrderByCreatedAtDesc(Long vehicleId);
    List<DvrAlertExecutionModel> findByVehicleModelIdOrderByCreatedAtDesc(Long vehicleId);
}
