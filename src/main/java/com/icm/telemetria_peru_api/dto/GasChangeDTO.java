package com.icm.telemetria_peru_api.dto;


import com.icm.telemetria_peru_api.models.GasChangeModel;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class GasChangeDTO {
    private LocalDate day;
    private LocalTime time;
    private String country;
    private Long vehicleModelId;

    // Método para convertir DTO a GasChangeModel
    public GasChangeModel toGasChangeModel() {
        // Convert LocalDate and LocalTime to ZonedDateTime
        ZoneId zoneId = ZoneId.of(this.country); // "America/Lima" or other timezone
        ZonedDateTime changeDateTime = ZonedDateTime.of(this.day.atTime(this.time), zoneId);

        // Create GasChangeModel
        GasChangeModel gasChangeModel = new GasChangeModel();
        //gasChangeModel.setChangeDateTime(changeDateTime);

        // Set VehicleModel (you might need to fetch it from the database)
    /*
        VehicleModel vehicleModel = new VehicleModel();
        vehicleModel.setId(this.vehicleModelId);
        gasChangeModel.setVehicleModel(vehicleModel);
*/
        return gasChangeModel;
    }
}