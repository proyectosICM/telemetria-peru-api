package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

/**
 * ChecklistTypeModel represents the type or category of a checklist.
 * It contains the name of the checklist type (e.g., daily inspection, maintenance),
 * and timestamps for creation and last update.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "checklist_types")
public class ChecklistTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String name;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
