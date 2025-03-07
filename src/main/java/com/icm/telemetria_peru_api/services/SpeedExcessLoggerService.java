package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import com.icm.telemetria_peru_api.models.UserModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
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
public class SpeedExcessLoggerService {
    private final SpeedExcessLoggerRepository speedExcessLoggerRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

    public List<SpeedExcessLoggerModel> findAll(){
        return speedExcessLoggerRepository.findAll();
    }

    public Page<SpeedExcessLoggerModel> findAll(Pageable pageable){
        return speedExcessLoggerRepository.findAll(pageable);
    }

    public SpeedExcessLoggerModel findById(Long id){
        return speedExcessLoggerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<SpeedExcessLoggerModel> findByVehicleId(Long vehicleId){
        return speedExcessLoggerRepository.findByVehicleModelId(vehicleId);
    }

    public Page<SpeedExcessLoggerModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return speedExcessLoggerRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /** More CRUD methods **/
    public SpeedExcessLoggerModel save(SpeedExcessLoggerModel speedExcessLoggerModel){
        sendEmailInfo(speedExcessLoggerModel.getVehicleModel().getId());
        return speedExcessLoggerRepository.save(speedExcessLoggerModel);
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
                String subject = "Velocidad maxima exedida ";
                String message = "El vehículo con placa  " + vehicleModel.getLicensePlate() + " a excedido la velocidad máxima permitida.";

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
        speedExcessLoggerRepository.deleteById(id);
    }
}
