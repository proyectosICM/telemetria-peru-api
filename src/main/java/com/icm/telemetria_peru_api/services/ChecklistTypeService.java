package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.ChecklistTypeModel;
import com.icm.telemetria_peru_api.repositories.ChecklistTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ChecklistTypeService {
    List<ChecklistTypeModel> findAll();
    Page<ChecklistTypeModel> findAll(Pageable pageable);
    ChecklistTypeModel findById(Long id);
    ChecklistTypeModel save(ChecklistTypeModel checklistType);
    ChecklistTypeModel update(ChecklistTypeModel checklistType, Long id);
    void deleteById(Long id);
}
