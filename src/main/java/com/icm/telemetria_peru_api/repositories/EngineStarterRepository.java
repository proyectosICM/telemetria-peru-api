package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EngineStarterRepository extends JpaRepository<EngineStarterModel, Long> {
    List<EngineStarterModel> findByBatteryModelId(Long batteryId);
    Page<EngineStarterModel> findByBatteryModelId(Long batteryId, Pageable pageable);
}
