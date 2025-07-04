package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.TirePositioningModel;
import com.icm.telemetria_peru_api.repositories.TirePositioningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;


public interface TirePositioningService {
    List<TirePositioningModel> findByVehicleId(Long vehicleId);
    Page<TirePositioningModel> findByVehicleId(Long vehicleId, Pageable pageable);
    Optional<TirePositioningModel> findById(Long id);
}
