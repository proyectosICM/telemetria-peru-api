package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    /** Retrieves users, as a list and paginated. */
    List<UserModel> findByStatus(Boolean status);
    Page<UserModel> findByStatus(Boolean status, Pageable pageable);

    /** Retrieves users by company, as a list and paginated. */
    List<UserModel> findByCompanyModelId(Long companyId);
    Page<UserModel> findByCompanyModelId(Long companyId, Pageable pageable);

    /** Retrieves users by company and status, as a list and paginated. */
    List<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status);
    Page<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status, Pageable pageable);
}
