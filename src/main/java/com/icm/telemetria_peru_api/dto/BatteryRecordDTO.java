package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryRecordDTO {
    private Long id;
    private Double voltage;
    private Double current;
    private Long batteryId;
    private String nameBattery;
    private ZonedDateTime createdAt;
}