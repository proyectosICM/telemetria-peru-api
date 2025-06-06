package com.icm.telemetria_peru_api.models;

import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FuelEfficiencyStatus fuelEfficiencyStatus;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @Column(nullable = false)
    @CreationTimestamp
    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private Double accumulatedHours;

    @Column(nullable = false)
    private Double initialFuel;

    private Double finalFuel;

    private Double distance;

    private String coordinates;

    private Boolean isVisible = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "speeds", joinColumns = @JoinColumn(name = "fuel_efficiency_id"))
    @Column(name = "speed")
    private List<Double> speeds;

    private Double fuelEfficiency;

    private Double fuelConsumptionPerHour;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
