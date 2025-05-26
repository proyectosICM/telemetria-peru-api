package com.icm.telemetria_peru_api.dto;

import java.time.Duration;

public interface FuelReportSummaryDTO {
    Double getAverageFuelConsumption();
    Duration getTotalIdleTime();
    Duration getTotalParkedTime();
    Duration getTotalOperatingTime();
}
