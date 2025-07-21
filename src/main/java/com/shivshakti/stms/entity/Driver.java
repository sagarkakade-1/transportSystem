package com.shivshakti.stms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Driver entity representing transport drivers
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "drivers")
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "driver_number", unique = true, nullable = false, length = 20)
    private String driverNumber;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "phone", nullable = false, length = 15)
    private String phone;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "address", length = 200)
    private String address;
    
    @Column(name = "license_number", unique = true, nullable = false, length = 50)
    private String licenseNumber;
    
    @Column(name = "license_type", nullable = false, length = 20)
    private String licenseType;
    
    @Column(name = "license_expiry_date", nullable = false)
    private LocalDate licenseExpiryDate;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;
    
    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;
    
    @Column(name = "advance_amount", precision = 10, scale = 2)
    private BigDecimal advanceAmount = BigDecimal.ZERO;
    
    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;
    
    @Column(name = "emergency_phone", length = 15)
    private String emergencyPhone;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    // Relationships
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips;
    
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses;
    
    // Constructors
    public Driver() {
        this.createdDate = LocalDateTime.now();
    }
    
    public Driver(String driverNumber, String name, String phone, String licenseNumber, 
                  String licenseType, LocalDate licenseExpiryDate, LocalDate hireDate) {
        this();
        this.driverNumber = driverNumber;
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
        this.licenseExpiryDate = licenseExpiryDate;
        this.hireDate = hireDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDriverNumber() { return driverNumber; }
    public void setDriverNumber(String driverNumber) { this.driverNumber = driverNumber; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }
    
    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    
    public BigDecimal getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(BigDecimal advanceAmount) { this.advanceAmount = advanceAmount; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
    
    public List<Trip> getTrips() { return trips; }
    public void setTrips(List<Trip> trips) { this.trips = trips; }
    
    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", driverNumber='" + driverNumber + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", licenseType='" + licenseType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

