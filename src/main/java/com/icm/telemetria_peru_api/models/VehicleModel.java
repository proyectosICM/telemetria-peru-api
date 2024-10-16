package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@Table(name = "vehicle")
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @NotEmpty(message = "License plate is required")
    @Size(max = 20, message = "License plate must be less than 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_]{1,20}$")
    @Column(name = "licensePlate", nullable = false, length = 20)
    private String licensePlate;

    private Boolean alarmStatus;

    private Boolean engineStatus;

    private Boolean lockStatus;

    private Long timeOn;

    private Integer maxSpeed;

    @ManyToOne
    @JoinColumn(name = "driver", referencedColumnName ="id", nullable = true)
    private DriverModel driverModel;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "vehicle_type_id", referencedColumnName = "id", nullable = false)
    private VehicleTypeModel vehicletypeModel;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private CompanyModel companyModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    @Column(name = "max_fuel_volume", nullable = true)
    private Double maxFuelVolume;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

}
