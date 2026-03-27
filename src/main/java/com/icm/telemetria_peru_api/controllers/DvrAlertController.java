package com.icm.telemetria_peru_api.controllers;

import com.icm.telemetria_peru_api.dto.DvrAlertExecuteRequestDTO;
import com.icm.telemetria_peru_api.dto.DvrAlertExecutionDTO;
import com.icm.telemetria_peru_api.dto.VehicleDvrAlertsDTO;
import com.icm.telemetria_peru_api.services.DvrAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicles/{vehicleId}/dvr-alerts")
@RequiredArgsConstructor
public class DvrAlertController {
    private final DvrAlertService dvrAlertService;

    @GetMapping
    public ResponseEntity<VehicleDvrAlertsDTO> getAlerts(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(dvrAlertService.getVehicleAlerts(vehicleId));
    }

    @PostMapping("/execute")
    public ResponseEntity<DvrAlertExecutionDTO> execute(@PathVariable Long vehicleId,
                                                        @RequestBody DvrAlertExecuteRequestDTO request,
                                                        Authentication authentication) {
        String requestedBy = authentication != null ? authentication.getName() : "system";
        DvrAlertExecutionDTO response = dvrAlertService.executeAlert(vehicleId, request, requestedBy);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/executions")
    public ResponseEntity<List<DvrAlertExecutionDTO>> getExecutions(@PathVariable Long vehicleId,
                                                                    @RequestParam(defaultValue = "20") Integer limit) {
        return ResponseEntity.ok(dvrAlertService.getExecutions(vehicleId, limit));
    }
}
