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
@Table(name = "gas_changes")
public class GasChangeModel {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Time when low pressure was detected
    @Column(nullable = false)
    private String lowPressureDetectedAt;

    @Column(nullable = false)
    private Double pressureBeforeChange;

    // Time when the gas change was performed
    @Column(nullable = false)
    private String changePerformedAt;

    // Pressure after the change (in psi)
    @Column(nullable = false)
    private Double pressureAfterChange;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}

