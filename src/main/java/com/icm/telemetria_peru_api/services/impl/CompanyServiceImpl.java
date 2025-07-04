package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.repositories.CompanyRepository;
import com.icm.telemetria_peru_api.services.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    @Override
    public CompanyModel findById(Long id){
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    @Override
    public List<CompanyModel> findAll(){
        return companyRepository.findAll();
    }

    @Override
    public Page<CompanyModel> findAll(Pageable pageable){
        return companyRepository.findAll(pageable);
    }

    @Override
    public List<CompanyModel> findByStatus(Boolean status){
        return companyRepository.findByStatus(status);
    }

    @Override
    public Page<CompanyModel> findByStatus(Boolean status, Pageable pageable){
        return companyRepository.findByStatus(status, pageable);
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    @Override
    public CompanyModel save(@Valid CompanyModel companyModel){
        return companyRepository.save(companyModel);
    }

    @Override
    public CompanyModel update(Long companyId, @Valid CompanyModel companyModel) {
        CompanyModel existing = findById(companyId);
        existing.setName(companyModel.getName());
        return companyRepository.save(existing);
    }

    @Override
    public CompanyModel changeStatus(Long companyId) {
        CompanyModel existing = findById(companyId);
        existing.setStatus(!existing.getStatus());
        return companyRepository.save(existing);
    }

    @Override
    public void deleteById(Long companyId){
        companyRepository.deleteById(companyId);
    }
}
