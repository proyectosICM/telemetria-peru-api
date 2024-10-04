package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryDTO {
    private Long id;
    private String name;
    private Long vehicleId;
    private String licensePlate;
    private Long companyId;
    private String companyName;
}
