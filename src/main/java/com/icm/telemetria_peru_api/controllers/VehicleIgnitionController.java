package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.IgnitionCountByDate;
import com.icm.telemetria_peru_api.dto.IgnitionDuration;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import com.icm.telemetria_peru_api.services.VehicleIgnitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/vehicle-ignition")
@RequiredArgsConstructor
public class VehicleIgnitionController {
    private final VehicleIgnitionService vehicleIgnitionService;

    @GetMapping
    public List<VehicleIgnitionModel> findAll(){
        return vehicleIgnitionService.findAll();
    }

    @GetMapping("/paged")
    public Page<VehicleIgnitionModel> findAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleIgnitionService.findAll(pageable);
    }

    @GetMapping("/findByVehicle/{vehicleId}")
    public List<VehicleIgnitionModel> findByVehicleModelId(@PathVariable Long vehicleId){
        return vehicleIgnitionService.findByVehicleModelId(vehicleId);
    }

    @GetMapping("/findByVehicle-paged/{vehicleId}")
    public Page<VehicleIgnitionModel> findByVehicleModelId(@PathVariable Long vehicleId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "8") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return vehicleIgnitionService.findByVehicleModelId(vehicleId, pageable);
    }

    @GetMapping("/active-durations/{vehicleId}")
    public List<IgnitionDuration> getActiveDurations(@PathVariable Long vehicleId) {
        return vehicleIgnitionService.calculateActiveDurations(vehicleId);
    }

    @GetMapping("/count/{vehicleId}")
    public ResponseEntity<Map<String, Object>> getConsolidatedIgnitionData(@PathVariable Long vehicleId) {
        try {
            // Llama al servicio para obtener los datos consolidados
            Map<String, Object> data = vehicleIgnitionService.getConsolidatedIgnitionData(vehicleId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // Manejo de errores
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al obtener los datos consolidados de igniciones");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count-weekly/{vehicleId}")
    public List<IgnitionCountByDate> countWeeklyIgnitions(@PathVariable Long vehicleId) {
        return vehicleIgnitionService.countIgnitionsByWeek(vehicleId);
    }

    @PostMapping
    public VehicleIgnitionModel save(@RequestBody VehicleIgnitionModel vehicleIgnitionModel){
        return vehicleIgnitionService.save(vehicleIgnitionModel);
    }
}
