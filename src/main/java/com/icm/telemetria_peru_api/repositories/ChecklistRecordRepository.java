package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistRecordRepository extends JpaRepository<ChecklistRecordModel, Long> {
    List<ChecklistRecordModel> findByVehicleModelId(Long vehicleId);
    Page<ChecklistRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable);

    List<ChecklistRecordModel> findByCompanyModelId(Long companyId);
    Page<ChecklistRecordModel> findByCompanyModelId(Long companyId, Pageable pageable);
}
