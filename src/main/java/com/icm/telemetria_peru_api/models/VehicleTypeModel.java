package com.icm.telemetria_peru_api.models;

import com.icm.telemetria_peru_api.embedded.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "vehicle_types")
public class VehicleTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotEmpty(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z\\\\s\\\\u00E1\\\\u00E9\\\\u00ED\\\\u00F3\\\\u00FA\\\\u00F1\\\\u00D1]{1,100}$", message = "Name must be less than 100 characters and contain only letters, spaces, or dashes")
    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    private FuelRange fuelRange;

    @Embedded
    private BatteryRange batteryRange;

    @Embedded
    private TirePressureRange tirePressureRange;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
