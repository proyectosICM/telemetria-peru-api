package com.icm.telemetria_peru_api.dto;

import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FuelEfficiencySummary {
    private FuelEfficiencyStatus status;
    private Double totalHours;
    private Double totalFuelConsumed;
    private Double averageEfficiency;
}
