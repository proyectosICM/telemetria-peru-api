package com.icm.telemetria_peru_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class IgnitionCountByDate {
    private String date;
    private Long count;

    public IgnitionCountByDate(String date, Long count) {
        this.date = date;
        this.count = count;
    }
}
