package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IgnitionDuration {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private long durationInHours;
    private long durationInMinutes;

}
