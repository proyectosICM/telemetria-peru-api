package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @NotEmpty(message = "Username is required")
    @Size(max = 50, message = "Username must be less than 50 characters")
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(max = 100, message = "Password must be less than 100 characters")
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @NotEmpty(message = "Email is required")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Column(nullable = false, length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name = "company", referencedColumnName = "id", nullable = false)
    private CompanyModel companyModel;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("UTC"));;

    @UpdateTimestamp
    private ZonedDateTime updatedAt = ZonedDateTime.now(ZoneId.of("UTC"));;
}
