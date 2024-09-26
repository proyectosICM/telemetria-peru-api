package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.ChecklistTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistTypeRepository extends JpaRepository<ChecklistTypeModel, Long> {
}
