package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleOptionsDTO {
    private Long id;
    private String licensePlate;
    private Boolean engineStatus;
    private Boolean alarmStatus;
    private Boolean lockStatus;
}