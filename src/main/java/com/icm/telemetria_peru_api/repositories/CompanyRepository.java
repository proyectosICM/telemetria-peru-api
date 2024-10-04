package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.CompanyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  CompanyRepository extends JpaRepository<CompanyModel, Long> {
    List<CompanyModel> findByStatus(Boolean status);
    Page<CompanyModel> findByStatus(Boolean status, Pageable pageable);
}
