package com.icm.telemetria_peru_api.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class BatteryRange {
    @Column(nullable = false)
    private int optimalBatteryRangeStart;
    @Column(nullable = false)
    private int regularBatteryRangeStart;
    @Column(nullable = false)
    private int lowBatteryRangeStart;
    @Column(nullable = false)
    private int veryLowBatteryRangeStart;
    @Column(nullable = false)
    private int maxBatteryVoltage;
}
