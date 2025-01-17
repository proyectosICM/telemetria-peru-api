package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRecordDTO {
    private Long id;
    private String name;
    private String fileName;
    private int timer;
    private Long driverId;
    private String driverName;
    private Long vehicleId;
    private String licensePlate;
    private Long type;
    private Long companyId;
    private ZonedDateTime createdAt;
}
