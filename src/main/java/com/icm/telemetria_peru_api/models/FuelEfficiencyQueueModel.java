package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

/** FuelEfficiencyQueueModel represents a persistent queue record for fuel efficiency processing.
 * It stores the minimum telemetry data required to calculate daily parked, idle,
 * and operation seconds without blocking the main MQTT ingestion flow.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "fuel_efficiency_queue",
        indexes = {
                @Index(name = "idx_feq_processed_processing_created", columnList = "processed, processing, created_at"),
                @Index(name = "idx_feq_vehicle_event_time", columnList = "vehicle_id, event_time"),
                @Index(name = "idx_feq_attempt_count", columnList = "attempt_count")
        }
)
public class FuelEfficiencyQueueModel {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @Column(name = "event_time", nullable = false)
    private ZonedDateTime eventTime;

    @Column(name = "ignition_info")
    private Boolean ignitionInfo;

    @Column(name = "movement")
    private Integer movement;

    @Column(name = "instant_movement")
    private Integer instantMovement;

    @Column(name = "vehicle_speed_io")
    private Integer vehicleSpeedIo;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "external_voltage")
    private Integer externalVoltage;

    @Column(name = "processing", nullable = false)
    private Boolean processing = false;

    @Column(name = "processed", nullable = false)
    private Boolean processed = false;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "processed_at")
    private ZonedDateTime processedAt;

    @Column(name = "last_error", length = 500)
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
