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

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    public CompanyModel findById(Long id){
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<CompanyModel> findAll(){
        return companyRepository.findAll();
    }

    public Page<CompanyModel> findAll(Pageable pageable){
        return companyRepository.findAll(pageable);
    }

    public List<CompanyModel> findByStatus(Boolean status){
        return companyRepository.findByStatus(status);
    }

    public Page<CompanyModel> findByStatus(Boolean status, Pageable pageable){
        return companyRepository.findByStatus(status, pageable);
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    public CompanyModel save(@Valid CompanyModel companyModel){
        return companyRepository.save(companyModel);
    }

    public CompanyModel update(Long companyId, @Valid CompanyModel companyModel) {
        CompanyModel existing = findById(companyId);
        existing.setName(companyModel.getName());
        return companyRepository.save(existing);
    }

    public CompanyModel changeStatus(Long companyId) {
        CompanyModel existing = findById(companyId);
        existing.setStatus(!existing.getStatus());
        return companyRepository.save(existing);
    }

    public void deleteById(Long companyId){
        companyRepository.deleteById(companyId);
    }
}
