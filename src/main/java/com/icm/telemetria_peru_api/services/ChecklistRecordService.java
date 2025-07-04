package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.ChecklistRecordDTO;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.mappers.ChecklistRecordMapper;
import com.icm.telemetria_peru_api.models.ChecklistRecordModel;
import com.icm.telemetria_peru_api.repositories.ChecklistRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;


public interface ChecklistRecordService {
    ChecklistRecordDTO findById(Long id);
    String getJsonFileContentById(Long id) throws IOException;
    List<ChecklistRecordDTO> findAll();
    Page<ChecklistRecordDTO> findAll(Pageable pageable);
    List<ChecklistRecordDTO> findByVehicleModelId(Long vehicleId);
    Page<ChecklistRecordDTO> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<ChecklistRecordDTO> findByCompanyModelId(Long companyId);
    Page<ChecklistRecordDTO> findByCompanyModelId(Long companyId, Pageable pageable);
    ChecklistRecordModel getLatestChecklistForVehicle(Long vehicleId);
    ChecklistRecordModel saveWithJson(ChecklistRecordModel checklistRecordModel, Map<String, Object> jsonData) throws IOException;
    void deleteById(Long id);
}
