package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.ZonedDateTime;

/** TireSensorModel represents a tire sensor in the system.
 * It contains information about the sensor's identification code,
 * temperature, pressure, battery level, status, associated vehicle,
 * company, and positioning data, along with timestamps for creation and update.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tires_sensor")
    public class TireSensorModel {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Identification code cannot be blank")
    @Size(max = 10, message = "Identification code cannot exceed 10 characters")
    private String identificationCode;

    @DecimalMin(value = "-60.0", inclusive = true, message = "Temperature must be greater than or equal to -60°C")
    @DecimalMax(value = "120.0", inclusive = true, message = "Temperature must be less than or equal to 120°C")
    private Double temperature;

    @DecimalMin(value = "-20.0", inclusive = true, message = "Pressure must be greater than or equal to 0 psi")
    @DecimalMax(value = "180.0", inclusive = true, message = "Pressure must be less than or equal to 100 psi")
    private Double pressure;

    @DecimalMin(value = "0.0", inclusive = true, message = "Battery level must be greater than or equal to 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Battery level must be less than or equal to 100")
    private Double batteryLevel;

    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "vehicle", nullable = true)
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "company", nullable = false)
    private CompanyModel companyModel;

    @ManyToOne
    @JoinColumn(name = "positioning", nullable = true)
    private TirePositioningModel tirePositioningModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
