package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.repositories.AlarmRecordRepository;
import com.icm.telemetria_peru_api.services.AlarmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<AlarmRecordModel> findByVehicleModelId(@PathVariable Long vehicleId){
        return alarmRecordService.findByVehicleModelId(vehicleId);
    }

    @GetMapping("/paged")
    public Page<AlarmRecordModel> findByVehicleModelId(@PathVariable Long vehicleId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return alarmRecordService.findByVehicleModelId(vehicleId, pageable);
    }
}
