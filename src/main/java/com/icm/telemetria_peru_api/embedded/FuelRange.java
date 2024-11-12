package com.icm.telemetria_peru_api.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class FuelRange {
    @Column(nullable = false)
    private int optimalFuelRangeStart;

    @Column(nullable = false)
    private int regularFuelRangeStart;

    @Column(nullable = false)
    private int lowFuelRangeStart;

    @Column(nullable = false)
    private int veryLowFuelRangeStart;

    @Column(nullable = false)
    private int maxFuelPressure;
}
