package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.TireSensorModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TireSensorRepository extends JpaRepository<TireSensorModel, Long> {
    Optional<TireSensorModel> findByIdentificationCode(String code);
    List<TireSensorModel> findByCompanyModelId(Long companyId);
    Page<TireSensorModel> findByCompanyModelId(Long companyId, Pageable pageable);
    List<TireSensorModel> findByVehicleModelId(Long vehicle);
    Page<TireSensorModel> findByVehicleModelId(Long vehicle, Pageable pageable);
    List<TireSensorModel> findByStatus(Boolean status);
    Page<TireSensorModel> findByStatus(Boolean status, Pageable pageable);

    List<TireSensorModel> findByVehicleModelIdAndStatus(Long vehicleId, Boolean status);

    Page<TireSensorModel> findByVehicleModelIdAndStatus(Long vehicleId, Boolean status, Pageable pageable);


}
