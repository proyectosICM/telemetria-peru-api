package com.icm.telemetria_peru_api.dto;

import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
public class FuelEfficiencySummaryDTO {
    private FuelEfficiencyStatus status;
    private Double totalHours;
    private Double totalFuelConsumed;
    private Double avgFuelEfficiency;

    public FuelEfficiencySummaryDTO(FuelEfficiencyStatus status, Double totalHours, Double totalFuelConsumed, Double avgFuelEfficiency) {
        this.status = status;
        this.totalHours = totalHours;
        this.totalFuelConsumed = totalFuelConsumed;
        this.avgFuelEfficiency = avgFuelEfficiency;
    }
}
