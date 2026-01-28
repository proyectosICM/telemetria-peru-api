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
    private Double speed; // GPS speed
    private String timestamp;
    private Double fuelInfo;
    private Integer alarmInfo;
    private Boolean ignitionInfo; // 239
    private String coordinates;
    private Integer movement;         // 240 (0/1)
    private Integer instantMovement;  // 303 (0/1)
    private Integer vehicleSpeedIo;   // 37
    private Integer externalVoltage;  // 66 (mV normalmente)
    private Double gasInfo;
}
