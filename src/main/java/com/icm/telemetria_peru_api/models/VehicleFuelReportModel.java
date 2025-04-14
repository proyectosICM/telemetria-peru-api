package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicle_fuel_report")
public class VehicleFuelReportModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private LocalDate date;
    private LocalDateTime openingTime;

    private LocalDateTime closingTime;

    private Duration idleTime;       // Tiempo en ralentí
    private Duration parkedTime;     // Tiempo estacionado
    private Duration operatingTime;  // Tiempo en operación

    private Double initialFuel;      // Combustible inicial (litros)
    private Double finalFuel;        // Combustible final (litros)
    private Double fuelConsumed;     // Combustible consumido

    private Double currentFuelDetected;
    @ManyToOne
    private VehicleModel vehicleModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
