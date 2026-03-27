package com.icm.telemetria_peru_api.dto;

import lombok.Data;

@Data
public class DvrAlertSubalertDTO {
    private String code;
    private String name;
    private String description;
    private Boolean available;
}
