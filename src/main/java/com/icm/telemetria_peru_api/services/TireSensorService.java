package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.TireSensorModel;
import com.icm.telemetria_peru_api.repositories.TireSensorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TireSensorService {
    @Autowired
    private TireSensorRepository tireSensorRepository;
    public List<TireSensorModel> findAll() {
        return tireSensorRepository.findAll();
    }

    public Page<TireSensorModel> findAll(Pageable pageable) {
        return tireSensorRepository.findAll(pageable);
    }

    public TireSensorModel findById(Long id){
        return  tireSensorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public Optional<TireSensorModel> findByIdentificationCode(String code) {
        return tireSensorRepository.findByIdentificationCode(code);
    }

    public List<TireSensorModel> findByCompanyModelId(Long companyId) {
        return tireSensorRepository.findByCompanyModelId(companyId);
    }

    public Page<TireSensorModel> findByCompanyModelId(Long companyId, Pageable pageable) {
        return tireSensorRepository.findByCompanyModelId(companyId, pageable);
    }

    public List<TireSensorModel> findByVehicleModelId(Long vehicleId) {
        return tireSensorRepository.findByVehicleModelId(vehicleId);
    }

    public Page<TireSensorModel> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return tireSensorRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public List<TireSensorModel> findByStatus(Boolean status) {
        return tireSensorRepository.findByStatus(status);
    }

    public Page<TireSensorModel> findByStatus(Boolean status, Pageable pageable) {
        return tireSensorRepository.findByStatus(status, pageable);
    }

    public List<TireSensorModel> findByVehicleModelIdAndStatus(Long vehicleId, Boolean status) {
        return tireSensorRepository.findByVehicleModelIdAndStatus(vehicleId, status);
    }

    public Page<TireSensorModel> findByVehicleModelIdAndStatus(Long vehicleId, Boolean status, Pageable pageable) {
        return tireSensorRepository.findByVehicleModelIdAndStatus(vehicleId, status, pageable);
    }

    public TireSensorModel save(TireSensorModel tireSensorModel) {
        return tireSensorRepository.save(tireSensorModel);
    }

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
        existing.setPositioningModel(tireSensorModel.getPositioningModel());

        // Guardar el registro actualizado en la base de datos
        return tireSensorRepository.save(existing);
    }

    public void deleteById(Long id) {
        tireSensorRepository.deleteById(id);
    }
}
