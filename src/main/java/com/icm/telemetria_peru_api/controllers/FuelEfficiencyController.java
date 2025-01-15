package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.FuelEfficiencyDTO;
import com.icm.telemetria_peru_api.dto.FuelEfficiencySummaryDTO;
import com.icm.telemetria_peru_api.integration.mqtt.MqttMessagePublisher;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/fuel-efficiency")
@RequiredArgsConstructor
public class FuelEfficiencyController {
    private final FuelEfficiencyService fuelEfficiencyService;
    private final MqttMessagePublisher mqttMessagePublisher;


    @GetMapping("/{id}")
    public ResponseEntity<Optional<FuelEfficiencyModel>> findById(@PathVariable Long id){
        Optional<FuelEfficiencyModel> data = fuelEfficiencyService.findById(id);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/download-excel/{vehicleModelId}")
    public ResponseEntity<byte[]> exportFuelEfficiencyToExcel(@PathVariable Long vehicleModelId) {
        try {
            // Obtener los datos necesarios para el Excel
            List<FuelEfficiencyModel> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId);

            // Generar el archivo Excel
            byte[] excelFile = fuelEfficiencyService.generateExcel(data);

            // Configurar los encabezados de respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("fuel_efficiency.xlsx")
                    .build());

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Manejo de errores: devolver una respuesta adecuada
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/by-vehicle/{vehicleModelId}")
    public ResponseEntity<List<FuelEfficiencyModel>> findByVehicleModelId(
            @PathVariable Long vehicleModelId) {
        List<FuelEfficiencyModel> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/by-vehicle-paged/{vehicleModelId}")
    public ResponseEntity<Page<FuelEfficiencyDTO>> findByVehicleModelId(
            @PathVariable Long vehicleModelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FuelEfficiencyDTO> data = fuelEfficiencyService.findByVehicleModelId(vehicleModelId, pageable);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    /** STAST */

    @GetMapping("/daily-averages/{vehicleId}")
    public List<Map<String, Object>> getDailyAveragesForMonth(@PathVariable Long vehicleId,
                                                              @RequestParam Integer month,
                                                              @RequestParam(defaultValue = "") Integer year) {
        if (year == null || year == 0) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        return fuelEfficiencyService.getDailyAveragesForMonth(vehicleId, month, year);
    }

    @GetMapping("/monthly-averages/{vehicleId}")
    public List<Map<String, Object>> getMonthlyAveragesForYear(@PathVariable Long vehicleId,
                                                                      @RequestParam String status,
                                                                      @RequestParam(defaultValue = "") Integer year) {
        if (year == null || year == 0) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        return fuelEfficiencyService.getMonthlyAveragesForYear(vehicleId, status, year);
    }

    @GetMapping("/summary/{vehicleId}")
    public ResponseEntity<List<FuelEfficiencySummaryDTO>> getFuelEfficiencyByVehicle(
            @PathVariable Long vehicleId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day) {

        List<FuelEfficiencySummaryDTO> summary = fuelEfficiencyService.getAggregatedFuelEfficiencyByVehicleIdAndTimeFilter(vehicleId, year, month, day);
        return ResponseEntity.ok(summary);
    }
    /** STAST */
    @PostMapping
    public ResponseEntity<FuelEfficiencyModel> save(@RequestBody FuelEfficiencyModel fuelEfficiencyModel){
        FuelEfficiencyModel saveData = fuelEfficiencyService.save(fuelEfficiencyModel);
        return new ResponseEntity<>(saveData, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FuelEfficiencyModel> delete(@PathVariable Long id){
        fuelEfficiencyService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
