package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.BatteryRecordDTO;
import com.icm.telemetria_peru_api.mappers.BatteryRecordMapper;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import com.icm.telemetria_peru_api.repositories.BatteryRecordRepository;
import com.icm.telemetria_peru_api.utils.DateUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


public interface BatteryRecordService {
    BatteryRecordDTO findById(Long id);
    List<BatteryRecordDTO> findAll();
    Page<BatteryRecordDTO> findAll(Pageable pageable);
    List<BatteryRecordDTO> findByBatteryId(Long batteryId);
    Page<BatteryRecordDTO> findByBatteryId(Long batteryId, Pageable pageable);
    List<BatteryRecordDTO> findByBatteryModelVehicleModelId(Long vehicleId);
    Page<BatteryRecordDTO> findByBatteryModelVehicleModelId(Long vehicleId, Pageable pageable);
    List<BatteryRecordDTO> findByBatteryModelVehicleModelIdAndBatteryModelId(Long vehicleId, Long batteryId);
    Page<BatteryRecordDTO> findByBatteryModelVehicleModelIdAndBatteryModelId(Long vehicleId, Long batteryId, Pageable pageable);
    List<Map<String, Object>> getDataMonth(Long vehicleId, Integer year, Integer month);
    BatteryRecordModel save(BatteryRecordModel batteryRecordModel);

}