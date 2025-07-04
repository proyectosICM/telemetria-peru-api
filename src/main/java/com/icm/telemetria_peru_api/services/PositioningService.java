package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.PositioningModel;

import java.util.List;

public interface PositioningService {
    List<PositioningModel> findAll();
    PositioningModel findById(Long id);
    List<PositioningModel> findByVehicleTypeId(Long vehicleTypeId);
    PositioningModel save(PositioningModel positioning);
    void deleteById(Long id);

}
