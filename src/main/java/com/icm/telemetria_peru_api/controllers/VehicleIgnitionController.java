package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.IgnitionDuration;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.services.VehicleIgnitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicle-ignition")
public class VehicleIgnitionController {
    private final VehicleIgnitionService vehicleIgnitionService;
    @Autowired
    public VehicleIgnitionController(VehicleIgnitionService vehicleIgnitionService) {
        this.vehicleIgnitionService = vehicleIgnitionService;
    }

    @GetMapping("/active-durations/{vehicleId}")
    public List<IgnitionDuration> getActiveDurations(@PathVariable Long vehicleId) {
        return vehicleIgnitionService.calculateActiveDurations(vehicleId);
    }

    @GetMapping
    public List<VehicleIgnitionModel> findAll(){
        return vehicleIgnitionService.findAll();
    }

    @GetMapping("/paged")
    public Page<VehicleIgnitionModel> findAll(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return vehicleIgnitionService.findAll(pageable);
    }

    @GetMapping("/findByVehicle/{vehicleId}")
    public List<VehicleIgnitionModel> findByVehicleModelId(@PathVariable Long vehicleId){
        return vehicleIgnitionService.findByVehicleModelId(vehicleId);
    }

    @GetMapping("/findByVehicle-paged/{vehicleId}")
    public Page<VehicleIgnitionModel> findByVehicleModelId(@PathVariable Long vehicleId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleIgnitionService.findByVehicleModelId(vehicleId, pageable);
    }

    @PostMapping
    public VehicleIgnitionModel save(@RequestBody VehicleIgnitionModel vehicleIgnitionModel){
        return vehicleIgnitionService.save(vehicleIgnitionModel);
    }
}
