package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Driver Entity - Represents drivers in the transport system
 * Manages driver information, license details, and salary tracking
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "drivers", indexes = {
    @Index(name = "idx_driver_license", columnList = "license_number"),
    @Index(name = "idx_driver_contact", columnList = "contact_number"),
    @Index(name = "idx_driver_active", columnList = "is_active")
})
public class Driver extends BaseEntity {

    @NotBlank(message = "Driver name is required")
    @Size(max = 100, message = "Driver name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @NotNull(message = "License expiry date is required")
    @Future(message = "License expiry date must be in the future")
    @Column(name = "license_expiry_date", nullable = false)
    private LocalDate licenseExpiryDate;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Alternate contact must be 10-15 digits")
    @Column(name = "alternate_contact", length = 15)
    private String alternateContact;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @DecimalMin(value = "0.0", message = "Salary must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Salary must have at most 8 integer digits and 2 decimal places")
    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Advance paid must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Advance paid must have at most 8 integer digits and 2 decimal places")
    @Column(name = "advance_paid", precision = 10, scale = 2)
    private BigDecimal advancePaid = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips = new ArrayList<>();

    // Constructors
    public Driver() {
    }

    public Driver(String name, String licenseNumber, LocalDate licenseExpiryDate, String contactNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
        this.contactNumber = contactNumber;
    }

    // Business Methods
    public boolean isLicenseExpired() {
        return licenseExpiryDate != null && licenseExpiryDate.isBefore(LocalDate.now());
    }

    public boolean isLicenseExpiringSoon(int daysThreshold) {
        return licenseExpiryDate != null && 
               licenseExpiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public BigDecimal getOutstandingAdvance() {
        return advancePaid != null ? advancePaid : BigDecimal.ZERO;
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
        trip.setDriver(this);
    }

    public void removeTrip(Trip trip) {
        trips.remove(trip);
        trip.setDriver(null);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAlternateContact() {
        return alternateContact;
    }

    public void setAlternateContact(String alternateContact) {
        this.alternateContact = alternateContact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public BigDecimal getAdvancePaid() {
        return advancePaid;
    }

    public void setAdvancePaid(BigDecimal advancePaid) {
        this.advancePaid = advancePaid;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

