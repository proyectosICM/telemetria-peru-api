package com.icm.telemetria_peru_api.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VehicleDvrAlertsDTO {
    private Long vehicleId;
    private String licensePlate;
    private String dvrPhone;
    private Boolean online;
    private String connectedAt;
    private String lastSeenAt;
    private List<DvrAlertDTO> alerts = new ArrayList<>();
}
