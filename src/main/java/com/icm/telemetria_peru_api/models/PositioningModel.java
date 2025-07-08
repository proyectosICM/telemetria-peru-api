package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

/** PositioningModel represents the positioning of a vehicle's axle.
 * It contains information about the location code, description, side of the vehicle,
 * axle type, position on the axle, and the associated vehicle type.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "positioning")
public class PositioningModel {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String locationCode;

    private String description;

    private String sideOfVehicle;

    private String axle;

    private String positionOnAxle;

    @ManyToOne
    @JoinColumn(name = "type", nullable = false)
    private VehicleTypeModel vehicleTypeModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
