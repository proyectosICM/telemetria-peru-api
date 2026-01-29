package com.icm.telemetria_peru_api.dto;

import java.time.ZonedDateTime;

public record FuelTheftAlertDTO(
        Long id,
        Long vehicleId,
        String licensePlate,
        ZonedDateTime detectedAt,
        String message
) {}
