package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "fuel_theft_alerts",
        indexes = {
                @Index(name = "idx_fuel_theft_vehicle_detected", columnList = "vehicle_id, detected_at"),
                @Index(name = "idx_fuel_theft_status", columnList = "status")
        }
)
public class FuelTheftAlertModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vehículo
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    // Cuándo lo detectamos
    @Column(name = "detected_at", nullable = false)
    private ZonedDateTime detectedAt;

    // Métricas
    @Column(name = "baseline_value", nullable = false)
    private Double baselineValue;

    @Column(name = "current_value", nullable = false)
    private Double currentValue;

    @Column(name = "drop_value", nullable = false)
    private Double dropValue;

    // Estado de la alerta (OPEN / DISMISSED / CONFIRMED)
    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN";

    // Opcional: texto/JSON con evidencia breve
    @Column(name = "evidence", columnDefinition = "TEXT")
    private String evidence;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
