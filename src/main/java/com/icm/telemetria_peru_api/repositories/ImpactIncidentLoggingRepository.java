package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImpactIncidentLoggingRepository extends JpaRepository<ImpactIncidentLoggingModel, Long> {
    List<ImpactIncidentLoggingModel> findByVehicleModelId(Long id);
    Page<ImpactIncidentLoggingModel> findByVehicleModelId(Long id, Pageable pageable);
}
