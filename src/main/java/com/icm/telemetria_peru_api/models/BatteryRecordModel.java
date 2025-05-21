package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

/**
 * BatteryRecordModel represents a log entry for battery records.
 * It contains information about the voltage, current, the associated battery,
 * and timestamps for creation and update.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "battery_records")
public class BatteryRecordModel {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double voltage;

    private Double current;

    @ManyToOne
    @JoinColumn(name = "battery_id", referencedColumnName = "id", nullable = false)
    private BatteryModel batteryModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}