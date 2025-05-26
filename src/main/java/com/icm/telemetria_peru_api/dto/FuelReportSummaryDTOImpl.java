package com.icm.telemetria_peru_api.dto;

import java.time.Duration;

public class FuelReportSummaryDTOImpl implements FuelReportSummaryDTO {
    private final Double averageFuelConsumption;
    private final Duration totalIdleTime;
    private final Duration totalParkedTime;
    private final Duration totalOperatingTime;

    public FuelReportSummaryDTOImpl(Double avg, Duration idle, Duration parked, Duration op) {
        this.averageFuelConsumption = avg;
        this.totalIdleTime = idle;
        this.totalParkedTime = parked;
        this.totalOperatingTime = op;
    }

    @Override public Double getAverageFuelConsumption() { return averageFuelConsumption; }
    @Override public Duration getTotalIdleTime() { return totalIdleTime; }
    @Override public Duration getTotalParkedTime() { return totalParkedTime; }
    @Override public Duration getTotalOperatingTime() { return totalOperatingTime; }
}
