package com.icm.telemetria_peru_api.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class VehicleVideoDTO {
    private Long vehicleId;
    private String licensePlate;
    private String dvrPhone;

    // canales configurados en BD
    private Set<Integer> videoChannels;

    // URLs completas HLS listas para usar en el front
    private List<String> hlsUrls;
}
