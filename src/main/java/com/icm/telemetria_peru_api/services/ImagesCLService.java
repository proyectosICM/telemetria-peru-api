package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.ImagesCLModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImagesCLService {
    ImagesCLModel findById(Long id);
    List<ImagesCLModel> findAll();
    Page<ImagesCLModel> findAll(Pageable pageable);
    List<ImagesCLModel> findByChecklistRecord(Long clId);
    Page<ImagesCLModel> findByChecklistRecord(Long clId, Pageable pageable);
    ImagesCLModel save(MultipartFile file, Long clId) throws IOException;
    void deleteById(Long id);
}
