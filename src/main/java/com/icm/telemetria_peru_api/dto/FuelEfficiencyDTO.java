package com.icm.telemetria_peru_api.dto;

import com.icm.telemetria_peru_api.enums.FuelType;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import lombok.Data;

@Data
public class FuelEfficiencyDTO {
    private Long id;
    private String licensePlate;
    private String fuelEfficiencyStatus;
    private Double initialFuel;
    private Double finalFuel;
    private FuelType fuelType;
    private Double fuelEfficiency;
    private Double fuelConsumptionPerHour;

    // Constructor
    public FuelEfficiencyDTO(FuelEfficiencyModel model) {
        this.id = model.getId();
        this.licensePlate = model.getVehicleModel().getLicensePlate();
        this.fuelEfficiencyStatus = model.getFuelEfficiencyStatus().name();
        this.initialFuel = model.getInitialFuel();
        this.finalFuel = model.getFinalFuel();

        this.fuelType = model.getVehicleModel().getFuelType();
        this.fuelEfficiency = model.getFuelEfficiency();
        this.fuelConsumptionPerHour = model.getFuelConsumptionPerHour();

        if (this.fuelType.equals(FuelType.DIESEL)) {
            this.initialFuel = this.initialFuel != null ? this.initialFuel * 0.264172 : null;
            this.finalFuel = this.finalFuel != null ? this.finalFuel * 0.264172 : null;
            this.fuelEfficiency = this.fuelEfficiency != null ? this.fuelEfficiency * 0.264172 : null;
            this.fuelConsumptionPerHour = this.fuelConsumptionPerHour != null ? this.fuelConsumptionPerHour * 0.264172 : null;
        } else {
            this.initialFuel = this.initialFuel;
            this.finalFuel = this.finalFuel;
        }
    }
}
