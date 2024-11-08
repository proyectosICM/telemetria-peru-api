package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.VehicleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleModel, Long> {

    Optional<VehicleModel> findByImei(String imei);

    /** Retrieves vehicles by status, as a list and paginated. */
    List<VehicleModel> findByStatus(Boolean status);
    Page<VehicleModel> findByStatus(Boolean status, Pageable pageable);

    /** Retrieves vehicles by vehicleType, as a list and paginated. */
    List<VehicleModel> findByVehicletypeModelId(Long vehicleTypeId);
    Page<VehicleModel> findByVehicletypeModelId(Long vehicleTypeId, Pageable pageable);

    /** Retrieves vehicles by company, as a list and paginated. */
    List<VehicleModel> findByCompanyModelId(Long vehicleTypeId);
    Page<VehicleModel> findByCompanyModelId(Long vehicleTypeId, Pageable pageable);

    /** Retrieves vehicles by vehicleType and company, as a list and paginated. */
    List<VehicleModel> findByVehicletypeModelIdAndCompanyModelId(Long vehicleTypeId, Long companyId);
    Page<VehicleModel> findByVehicletypeModelIdAndCompanyModelId(Long vehicleTypeId, Long companyId, Pageable pageable);

    /** Retrieves vehicles by company, as a list and paginated. */
    List<VehicleModel> findByVehicletypeModelIdAndCompanyModelIdAndStatus(Long vehicleTypeId, Long companyId, Boolean status);
    Page<VehicleModel> findByVehicletypeModelIdAndCompanyModelIdAndStatus(Long vehicleTypeId, Long companyId, Boolean status, Pageable pageable);
}
