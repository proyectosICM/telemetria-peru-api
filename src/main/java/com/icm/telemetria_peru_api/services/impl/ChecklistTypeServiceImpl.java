package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.ChecklistTypeModel;
import com.icm.telemetria_peru_api.repositories.ChecklistTypeRepository;
import com.icm.telemetria_peru_api.services.ChecklistTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistTypeServiceImpl implements ChecklistTypeService {
    private final ChecklistTypeRepository checklistTypeRepository;

    @Override
    public List<ChecklistTypeModel> findAll() {return checklistTypeRepository.findAll();}

    @Override
    public Page<ChecklistTypeModel> findAll(Pageable pageable){
        return checklistTypeRepository.findAll(pageable);
    }

    @Override
    public ChecklistTypeModel findById(Long id) {
        return checklistTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    @Override
    public ChecklistTypeModel save(ChecklistTypeModel checklistType){
        return checklistTypeRepository.save(checklistType);
    }

    @Override
    public ChecklistTypeModel update(ChecklistTypeModel checklistType, Long id){
        ChecklistTypeModel existing = findById(id);
        existing.setName(checklistType.getName());
        return checklistTypeRepository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        checklistTypeRepository.deleteById(id);
    }
}
