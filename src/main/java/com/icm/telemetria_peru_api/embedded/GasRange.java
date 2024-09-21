package com.icm.telemetria_peru_api.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class GasRange {
    @Column(nullable = false)
    private int optimalGasRangeStart;

    @Column(nullable = false)
    private int regularGasRangeStart;

    @Column(nullable = false)
    private int lowGasRangeStart;

    @Column(nullable = false)
    private int veryLowGasRangeStart;

    @Column(nullable = false)
    private int maxGasPressure;
}
