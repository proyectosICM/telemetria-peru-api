package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.TireSensorModel;
import com.icm.telemetria_peru_api.repositories.TireSensorRepository;
import com.icm.telemetria_peru_api.services.TireSensorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TireSensorServiceImpl implements TireSensorService {
    private final TireSensorRepository tireSensorRepository;

    @Override
    public List<TireSensorModel> findAll() {
        return tireSensorRepository.findAll();
    }

    @Override
    public Page<TireSensorModel> findAll(Pageable pageable) {
        return tireSensorRepository.findAll(pageable);
    }

    @Override
    public TireSensorModel findById(Long id){
        return  tireSensorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    @Override
    public Optional<TireSensorModel> findByIdentificationCode(String code) {
        return tireSensorRepository.findByIdentificationCode(code);
    }

    @Override
    public List<TireSensorModel> findByCompanyModelId(Long companyId) {
        return tireSensorRepository.findByCompanyModelId(companyId);
    }

    @Override
    public Page<TireSensorModel> findByCompanyModelId(Long companyId, Pageable pageable) {
        return tireSensorRepository.findByCompanyModelId(companyId, pageable);
    }

    @Override
    public List<TireSensorModel> findByVehicleModelId(Long vehicleId) {
        return tireSensorRepository.findByVehicleModelId(vehicleId);
    }

    @Override
    public Page<TireSensorModel> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return tireSensorRepository.findByVehicleModelId(vehicleId, pageable);
    }

    @Override
    public List<TireSensorModel> findByStatus(Boolean status) {
        return tireSensorRepository.findByStatus(status);
    }

    @Override
    public Page<TireSensorModel> findByStatus(Boolean status, Pageable pageable) {
        return tireSensorRepository.findByStatus(status, pageable);
    }

    @Override
    public List<TireSensorModel> findByVehicleModelIdAndStatus(Long vehicleId, Boolean status) {
        return tireSensorRepository.findByVehicleModelIdAndStatus(vehicleId, status);
    }

    @Override
    public Page<TireSensorModel> findByVehicleModelIdAndStatusPage(Long vehicleId, Boolean status, Pageable pageable) {
        return tireSensorRepository.findByVehicleModelIdAndStatus(vehicleId, status, pageable);
    }

    @Override
    public TireSensorModel save(TireSensorModel tireSensorModel) {
        return tireSensorRepository.save(tireSensorModel);
    }

    @Override
    public TireSensorModel update(Long id, TireSensorModel tireSensorModel) {
        TireSensorModel existing = findById(id);

        // Actualizar los campos existentes con los nuevos valores
        existing.setIdentificationCode(tireSensorModel.getIdentificationCode());
        existing.setTemperature(tireSensorModel.getTemperature());
        existing.setPressure(tireSensorModel.getPressure());
        existing.setBatteryLevel(tireSensorModel.getBatteryLevel());
        existing.setStatus(tireSensorModel.getStatus());
        existing.setVehicleModel(tireSensorModel.getVehicleModel());
        existing.setCompanyModel(tireSensorModel.getCompanyModel());
        existing.setTirePositioningModel(tireSensorModel.getTirePositioningModel());

        // Guardar el registro actualizado en la base de datos
        return tireSensorRepository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        tireSensorRepository.deleteById(id);
    }
}
