package com.icm.telemetria_peru_api.models;

import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/** FuelEfficiencyModel represents the fuel efficiency data for a vehicle.
 * It contains information about the fuel efficiency status, associated vehicle,
 * timestamps for start and end times, accumulated hours, initial and final fuel levels,
 * distance traveled, coordinates, speeds recorded, fuel efficiency calculations,
 * and visibility status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fuel_efficiency")
public class FuelEfficiencyModel {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @Column(name = "day", nullable = false)
    private LocalDate day;

    @Column(name = "parked_seconds", nullable = false)
    private Long parkedSeconds = 0L;

    @Column(name = "idle_seconds", nullable = false)
    private Long idleSeconds = 0L;

    @Column(name = "operation_seconds", nullable = false)
    private Long operationSeconds = 0L;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
