package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dvr_alert_executions")
public class DvrAlertExecutionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @Column(nullable = false, length = 12)
    private String dvrPhone;

    @Column(nullable = false, length = 100)
    private String alertCode;

    @Column(length = 100)
    private String subalertCode;

    private Integer channel;

    private Integer durationSeconds;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 255)
    private String requestedBy;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String responsePayload;

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
