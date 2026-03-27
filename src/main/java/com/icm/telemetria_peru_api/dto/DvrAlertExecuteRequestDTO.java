package com.icm.telemetria_peru_api.dto;

import lombok.Data;

@Data
public class DvrAlertExecuteRequestDTO {
    private String alertCode;
    private String subalertCode;
    private Integer channel;
    private Integer durationSeconds;
}
