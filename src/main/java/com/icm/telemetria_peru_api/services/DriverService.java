package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.repositories.DriverRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    public Optional<DriverModel> findById(Long driverId){
        return driverRepository.findById(driverId);
    }

    /** Retrieves drivers, as a list and paginated. */
    public List<DriverModel> findAll(){
        return driverRepository.findAll();
    }
    public Page<DriverModel> findAll(Pageable pageable){
        return driverRepository.findAll(pageable);
    }

    /** Retrieves drivers by status, as a list and paginated. */
    public List<DriverModel> findByStatus(Boolean status){
        return driverRepository.findByStatus(status);
    }
    public Page<DriverModel> findByStatus(Boolean status, Pageable pageable){
        return driverRepository.findByStatus(status, pageable);
    }

    /** Retrieves drivers by company, as a list and paginated. */
    public List<DriverModel> findByCompanyModelId(Long companyId){
        return driverRepository.findByCompanyModelId(companyId);
    }
    public Page<DriverModel> findByCompanyModelId(Long companyId, Pageable pageable){
        return driverRepository.findByCompanyModelId(companyId, pageable);
    }

    /** Retrieves drivers by company and status, as a list and paginated. */
    public List<DriverModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status){
        return driverRepository.findByCompanyModelIdAndStatus(companyId, status);
    }

    public Page<DriverModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status, Pageable pageable){
        return driverRepository.findByCompanyModelIdAndStatus(companyId, status, pageable);
    }

    /** More CRUD methods */
    public DriverModel save(@Valid DriverModel driverModel){
        return driverRepository.save(driverModel);
    }

    /**  Main data update */
    public DriverModel updateMainData(Long driverId,@Valid DriverModel driverModel){
        return driverRepository.findById(driverId)
                .map(existing -> {
                    existing.setName(driverModel.getName());
                    existing.setLastName(driverModel.getLastName());
                    existing.setDriverLicense(driverModel.getDriverLicense());
                    existing.setLicenseIssueDate(driverModel.getLicenseIssueDate());
                    existing.setLicenseExpireDate(driverModel.getLicenseExpireDate());
                    existing.setDriverPhoneNumber(driverModel.getDriverPhoneNumber());
                    return driverRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("Driver with id " + driverId + " not found"));
    }

    /**  RFID update */
    public DriverModel updateRFID(Long driverId,@Valid String newRFId){
        return driverRepository.findById(driverId)
                .map(existing ->{
                    existing.setRfid(newRFId);
                    return driverRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("Driver with id " + driverId + " not found"));
    }

    /**  status update */
    public DriverModel changeStatus(Long driverId){
        return driverRepository.findById(driverId)
                .map(existing -> {
                    existing.setStatus(!existing.getStatus());
                    return driverRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("Driver with id" + driverId + " no found"));
    }
}
