package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.DvrAlertExecuteRequestDTO;
import com.icm.telemetria_peru_api.dto.DvrAlertExecutionDTO;
import com.icm.telemetria_peru_api.dto.VehicleDvrAlertsDTO;

import java.util.List;

public interface DvrAlertService {
    VehicleDvrAlertsDTO getVehicleAlerts(Long vehicleId);
    DvrAlertExecutionDTO executeAlert(Long vehicleId, DvrAlertExecuteRequestDTO request, String requestedBy);
    List<DvrAlertExecutionDTO> getExecutions(Long vehicleId, Integer limit);
}
