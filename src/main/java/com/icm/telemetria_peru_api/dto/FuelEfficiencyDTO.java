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
    private Double fuelConsumptionPerHour;
    private FuelType fuelType;
    private Double formattedInitialFuel;
    private Double formattedFinalFuel;

    // Constructor
    public FuelEfficiencyDTO(FuelEfficiencyModel model) {
        this.id = model.getId();
        this.licensePlate = model.getVehicleModel().getLicensePlate();
        this.fuelEfficiencyStatus = model.getFuelEfficiencyStatus().name();
        this.initialFuel = model.getInitialFuel();
        this.finalFuel = model.getFinalFuel();
        this.fuelConsumptionPerHour = model.getFuelConsumptionPerHour();
        this.fuelType = model.getVehicleModel().getFuelType();

        if ("DIESEL".equals(this.fuelType)) {
            this.formattedInitialFuel = this.initialFuel != null ? this.initialFuel * 0.264172 : null;
            this.formattedFinalFuel = this.finalFuel != null ? this.finalFuel * 0.264172 : null;
        } else {
            this.formattedInitialFuel = this.initialFuel;
            this.formattedFinalFuel = this.finalFuel;
        }
    }
}
