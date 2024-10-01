package com.icm.telemetria_peru_api.repositories;

import com.icm.telemetria_peru_api.models.ImagesCLModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagesCLRepository extends JpaRepository<ImagesCLModel, Long> {
    List<ImagesCLModel> findByChecklistRecordModelId(Long id);
    Page<ImagesCLModel> findByChecklistRecordModelId(Long id, Pageable pageable);
}
