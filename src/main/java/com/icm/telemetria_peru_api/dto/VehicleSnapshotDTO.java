package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSnapshotDTO {
    private Long id;
    private Long vehicleId;
    private Long companyId;
    private String licensePlate;
    private String imei;
    private String timestamp;
    private Double fuelInfo;
    private Boolean alarmInfo;
    private Boolean ignitionInfo;
    private String coordinates;
    private Double gasInfo;
    private Integer snapshotSpeed;
    private String snapshotLatitude;
    private String snapshotLongitude;
}
