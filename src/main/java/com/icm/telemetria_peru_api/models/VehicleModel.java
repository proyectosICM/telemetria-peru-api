package com.icm.telemetria_peru_api.models;

import com.icm.telemetria_peru_api.enums.FuelType;
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

/**
 * VehicleModel represents a vehicle in the system.
 * It contains information about the vehicle's license plate,
 * status, alarm status, engine status, lock status, time on,
 * maximum speed, associated driver, vehicle type, company,
 * fuel type, and timestamps for creation and update.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicles")
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotEmpty(message = "License plate is required")
    @Size(max = 20, message = "License plate must be less than 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_]{1,20}$")
    @Column(name = "licensePlate", nullable = false, length = 20)
    private String licensePlate;

    private String imei;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    private Boolean alarmStatus = false;

    private Boolean engineStatus = false;

    private Boolean lockStatus = false;

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

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

}
