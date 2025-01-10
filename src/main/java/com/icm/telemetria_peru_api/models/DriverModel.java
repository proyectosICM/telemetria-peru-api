package com.icm.telemetria_peru_api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.ZonedDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver")
public class DriverModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z\\\\s\\\\u00E1\\\\u00E9\\\\u00ED\\\\u00F3\\\\u00FA\\\\u00F1\\\\u00D1]{1,100}$", message = "Name must be less than 100 characters" )
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z\\\\s\\\\u00E1\\\\u00E9\\\\u00ED\\\\u00F3\\\\u00FA\\\\u00F1\\\\u00D1]{1,100}$", message = "Last name must be less than 100 characters")
    @Column(name = "lastName", nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "Driver license number is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,50}$", message = "Driver license number must be alphanumeric (letters and numbers) and up to 50 characters")
    @Column(name = "driverLicense", nullable = false, length = 50)
    private String driverLicense;

    @NotNull(message = "License issue date is required")
    @Column(name = "licenseIssueDate", nullable = false)
    private LocalDate licenseIssueDate;

    @NotNull(message = "License expire date is required")
    @Column(name = "licenseExpireDate", nullable = false)
    private LocalDate licenseExpireDate;

    @Pattern(regexp = "^[+0-9\\-()\\s]{1,15}$", message = "Phone number must be up to 15 characters long and can only contain digits, plus signs, dashes, parentheses, and spaces")
    @Column(name = "driverPhoneNumber", length = 15)
    private String driverPhoneNumber;

    @NotNull(message = "Company associated is required")
    @ManyToOne(cascade =  CascadeType.REMOVE)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private CompanyModel companyModel;

    @NotBlank(message = "RFID number is required")
    @Pattern(regexp = "^\\d{1,50}$", message = "RFID must be a numeric value with up to 50 digits")
    @Column(name = "rfid", length = 50, unique = true)
    private String rfid;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
