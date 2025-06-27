package com.icm.telemetria_peru_api.dto;

import lombok.Data;

@Data
public class GasQueryParams {
    private Long vehicleId;
    private String viewType; // day, month, year
    private Integer year;
    private Integer month; // opcional
    private Integer day;   // opcional
}
