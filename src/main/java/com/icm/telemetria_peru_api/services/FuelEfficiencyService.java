package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelEfficiencyService {

    private final FuelEfficiencyRepository fuelEfficiencyRepository;
    private final MqttMessagePublisher mqttMessagePublisher;
    public List<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId){
        return fuelEfficiencyRepository.findByVehicleModelId(vehicleId);
    }

    public Page<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId, Pageable pageable){
        return fuelEfficiencyRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public FuelEfficiencyModel save(FuelEfficiencyModel fuelEfficiencyModel){

        FuelEfficiencyModel savedData = fuelEfficiencyRepository.save(fuelEfficiencyModel);
        if (savedData.getVehicleModel() != null) {
            mqttMessagePublisher.fuelEfficient(savedData.getId(), savedData.getVehicleModel().getId());
        } else {
            System.err.println("VehicleModel es nulo, no se puede enviar el mensaje MQTT.");
        }
        return savedData;
    }
}
