package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "fuel_theft_alerts",
        indexes = {
                @Index(name = "idx_fuel_theft_vehicle_detected", columnList = "vehicle_id, detected_at")
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

    // Mensaje (ej: "Posible robo..." / "Caída brusca...")
    @Column(name = "message", nullable = false, length = 120)
    private String message;
}
