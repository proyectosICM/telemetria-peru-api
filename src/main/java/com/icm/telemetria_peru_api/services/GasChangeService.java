package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.GasChangeDTO;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasChangeRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GasChangeService {
    private final GasChangeRepository gasChangeRepository;
    private final VehicleRepository vehicleRepository;

    public Optional<GasChangeModel> findById(Long gasChangeId) {
        return gasChangeRepository.findById(gasChangeId);
    }

    public List<GasChangeModel> findByVehicleModelId(Long vehicleId) {
        return gasChangeRepository.findByVehicleModelId(vehicleId);
    }

    public Page<GasChangeModel> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return gasChangeRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public GasChangeModel saveFromDTO(GasChangeDTO gasChangeDTO) {
        GasChangeModel gasChangeModel = gasChangeDTO.toGasChangeModel();

        Optional<VehicleModel> vehicleModelOptional = vehicleRepository.findById(gasChangeDTO.getVehicleModelId());
        if (vehicleModelOptional.isEmpty()) {
            throw new IllegalArgumentException("VehicleModel with ID " + gasChangeDTO.getVehicleModelId() + " not found");
        }

        gasChangeModel.setVehicleModel(vehicleModelOptional.get());

        return gasChangeRepository.save(gasChangeModel);
    }

    public GasChangeModel save(GasChangeModel gasChangeModel){
        return gasChangeRepository.save(gasChangeModel);
    }

    public void deleteById(Long id){
        gasChangeRepository.deleteById(id);
    }
}