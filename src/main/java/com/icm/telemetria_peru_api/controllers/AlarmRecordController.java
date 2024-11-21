package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.repositories.AlarmRecordRepository;
import com.icm.telemetria_peru_api.services.AlarmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("api/alarm-record")
public class AlarmRecordController {
    private final AlarmRecordService alarmRecordService;

    @Autowired
    public AlarmRecordController(AlarmRecordService alarmRecordService) {
        this.alarmRecordService = alarmRecordService;
    }

    @GetMapping
    public List<AlarmRecordModel> findAll(){
        return alarmRecordService.findAll();
    }

    @GetMapping("/paged")
    public Page<AlarmRecordModel> findAll(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return alarmRecordService.findAll(pageable);
    }

    @GetMapping("/findByVehicle/{vehicleId}")
    public List<AlarmRecordModel> findByVehicleModelId(@PathVariable Long vehicleId){
        return alarmRecordService.findByVehicleModelId(vehicleId);
    }

    @GetMapping("/findByVehicle-paged/{vehicleId}")
    public Page<AlarmRecordModel> findByVehicleModelId(@PathVariable Long vehicleId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return alarmRecordService.findByVehicleModelId(vehicleId, pageable);
    }

    @GetMapping("/hourly-averages")
    public List<Map<String, Object>> getHourlyAverages(@RequestParam(required = false) String date) {
        LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        return alarmRecordService.getHourlyAveragesByDate(localDate);
    }

    @PostMapping
    public AlarmRecordModel save(@RequestBody AlarmRecordModel alarmRecordModel){
        return alarmRecordService.save(alarmRecordModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlarmRecordModel> delete(@PathVariable Long id){
        alarmRecordService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
