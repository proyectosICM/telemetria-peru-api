package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.dto.AlternatorDTO;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlternatorRepository extends JpaRepository<AlternatorModel, Long> {
    List<AlternatorDTO> findByVehicleModelId(Long vehicleId);
    Page<AlternatorDTO> findByVehicleModelId(Long vehicleId, Pageable pageable);
}
