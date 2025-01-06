package com.icm.telemetria_peru_api.controllers;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/today/{vehicleId}")
    public ResponseEntity<List<VehicleIgnitionModel>> getTodayIgnitionRecords(@PathVariable Long vehicleId) {
        // Llamar al servicio para obtener los registros de ignición del día actual
        List<VehicleIgnitionModel> todayRecords = vehicleIgnitionService.findTodayIgnitionRecords(vehicleId);

        // Verificar si se encontraron registros
        if (todayRecords.isEmpty()) {
            return ResponseEntity.noContent().build();  // No hay registros para hoy
        }

        return ResponseEntity.ok(todayRecords);  // Devolver los registros encontrados
    }

    @GetMapping("/last-7-days/{vehicleId}")
    public ResponseEntity<List<VehicleIgnitionModel>> getLast7DaysIgnitionRecords(@PathVariable Long vehicleId) {
        // Llamar al servicio para obtener los registros de ignición de los últimos 7 días
        List<VehicleIgnitionModel> last7DaysRecords = vehicleIgnitionService.findLast7DaysIgnitionRecords(vehicleId);

        // Verificar si se encontraron registros
        if (last7DaysRecords.isEmpty()) {
            return ResponseEntity.noContent().build();  // No hay registros en los últimos 7 días
        }

        return ResponseEntity.ok(last7DaysRecords);  // Devolver los registros encontrados
    }

    @PostMapping
    public VehicleIgnitionModel save(@RequestBody VehicleIgnitionModel vehicleIgnitionModel){
        return vehicleIgnitionService.save(vehicleIgnitionModel);
    }
}
