package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.AlternatorDTO;
import com.icm.telemetria_peru_api.mappers.AlternatorMapper;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.repositories.AlternatorRepository;
import com.icm.telemetria_peru_api.utils.DateUtils;
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

public interface AlternatorService {
    List<AlternatorDTO> findByVehicleModelId(Long vehicleId);
    Page<AlternatorDTO> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<Map<String, Object>> getDataMonth(Long vehicleId, Integer year, Integer month);
    AlternatorModel save(AlternatorModel alternatorModel);
}
