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
@Table(name = "checklist_records")
public class ChecklistRecordModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String name;

    private String fileName;

    private int timer;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private DriverModel driverModel;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false)
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "checklist_type", referencedColumnName = "id", nullable = false)
    private ChecklistTypeModel checklistTypeModel;

    @ManyToOne
    @JoinColumn(name = "company", referencedColumnName = "id", nullable = false)
    private CompanyModel companyModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
