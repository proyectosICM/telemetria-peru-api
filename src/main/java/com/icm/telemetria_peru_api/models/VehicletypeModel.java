package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicleType")
public class VehicletypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotEmpty(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "createdAt", nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("America/Lima"));

    @Column(name = "updatedAt")
    @UpdateTimestamp
    private ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.of("America/Lima"));
}
