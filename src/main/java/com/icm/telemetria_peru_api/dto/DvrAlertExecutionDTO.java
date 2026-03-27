package com.icm.telemetria_peru_api.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class DvrAlertExecutionDTO {
    private Long id;
    private Long vehicleId;
    private String licensePlate;
    private String dvrPhone;
    private String alertCode;
    private String subalertCode;
    private Integer channel;
    private Integer durationSeconds;
    private String status;
    private String responsePayload;
    private String errorMessage;
    private String requestedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
