package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
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
@Table(name = "gas_records")
public class GasRecordModel {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HOra de inicio de conteo
    @Column(nullable = false)
    private Long  startTime;

    // Hora de fin de conteo
    private Long  endTime;

    // Tiempo acumulado
    private Long accumulatedTime = 0L;

    // Ultima presion detectada
    @Column(nullable = false)
    private Double lastPressureDetected;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
