package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.FuelEfficiencyDTO;
import com.icm.telemetria_peru_api.dto.FuelEfficiencySummaryDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.enums.FuelType;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public interface FuelEfficiencyService {
    Optional<FuelEfficiencyModel> findById(Long id);
    List<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId);
    byte[] generateExcel(List<FuelEfficiencyModel> data) throws IOException;
    public List<FuelEfficiencyDTO> findByVehicleModelId2(Long vehicleId);
    Page<FuelEfficiencyDTO> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<Map<String, Object>> getDailyAveragesForMonth(Long vehicleId, Integer month, Integer year);
    List<Map<String, Object>> getMonthlyAveragesForYear(Long vehicleId, String status, Integer year);
    List<FuelEfficiencySummaryDTO> getFuelEfficiencyByVehicleAndTime(Long vehicleId, Integer year, Integer month, Integer day);
    List<FuelEfficiencySummaryDTO>   getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(Long vehicleId, Integer year, Integer month, Integer day);
    List<FuelEfficiencySummaryDTO> getDefaultSummary();
    FuelEfficiencyModel save(FuelEfficiencyModel fuelEfficiencyModel);
    void deleteById(Long id);
    void deleteInvisibleRecords();

}
