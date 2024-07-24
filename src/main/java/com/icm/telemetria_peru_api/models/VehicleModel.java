package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZoneId;
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
    @Column(name = "licensePlate", nullable = false, length = 20)
    private String licensePlate;

    private Integer speed;

    @Column(name = "alarm_status")
    private Boolean alarmStatus;

    //@Column(name = "time_on")
    private Long timeOn;

    @Column(precision = 20, scale = 15)
    private BigDecimal longitud;

    @Column(precision = 20, scale = 15)
    private BigDecimal  latitud;

    @ManyToOne
    @JoinColumn(name = "driver", referencedColumnName ="id", nullable = true)
    private DriverModel driverModel;

    @ManyToOne
    @JoinColumn(name = "vehicleType", referencedColumnName = "id", nullable = false)
    private VehicletypeModel vehicletypeModel;

    @ManyToOne
    @JoinColumn(name = "comapanyId", referencedColumnName = "id", nullable = false)
    private CompanyModel companyModel;

    @Column(name = "createdAt", nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("America/Lima"));

    @Column(name = "updatedAt")
    @UpdateTimestamp
    private ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.of("America/Lima"));

}
