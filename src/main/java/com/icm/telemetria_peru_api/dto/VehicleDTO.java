package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String licensePlate;
    private Boolean status;

    private Long vehicleTypeId;
    private String vehicleTypeName;

    private Long companyId;
    private String companyName;

    private Integer maxSpeed;
}
