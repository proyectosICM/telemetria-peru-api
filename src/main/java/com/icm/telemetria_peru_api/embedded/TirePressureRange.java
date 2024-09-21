package com.icm.telemetria_peru_api.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class TirePressureRange {
    @Column(nullable = false)
    private int optimalTirePressureRangeStart;
    @Column(nullable = false)
    private int regularTirePressureRangeStart;
    @Column(nullable = false)
    private int lowTirePressureRangeStart;
    @Column(nullable = false)
    private int veryLowTirePressureRangeStart;
    @Column(nullable = false)
    private int maxTirePressure;
}
