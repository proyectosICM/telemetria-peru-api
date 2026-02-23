package com.icm.telemetria_peru_api.jobs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jobs.fuel-theft")
public record FuelTheftJobConfig(
        boolean enabled,
        int activeWindowMinutes,
        int batchSize,
        String zone
) {
}