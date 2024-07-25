package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.GasChangeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GasChangeRepository extends JpaRepository<GasChangeModel, Long> {
    List<GasChangeModel> findByVehicleModelId(Long vehicleId);
    Page<GasChangeModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
}
