package com.icm.telemetria_peru_api.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class GasolineRange {
    @Column(nullable = false)
    private int optimalGasolineRangeStart;

    @Column(nullable = false)
    private int regularGasolineRangeStart;

    @Column(nullable = false)
    private int lowGasolineRangeStart;

    @Column(nullable = false)
    private int veryLowGasolineRangeStart;

    @Column(nullable = false)
    private int maxGasolinePressure;
}
