package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DvrGpsSnapshotDTO {
    private String dvrPhone;
    private String latitude;
    private String longitude;
    private Integer speed;
    private Boolean ignitionStatus;
    private Boolean alarmStatus;
    private String timestamp;
}
