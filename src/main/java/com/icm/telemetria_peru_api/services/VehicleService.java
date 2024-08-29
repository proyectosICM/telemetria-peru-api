package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.DriverRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private MqttPahoMessageHandler mqttOutbound;

    @Autowired
    private DriverRepository driverRepository;

    private VehicleModel getVehicleById(Long vehicleId){
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
    }

    public Optional<VehicleModel> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    /** Retrieves vehicles, as a list and paginated. */
    public List<VehicleModel> findAll() {
        return vehicleRepository.findAll();
    }

    public Page<VehicleModel> findAll(Pageable pageable){
        return vehicleRepository.findAll(pageable);
    }

    /** Retrieves vehicles by status, as a list and paginated. */
    public List<VehicleModel> findByStatus(Boolean status) {
        return vehicleRepository.findByStatus(status);
    }

    public Page<VehicleModel> findByStatus(Boolean status, Pageable pageable){
        return vehicleRepository.findByStatus(status, pageable);
    }

    /** Retrieves vehicles by vehicleType, as a list and paginated. */
    public List<VehicleModel> findByVehicletypeModelId(Long vehicletypeId) {
        return vehicleRepository.findByVehicletypeModelId(vehicletypeId);
    }

    public Page<VehicleModel> findByVehicletypeModelId(Long vehicleTypeId, Pageable pageable){
        return vehicleRepository.findByVehicletypeModelId(vehicleTypeId, pageable);
    }

    /** Retrieves vehicles by company, as a list and paginated. */
    public List<VehicleModel> findByCompanyModelId(Long companyId) {
        return vehicleRepository.findByCompanyModelId(companyId);
    }

    public Page<VehicleModel> findByCompanyModelId(Long companyId, Pageable pageable){
        return vehicleRepository.findByCompanyModelId(companyId, pageable);
    }

    /** Retrieves vehicles by vehicleType and company, as a list and paginated. */
    public List<VehicleModel> findByVehicleTypeModelIdAndCompanyModelId(Long vehicleTypeId, Long companyId) {
        return vehicleRepository.findByVehicletypeModelIdAndCompanyModelId(vehicleTypeId, companyId);
    }

    public Page<VehicleModel> findByVehicleTypeModelIdAndCompanyModelId(Long vehicleTypeId, Long companyId, Pageable pageable){
        return vehicleRepository.findByVehicletypeModelIdAndCompanyModelId(vehicleTypeId, companyId, pageable);
    }

    /** Retrieves vehicles by vehicleType and compan and status, as a list and paginated. */
    public List<VehicleModel> findByVehicleTypeModelIdAndCompanyModelIdAndStatus(Long vehicleTypeId, Long companyId, Boolean status) {
        return vehicleRepository.findByVehicletypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId, status);
    }

    public Page<VehicleModel> findByVehicleTypeModelIdAndCompanyModelIdAndStatus(Long vehicleTypeId, Long companyId, Boolean status, Pageable pageable){
        return vehicleRepository.findByVehicletypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId, status, pageable);
    }

    /** More CRUD methods */
    public VehicleModel save(@Valid VehicleModel vehicleModel){
        return vehicleRepository.save(vehicleModel);
    }

    /**  Main data update */
    public VehicleModel updateMainData(Long vehicleId,@Valid VehicleModel vehicleModel){
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setStatus(vehicleModel.getStatus());
        existing.setLicensePlate(vehicleModel.getLicensePlate());
        existing.setVehicletypeModel(vehicleModel.getVehicletypeModel());
        existing.setCompanyModel(vehicleModel.getCompanyModel());
        return vehicleRepository.save(existing);
    }

    /** Change vehicle driver */
    public VehicleModel changeDriver(Long vehicleId,@Valid Long driverId){
        DriverModel newDriver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver with id " + driverId + " not found"));

        VehicleModel existing = getVehicleById(vehicleId);
        existing.setDriverModel(newDriver);
        return vehicleRepository.save(existing);
    }

    /** Update vehicle location */
    public VehicleModel changeLocation(Long vehicleId,@Valid BigDecimal longitud, @Valid BigDecimal latitud){
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setLongitud(longitud);
        existing.setLatitud(latitud);
        return vehicleRepository.save(existing);
    }

    /** Update vehicle alarm status */
    public VehicleModel alarmStatusUpdate(Long vehicleId, @Valid Boolean alarm) {
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setAlarmStatus(alarm);
        vehicleRepository.save(existing);

        // Publicar el estado de la alarma en un tema específico
        String topic = "tmp_remoteOptions";
        String payload = "alarm:" + alarm;

        mqttOutbound.handleMessage(MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build());

        return existing;
    }

    /** Update vehicle engine status */
    public VehicleModel engineStatusUpdate(Long vehicleId,@Valid Boolean engine){
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setEngineStatus(engine);
        vehicleRepository.save(existing);

        // Publicar el estado de la alarma en un tema específico
        String topic = "tmp_remoteOptions";
        String payload = "engine:" + engine;

        mqttOutbound.handleMessage(MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build());
        return existing;
    }

    /** Update vehicle lock status */
    public VehicleModel lockUpdate(Long vehicleId,@Valid Boolean lock){
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setLockStatus(lock);
        vehicleRepository.save(existing);

        // Publicar el estado de la alarma en un tema específico
        String topic = "tmp_remoteOptions";
        String payload = "lock:" + lock;

        mqttOutbound.handleMessage(MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build());
        return existing;
    }

    /** Update vehicle speed */
    public VehicleModel speedUpdate(Long vehicleId,@Valid Integer speed){
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setSpeed(speed);
        return vehicleRepository.save(existing);
    }

    /** Update vehicle time on */
    public VehicleModel timeOnUpdate(Long vehicleId, @Valid Long timeOn){
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setTimeOn(timeOn);
        return vehicleRepository.save(existing);
    }

    /** Toggle vehicle status */
    public VehicleModel changeStatus(Long vehicleId){
        VehicleModel existing = getVehicleById(vehicleId);
        existing.setStatus(!existing.getStatus());
        return vehicleRepository.save(existing);
    }

    public void deleteById(Long vehicleId){
        vehicleRepository.deleteById(vehicleId);
    }
}
