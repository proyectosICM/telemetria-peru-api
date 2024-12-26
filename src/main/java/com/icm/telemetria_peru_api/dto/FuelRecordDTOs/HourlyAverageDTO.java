package com.icm.telemetria_peru_api.dto.FuelRecordDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlyAverageDTO {
    private String hour;
    private Double averageValue;
}
