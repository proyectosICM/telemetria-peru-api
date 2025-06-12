package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

/**
 * VehicleSnapshot represents a snapshot of a vehicle's state at a specific moment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicle_snapshots")
public class VehicleSnapshotModel {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String snapshotLatitude;

    private String snapshotLongitude;

    private Integer snapshotSpeed;

    private Boolean snapshotAlarmStatus;

    private Boolean snapshotIgnitionStatus;

    private Double snapshotFuelLevel;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private CompanyModel companyModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
