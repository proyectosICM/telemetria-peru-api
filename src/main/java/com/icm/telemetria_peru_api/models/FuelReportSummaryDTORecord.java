package com.icm.telemetria_peru_api.models;

import java.time.Duration;

public record FuelReportSummaryDTORecord(
        Double averageFuelConsumption,
        Duration totalIdleTime,
        Duration totalParkedTime,
        Duration totalOperatingTime
) {}