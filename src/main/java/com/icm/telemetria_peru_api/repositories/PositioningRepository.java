package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.PositioningModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositioningRepository extends JpaRepository<PositioningModel, Long> {
    List<PositioningModel> findByVehicleTypeModelId(Long vehicleTypeId);
    PositioningModel findByLocationCode(String locationCode);
}
