package com.shivshakti.stms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Truck entity representing transport vehicles
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "trucks")
public class Truck {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "truck_number", unique = true, nullable = false, length = 20)
    private String truckNumber;
    
    @Column(name = "model", nullable = false, length = 50)
    private String model;
    
    @Column(name = "manufacturer", length = 50)
    private String manufacturer;
    
    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;
    
    @Column(name = "capacity", precision = 8, scale = 2)
    private BigDecimal capacity;
    
    @Column(name = "fuel_type", nullable = false, length = 20)
    private String fuelType;
    
    @Column(name = "engine_number", length = 50)
    private String engineNumber;
    
    @Column(name = "chassis_number", length = 50)
    private String chassisNumber;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    @Column(name = "insurance_number", length = 50)
    private String insuranceNumber;
    
    @Column(name = "insurance_expiry_date")
    private LocalDate insuranceExpiryDate;
    
    @Column(name = "permit_number", length = 50)
    private String permitNumber;
    
    @Column(name = "permit_expiry_date")
    private LocalDate permitExpiryDate;
    
    @Column(name = "fitness_certificate_expiry")
    private LocalDate fitnessCertificateExpiry;
    
    @Column(name = "pollution_certificate_expiry")
    private LocalDate pollutionCertificateExpiry;
    
    @Column(name = "current_odometer", precision = 10, scale = 2)
    private BigDecimal currentOdometer = BigDecimal.ZERO;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "AVAILABLE";
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    // Relationships
    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips;
    
    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses;
    
    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Maintenance> maintenances;
    
    // Constructors
    public Truck() {
        this.createdDate = LocalDateTime.now();
    }
    
    public Truck(String truckNumber, String model, String fuelType, BigDecimal capacity) {
        this();
        this.truckNumber = truckNumber;
        this.model = model;
        this.fuelType = fuelType;
        this.capacity = capacity;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTruckNumber() { return truckNumber; }
    public void setTruckNumber(String truckNumber) { this.truckNumber = truckNumber; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    
    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    
    public BigDecimal getCapacity() { return capacity; }
    public void setCapacity(BigDecimal capacity) { this.capacity = capacity; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getEngineNumber() { return engineNumber; }
    public void setEngineNumber(String engineNumber) { this.engineNumber = engineNumber; }
    
    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    
    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }
    
    public LocalDate getInsuranceExpiryDate() { return insuranceExpiryDate; }
    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) { this.insuranceExpiryDate = insuranceExpiryDate; }
    
    public String getPermitNumber() { return permitNumber; }
    public void setPermitNumber(String permitNumber) { this.permitNumber = permitNumber; }
    
    public LocalDate getPermitExpiryDate() { return permitExpiryDate; }
    public void setPermitExpiryDate(LocalDate permitExpiryDate) { this.permitExpiryDate = permitExpiryDate; }
    
    public LocalDate getFitnessCertificateExpiry() { return fitnessCertificateExpiry; }
    public void setFitnessCertificateExpiry(LocalDate fitnessCertificateExpiry) { this.fitnessCertificateExpiry = fitnessCertificateExpiry; }
    
    public LocalDate getPollutionCertificateExpiry() { return pollutionCertificateExpiry; }
    public void setPollutionCertificateExpiry(LocalDate pollutionCertificateExpiry) { this.pollutionCertificateExpiry = pollutionCertificateExpiry; }
    
    public BigDecimal getCurrentOdometer() { return currentOdometer; }
    public void setCurrentOdometer(BigDecimal currentOdometer) { this.currentOdometer = currentOdometer; }
    
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
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
    
    public List<Maintenance> getMaintenances() { return maintenances; }
    public void setMaintenances(List<Maintenance> maintenances) { this.maintenances = maintenances; }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Truck{" +
                "id=" + id +
                ", truckNumber='" + truckNumber + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", capacity=" + capacity +
                ", fuelType='" + fuelType + '\'' +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

