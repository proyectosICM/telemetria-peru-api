    package com.icm.telemetria_peru_api.models;
    
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;
    
    import java.time.ZonedDateTime;
    
    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(name = "tires_positioning")
    public class TirePositioningModel {
        @Id
        @Column(unique = true, nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        /**
         * Code representing a specific location or region on the vehicle
         * where the tire is mounted. Useful for internal tracking or grouping.
         */
        private String locationCode;
    
        /**
         * Indicates the side of the vehicle where the tire is located,
         * such as 'Left' or 'Right'.
         */
        private String sideOfVehicle;
    
        /**
         * Specifies the axle of the vehicle on which the tire is located, such as 'Front', 'Middle', 'Rear'.
         */
        private String axle;
    
        /**
         * Describes the position of the tire on the specified axle.
         * For vehicles with dual tires, values may include 'Inner' or 'Outer'.
         */
        private String positionOnAxle;
    
        @ManyToOne
        @JoinColumn(name = "type", nullable = false)
        private VehicleTypeModel vehicleTypeModel;
    
    
        @Column(nullable = false, updatable = false)
        @CreationTimestamp
        private ZonedDateTime createdAt;
    
        @UpdateTimestamp
        private ZonedDateTime updatedAt;
    }
