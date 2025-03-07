package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import com.icm.telemetria_peru_api.models.UserModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.ImpactIncidentLoggingRepository;
import com.icm.telemetria_peru_api.repositories.UserRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImpactIncidentLoggingService {
    private final ImpactIncidentLoggingRepository impactIncidentLoggingRepository;
    private final MqttMessagePublisher mqttMessagePublisher;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

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
        sendEmailInfo(impactIncidentLoggingModel.getVehicleModel().getId());
        return impactIncidentLoggingRepository.save(impactIncidentLoggingModel);
    }


    public void sendEmailInfo(Long vehicleId) {
        Optional<VehicleModel> vehicleModelOptional = vehicleRepository.findById(vehicleId);

        if (vehicleModelOptional.isPresent()) {
            VehicleModel vehicleModel = vehicleModelOptional.get();

            CompanyModel companyModel = vehicleModel.getCompanyModel();
            List<UserModel> userModels = userRepository.findByCompanyModelId(companyModel.getId());

            String[] emails = userModels.stream()
                    .map(UserModel::getEmail)
                    .toArray(String[]::new);

            if (emails.length > 0) {
                String subject = "Incidente de impacto";
                String message = "El vehículo con placa " + vehicleModel.getLicensePlate() + " A sufrido un incidente de impacto. Por favor, verifique el estado del vehículo.";

                try {
                    emailService.sendEmail(emails, subject, message);
                    //System.out.println("Mail sent to: " + Arrays.toString(emails));
                } catch (Exception e) {
                    System.err.println("Error sending email: " + e.getMessage());
                }
            } else {
                System.out.println("No users associated with the vehicle company were found.");
            }
        } else {
            System.out.println("A vehicle with the provided ID was not found.");
        }
    }

    public void deleteById(Long id){
        impactIncidentLoggingRepository.deleteById(id);
    }
}
