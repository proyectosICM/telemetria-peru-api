package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.repositories.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    private CompanyModel getCompanyById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company with id " + companyId + " not found"));
    }

    public Optional<CompanyModel> findById(Long companyId){
        return companyRepository.findById(companyId);
    }

    /** Retrieves companies, as a list and paginated. */
    public List<CompanyModel> findAll(){
        return companyRepository.findAll();
    }
    public Page<CompanyModel> findAll(Pageable pageable){
        return companyRepository.findAll(pageable);
    }

    /** Retrieves companies by status, as a list and paginated. */
    public List<CompanyModel> findByStatus(Boolean status){
        return companyRepository.findByStatus(status);
    }

    public Page<CompanyModel> findByStatus(Boolean status, Pageable pageable){
        return companyRepository.findByStatus(status, pageable);
    }

    /** More CRUD methods */
    public CompanyModel save(@Valid CompanyModel companyModel){
        return companyRepository.save(companyModel);
    }

    public CompanyModel update(Long companyId, @Valid CompanyModel companyModel) {
        CompanyModel existing = getCompanyById(companyId);
        existing.setName(companyModel.getName());
        return companyRepository.save(existing);
    }

    public CompanyModel changeStatus(Long companyId) {
        CompanyModel existing = getCompanyById(companyId);
        existing.setStatus(!existing.getStatus());
        return companyRepository.save(existing);
    }

    public void deleteById(Long companyId){
        companyRepository.deleteById(companyId);
    }
}
