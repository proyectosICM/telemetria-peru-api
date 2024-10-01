package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Images-cl")
public class ImagesCLModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "checklist_record", referencedColumnName = "id", nullable = false)
    private ChecklistRecordModel checklistRecordModel;
}
