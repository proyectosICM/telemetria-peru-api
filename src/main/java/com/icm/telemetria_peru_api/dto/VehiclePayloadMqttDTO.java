package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehiclePayloadMqttDTO {
    private Long vehicleId;
    private Long companyId;
    private String licensePlate;
    private String imei;
    private Double speed;
    private String timestamp;
    private Double fuelInfo;
    private Integer alarmInfo;
    private Boolean ignitionInfo;
    private String coordinates;
    private Double gasInfo;
}
