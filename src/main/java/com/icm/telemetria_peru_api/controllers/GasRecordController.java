package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.services.GasRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/gas-records")
@RequiredArgsConstructor
public class GasRecordController {
    private final GasRecordService gasRecordService;

    @GetMapping("/{id}")
    public ResponseEntity<Optional<GasRecordModel>> findById(@PathVariable Long id){
        try{
            Optional<GasRecordModel> gasRecord = gasRecordService.findById(id);
            return new ResponseEntity<>(gasRecord, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(Optional.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    private ResponseEntity<List<GasRecordModel>> findByVehicleId(@PathVariable Long vehicleId) {
        try {

            List<GasRecordModel> records = gasRecordService.findByVehicleId(vehicleId);

            if (records.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(records, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-vehicle-paged/{vehicleId}")
    public ResponseEntity<Page<GasRecordModel>> findByVehicleId(@PathVariable Long vehicleId,
                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<GasRecordModel> records = gasRecordService.findByVehicleId(vehicleId, pageable);

            if (records.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(records);

        } catch (DataAccessException e) { // Error en la BD
            System.out.println("Error de base de datos al obtener registros de gas para vehicleId: " + vehicleId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) { // Otros errores inesperados
            System.out.println("Error inesperado al obtener registros de gas para vehicleId: " + vehicleId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-vehicle-all-ordered/{vehicleId}")
    public List<GasRecordModel> getGasRecordsOrderedByVehicle(@PathVariable Long vehicleId) {
        return gasRecordService.findByVehicleIdOrdered(vehicleId);
    }

    @GetMapping("/by-vehicle-today/{vehicleId}")
    public List<GasRecordModel> getTodayGasRecordsByVehicle(@PathVariable Long vehicleId) {
        return gasRecordService.findTodayByVehicleId(vehicleId);
    }

    @PostMapping
    public ResponseEntity<GasRecordModel> save(@RequestBody GasRecordModel gasRecordModel) {
        try {
            GasRecordModel saveRecord = gasRecordService.save(gasRecordModel);
            return new ResponseEntity<>(saveRecord, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GasRecordModel> deleteById(@PathVariable Long id){
        try{
            gasRecordService.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
