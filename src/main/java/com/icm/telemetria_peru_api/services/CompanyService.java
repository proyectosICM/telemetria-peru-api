package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.CompanyModel;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    CompanyModel findById(Long id);

    List<CompanyModel> findAll();

    Page<CompanyModel> findAll(Pageable pageable);

    List<CompanyModel> findByStatus(Boolean status);

    Page<CompanyModel> findByStatus(Boolean status, Pageable pageable);

    CompanyModel save(CompanyModel companyModel);

    CompanyModel update(Long companyId, CompanyModel companyModel);

    CompanyModel changeStatus(Long companyId);
    void deleteById(Long companyId);
}
