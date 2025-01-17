package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlternatorRepository extends JpaRepository<AlternatorModel, Long> {
    List<AlternatorModel> findByVehicleModelId(Long vehicleId);
    Page<AlternatorModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
}
