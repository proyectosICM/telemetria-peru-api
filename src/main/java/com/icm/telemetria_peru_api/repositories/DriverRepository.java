package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.DriverModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<DriverModel, Long> {

    boolean existsByRfid(String rfid);
    /** Retrieves drivers by status, as a list and paginated. */
    List<DriverModel> findByStatus(Boolean status);
    Page<DriverModel> findByStatus(Boolean status, Pageable pageable);

    /** Retrieves drivers by company, as a list and paginated. */
    List<DriverModel> findByCompanyModelId(Long companyId);
    Page<DriverModel> findByCompanyModelId(Long companyId, Pageable pageable);

    /** Retrieves drivers by company and status, as a list and paginated. */
    List<DriverModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status);
    Page<DriverModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status, Pageable pageable);
}
