package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.services.FuelRecordService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/fuel-records")
@RequiredArgsConstructor
public class FuelRecordController {
    private final FuelRecordService fuelRecordService;


    @GetMapping("/hourly-averages/{vehicleId}")
    public List<Map<String, Object>> getHourlyAverages(@RequestParam(required = false) String date, @PathVariable Long vehicleId) {
        LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        return fuelRecordService.getHourlyAveragesByDate(localDate, vehicleId);
    }

    @GetMapping("/week-averages/{vehicleId}")
    public List<Map<String, Object>> findDailyAveragesForLast7Days(@PathVariable Long vehicleId) {
        //LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        return fuelRecordService.findDailyAveragesForLast7Days(vehicleId);
    }

    @GetMapping("/month-averages/{vehicleId}")
    public List<Map<String, Object>> findDailyAveragesForCurrentMonth(@PathVariable Long vehicleId) {
        //LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        return fuelRecordService.findDailyAveragesForCurrentMonth(vehicleId);
    }

    @GetMapping("/year-averages/{vehicleId}")
    public List<Map<String, Object>> findMonthlyAveragesForCurrentYear(@PathVariable Long vehicleId) {
        //LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        return fuelRecordService.findMonthlyAveragesForCurrentYear(vehicleId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuelRecordModel> findById(@PathVariable @NotNull Long id) {
        try {
            FuelRecordModel gasRecord = fuelRecordService.findById(id);
            return new ResponseEntity<>(gasRecord, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<FuelRecordModel> findAll(){
        return fuelRecordService.findAll();
    }

    @GetMapping("/page")
    public Page<FuelRecordModel> findAll(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "8") int size){
        Pageable pageable = PageRequest.of(page, size);
        return fuelRecordService.findAll(pageable);
    }

    @GetMapping("/findByVehicleId/{vehicleId}")
    public ResponseEntity<List<FuelRecordModel>> findByVehicleId(@PathVariable Long vehicleId){
        try {
            List<FuelRecordModel> data = fuelRecordService.findByVehicleId(vehicleId);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping ("/findByVehicleId-page/{vehicleId}")
    public ResponseEntity<Page<FuelRecordModel>> findByVehicleIdPage(@PathVariable Long vehicleId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "8") int size){
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<FuelRecordModel> data = fuelRecordService.findByVehicleId(vehicleId, pageable);
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody FuelRecordModel fuelRecordModel){
        try {
            FuelRecordModel data = fuelRecordService.save(fuelRecordModel);
            return new ResponseEntity<>(data, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id){
        try {
            fuelRecordService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
