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
        // Zona horaria del servidor
        ZoneId serverZoneId = ZoneId.of("America/Lima"); // Cambia según la zona horaria del servidor.

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Fuel Efficiency");

            // Crear la cabecera
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Estado", "Placa", "Día", "Hora de inicio", "Hora de fin", "Horas acumuladas",
                    "Combustible inicial", "Combustible final", "Combustible Consumido",
                    "Rendimiento Combustible (x KM)", "Rendimiento Combustible (gal/h)", "Coordenadas Final"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]); // Encabezados son siempre cadenas
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }

            // Llenar datos
            int rowIdx = 1;
            for (FuelEfficiencyModel model : data) {
                Row row = sheet.createRow(rowIdx++);

                // Ajustar las fechas a la zona horaria del servidor
                ZonedDateTime startTime = model.getStartTime().withZoneSameInstant(serverZoneId);
                ZonedDateTime endTime = model.getEndTime() != null ? model.getEndTime().withZoneSameInstant(serverZoneId) : null;

                // Validar si el modelo de vehículo y el tipo de combustible son válidos
                VehicleModel vehicleModel = model.getVehicleModel();
                FuelType fuelType = vehicleModel != null ? vehicleModel.getFuelType() : null;

                // Multiplicar valores de combustible si el tipo es DIESEL
                double conversionFactor = (fuelType != null && fuelType == FuelType.DIESEL) ? 0.264172 : 1.0;

                double initialFuel = model.getInitialFuel() != null ? model.getInitialFuel() * conversionFactor : 0;
                double finalFuel = model.getFinalFuel() != null ? model.getFinalFuel() * conversionFactor : 0;
                double fuelConsumed = model.getFinalFuel() != null ? initialFuel - finalFuel : 0;

                // Redondear a dos decimales
                initialFuel = roundToTwoDecimalPlaces(initialFuel);
                finalFuel = roundToTwoDecimalPlaces(finalFuel);
                fuelConsumed = roundToTwoDecimalPlaces(fuelConsumed);

                row.createCell(0).setCellValue(model.getFuelEfficiencyStatus() != null ? model.getFuelEfficiencyStatus().toString() : "Aún no disponible");
                row.createCell(1).setCellValue(vehicleModel != null ? vehicleModel.getLicensePlate() : "Aún no disponible");
                row.createCell(2).setCellValue(startTime != null ? startTime.toLocalDate().toString() : "Aún no disponible");
                row.createCell(3).setCellValue(startTime != null ? startTime.toLocalTime().toString() : "Aún no disponible");
                row.createCell(4).setCellValue(endTime != null ? endTime.toLocalTime().toString() : "Aún no disponible");
                row.createCell(5).setCellValue(model.getAccumulatedHours() != null ? model.getAccumulatedHours().toString() : "Aún no disponible");
                row.createCell(6).setCellValue(initialFuel); // Combustible inicial (redondeado)
                row.createCell(7).setCellValue(finalFuel); // Combustible final (redondeado)
                row.createCell(8).setCellValue(fuelConsumed); // Combustible Consumido (redondeado)
                row.createCell(9).setCellValue(model.getFuelEfficiency() != null ? model.getFuelEfficiency() : 0);
                row.createCell(10).setCellValue(model.getFuelConsumptionPerHour() != null ? model.getFuelConsumptionPerHour() : 0);
                row.createCell(11).setCellValue(model.getCoordinates() != null ? model.getCoordinates() : "Aún no disponible");
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Método para redondear a dos decimales
    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP); // Redondeo hacia el más cercano
        return bd.doubleValue();
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
