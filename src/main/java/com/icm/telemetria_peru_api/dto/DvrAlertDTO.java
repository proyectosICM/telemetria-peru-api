package com.icm.telemetria_peru_api.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DvrAlertDTO {
    private String code;
    private String name;
    private String description;
    private Integer durationSecondsDefault;
    private Boolean requiresChannel;
    private Boolean available;
    private List<DvrAlertSubalertDTO> subalerts = new ArrayList<>();
}
