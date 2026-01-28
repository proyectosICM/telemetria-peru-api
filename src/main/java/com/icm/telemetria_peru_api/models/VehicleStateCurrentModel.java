package com.icm.telemetria_peru_api.models;

import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
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
        name = "vehicle_state_current",
        uniqueConstraints = @UniqueConstraint(columnNames = {"vehicle_id"})
)
public class VehicleStateCurrentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FuelEfficiencyStatus status;

    @Column(name = "last_event_time", nullable = false)
    private ZonedDateTime lastEventTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
