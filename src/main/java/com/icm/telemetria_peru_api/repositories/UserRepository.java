package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.UserModel;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    Optional<UserModel> findByUsername(String username);

    /** Retrieves users, as a list and paginated. */
    List<UserModel> findByStatus(Boolean status);
    Page<UserModel> findByStatus(Boolean status, Pageable pageable);

    /** Retrieves users by company, as a list and paginated. */
    List<UserModel> findByCompanyModelId(Long companyId);
    Page<UserModel> findByCompanyModelId(Long companyId, Pageable pageable);

    /** Retrieves users by company and status, as a list and paginated. */
    List<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status);
    Page<UserModel> findByCompanyModelIdAndStatus(Long companyId, Boolean status, Pageable pageable);

    /**
     * Deletes all UserModel entries associated with the specified company ID.
     * This operation is transactional, meaning that it will be executed within a transaction
     * context and can be rolled back if an error occurs during the execution.
     *
     * @param companyId the ID of the company whose users should be deleted
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserModel u WHERE u.companyModel.id = :companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);
}
