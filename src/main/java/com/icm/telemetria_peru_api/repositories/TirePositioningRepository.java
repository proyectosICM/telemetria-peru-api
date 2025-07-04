package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.TirePositioningModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TirePositioningRepository extends JpaRepository<TirePositioningModel, Long> {
    List<TirePositioningModel> findByVehicleId(Long vehicleId);
    Page<TirePositioningModel> findByVehicleId(Long vehicleId, Pageable pageable);
}
