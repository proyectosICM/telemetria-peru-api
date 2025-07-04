package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.GasChangeDTO;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasChangeRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import com.icm.telemetria_peru_api.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface GasChangeService {
    Optional<GasChangeModel> findById(Long gasChangeId);
    List<GasChangeModel> findByVehicleModelId(Long vehicleId);
    Page<GasChangeModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
    GasChangeModel saveFromDTO(GasChangeDTO gasChangeDTO);
    GasChangeModel save(GasChangeModel gasChangeModel);
    void deleteById(Long id);
}