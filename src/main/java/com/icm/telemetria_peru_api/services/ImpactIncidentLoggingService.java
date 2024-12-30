package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import com.icm.telemetria_peru_api.repositories.ImpactIncidentLoggingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImpactIncidentLoggingService {
    @Autowired
    private final ImpactIncidentLoggingRepository impactIncidentLoggingRepository;
    private final MqttMessagePublisher mqttMessagePublisher;

    public List<ImpactIncidentLoggingModel> findAll(){
        return impactIncidentLoggingRepository.findAll();
    }

    public Page<ImpactIncidentLoggingModel> findAll(Pageable pageable){
        return impactIncidentLoggingRepository.findAll(pageable);
    }

    public ImpactIncidentLoggingModel findById(Long id){
        return impactIncidentLoggingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<ImpactIncidentLoggingModel> findByVehicleId(Long vehicleId){
        return impactIncidentLoggingRepository.findByVehicleModelId(vehicleId);
    }

    public Page<ImpactIncidentLoggingModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return impactIncidentLoggingRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /** More CRUD methods **/
    public ImpactIncidentLoggingModel save(ImpactIncidentLoggingModel impactIncidentLoggingModel){
        mqttMessagePublisher.ImpactIncident(impactIncidentLoggingModel.getVehicleModel().getId());
        return impactIncidentLoggingRepository.save(impactIncidentLoggingModel);
    }

    public void deleteById(Long id){
        impactIncidentLoggingRepository.deleteById(id);
    }
}
