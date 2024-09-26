package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.ChecklistTypeModel;
import com.icm.telemetria_peru_api.repositories.ChecklistTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChecklistTypeService {
    @Autowired
    private ChecklistTypeRepository checklistTypeRepository;

    public List<ChecklistTypeModel> findAll() {return checklistTypeRepository.findAll();}

    public Page<ChecklistTypeModel> findAll(Pageable pageable){
        return checklistTypeRepository.findAll(pageable);
    }

    public ChecklistTypeModel findById(Long id) {
        return checklistTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public ChecklistTypeModel save(ChecklistTypeModel checklistType){
        return checklistTypeRepository.save(checklistType);
    }

    public ChecklistTypeModel update(ChecklistTypeModel checklistType, Long id){
        ChecklistTypeModel existing = findById(id);
        existing.setName(checklistType.getName());
        return checklistTypeRepository.save(existing);
    }

    public void deleteById(Long id) {
        checklistTypeRepository.deleteById(id);
    }
}
