package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.FuelEfficiencyDTO;
import com.icm.telemetria_peru_api.dto.FuelEfficiencySummaryDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelEfficiencyService {

    private final FuelEfficiencyRepository fuelEfficiencyRepository;
    private final MqttMessagePublisher mqttMessagePublisher;
    private final VehicleRepository vehicleRepository;

    public Optional<FuelEfficiencyModel> findById(Long id) {
        return fuelEfficiencyRepository.findById(id);
    }

    public List<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId) {
        return fuelEfficiencyRepository.findByVehicleModelIdOrderByCreatedAtDesc(vehicleId);
    }

    public byte[] generateExcel(List<FuelEfficiencyModel> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Fuel Efficiency");

            // Crear la cabecera
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Estado", "Placa", "Día", "Hora de inicio"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            // Llenar datos
            int rowIdx = 1;
            for (FuelEfficiencyModel model : data) {
                Row row = sheet.createRow(rowIdx++);

                // Convertir la fecha creada (createdAt) a epoch timestamp con nanosegundos
                ZonedDateTime createdAt = model.getCreatedAt();
                String formattedTimestamp = createdAt.toEpochSecond() + "." + createdAt.getNano();

                // Extraer el día y la hora de inicio a partir de la fecha
                String day = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String time = createdAt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                row.createCell(0).setCellValue(model.getFuelEfficiencyStatus().toString()); // Estado
                row.createCell(1).setCellValue(model.getVehicleModel().getLicensePlate().toString()); // Placa
                row.createCell(2).setCellValue(day); // Día
                row.createCell(3).setCellValue(time); // Hora de inicio
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerCellStyle.setFont(font);
        return headerCellStyle;
    }

    public List<FuelEfficiencyDTO> findByVehicleModelId2(Long vehicleId) {
        List<FuelEfficiencyModel> records = fuelEfficiencyRepository.findByVehicleModelIdOrderByCreatedAtDesc(vehicleId);

        if (records.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron registros para el vehículo con ID " + vehicleId);
        }

        return records.stream()
                .map(FuelEfficiencyDTO::new)
                .toList();
    }
/*
    public Page<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return fuelEfficiencyRepository.findByVehicleModelId(vehicleId, pageable);
    }
    */

    public Page<FuelEfficiencyDTO> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        Page<FuelEfficiencyModel> records = fuelEfficiencyRepository.findByVehicleModelId(vehicleId, pageable);

        if (records.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron registros para el vehículo con ID " + vehicleId);
        }

        return records.map(FuelEfficiencyDTO::new);
    }

    /**
     * STAST
     */

    public List<Map<String, Object>> getDailyAveragesForMonth(Long vehicleId, Integer month, Integer year) {
        return fuelEfficiencyRepository.findDailyAveragesForMonth(vehicleId, month, year);
    }

    public List<Map<String, Object>> getMonthlyAveragesForYear(Long vehicleId, String status, Integer year) {
        return fuelEfficiencyRepository.findMonthlyAveragesForYear(vehicleId, status, year);
    }


    public List<FuelEfficiencySummaryDTO> getFuelEfficiencyByVehicleAndTime(
            Long vehicleId, Integer year, Integer month, Integer day) {

        List<Object[]> results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(vehicleId, year, month, day);
        List<FuelEfficiencySummaryDTO> summaries = new ArrayList<>();

        for (Object[] row : results) {
            summaries.add(new FuelEfficiencySummaryDTO(
                    FuelEfficiencyStatus.valueOf((String) row[0]), // status
                    (Double) row[1], // totalHours
                    (Double) row[2], // totalFuelConsumed
                    (Double) row[3]  // avgFuelEfficiency
            ));
        }

        return summaries;
    }

    /**
     * STAST
     */

    public List<FuelEfficiencySummaryDTO> getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(Long vehicleId, Integer year, Integer month, Integer day) {
        List<Object[]> results;

        if (year != null && month == null && day == null) {
            results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByYear(vehicleId, year);
        } else if (year != null && month != null && day == null) {
            results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByMonth(vehicleId, month, year);
        } else if (year != null && month != null && day != null) {
            results = fuelEfficiencyRepository.getAggregatedFuelEfficiencyByDay(vehicleId, day, month, year);
        } else {
            results = null;
        }

        Optional<VehicleModel> vehicleModel = vehicleRepository.findById(vehicleId);

        if (results != null && !results.isEmpty()) {
            List<FuelEfficiencySummaryDTO> summaries = results.stream().map(result -> {
                FuelEfficiencyStatus status = FuelEfficiencyStatus.valueOf(result[0].toString());
                Double totalHours = Math.max(0.0, Double.valueOf(result[1].toString()));
                Double totalFuelConsumed = Math.max(0.0, Double.valueOf(result[2].toString()));
                Double avgFuelEfficiency = Math.max(0.0, Double.valueOf(result[3].toString()));

                if (vehicleModel.isPresent()) {
                    switch (vehicleModel.get().getFuelType()) {
                        case DIESEL:
                            totalFuelConsumed *= 0.264172;
                            avgFuelEfficiency *= 0.264172;
                            break;
                    }
                }

                return new FuelEfficiencySummaryDTO(status, totalHours, totalFuelConsumed, avgFuelEfficiency);
            }).collect(Collectors.toList());

            return summaries;
        } else {
            return getDefaultSummary();
        }
    }

    private static final List<FuelEfficiencySummaryDTO> defaultSummaries = Arrays.asList(
            new FuelEfficiencySummaryDTO(FuelEfficiencyStatus.ESTACIONADO, 0.0, 0.0, 0.0),
            new FuelEfficiencySummaryDTO(FuelEfficiencyStatus.OPERACION, 0.0, 0.0, 0.0),
            new FuelEfficiencySummaryDTO(FuelEfficiencyStatus.RALENTI, 0.0, 0.0, 0.0)
    );

    public List<FuelEfficiencySummaryDTO> getDefaultSummary() {
        return defaultSummaries;
    }


    public FuelEfficiencyModel save(FuelEfficiencyModel fuelEfficiencyModel) {

        FuelEfficiencyModel savedData = fuelEfficiencyRepository.save(fuelEfficiencyModel);
        if (savedData.getVehicleModel() != null) {
            mqttMessagePublisher.fuelEfficient(savedData.getId(), savedData.getVehicleModel().getId());
        } else {
            System.err.println("VehicleModel es nulo, no se puede enviar el mensaje MQTT.");
        }
        return savedData;
    }

    public void deleteById(Long id) {
        fuelEfficiencyRepository.deleteById(id);
    }
}
