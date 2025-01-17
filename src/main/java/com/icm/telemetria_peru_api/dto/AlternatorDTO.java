package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlternatorDTO {
    private Long id;
    private Double voltage;
    private Long vehicleId;
    private String licensePlate;
    private ZonedDateTime createdAt;
}
