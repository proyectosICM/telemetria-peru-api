package com.icm.telemetria_peru_api.repositories.projections;

public interface FuelEfficiencySumView {
    Long getParkedSeconds();
    Long getIdleSeconds();
    Long getOperationSeconds();
}
