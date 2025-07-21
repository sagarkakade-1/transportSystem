package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Truck Entity - Represents trucks/vehicles in the transport system
 * Manages complete truck information including documentation and maintenance
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "trucks", indexes = {
    @Index(name = "idx_truck_number", columnList = "truck_number"),
    @Index(name = "idx_truck_active", columnList = "is_active"),
    @Index(name = "idx_truck_insurance_expiry", columnList = "insurance_expiry_date"),
    @Index(name = "idx_truck_permit_expiry", columnList = "permit_expiry_date")
})
public class Truck extends BaseEntity {

    public enum FuelType {
        DIESEL("Diesel"),
        PETROL("Petrol"),
        CNG("CNG");

        private final String displayName;

        FuelType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PermitType {
        NATIONAL("National Permit"),
        STATE("State Permit"),
        DISTRICT("District Permit");

        private final String displayName;

        PermitType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Truck number is required")
    @Size(max = 20, message = "Truck number must not exceed 20 characters")
    @Column(name = "truck_number", nullable = false, unique = true, length = 20)
    private String truckNumber;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    @Column(name = "make", nullable = false, length = 50)
    private String make;

    @NotNull(message = "Capacity is required")
    @DecimalMin(value = "0.1", message = "Capacity must be greater than 0")
    @Digits(integer = 3, fraction = 2, message = "Capacity must have at most 3 integer digits and 2 decimal places")
    @Column(name = "capacity_tons", nullable = false, precision = 5, scale = 2)
    private BigDecimal capacityTons;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", length = 20)
    private FuelType fuelType = FuelType.DIESEL;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @DecimalMin(value = "0.0", message = "Purchase price must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Purchase price must have at most 10 integer digits and 2 decimal places")
    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    // Insurance Details
    @Size(max = 100, message = "Insurance company name must not exceed 100 characters")
    @Column(name = "insurance_company", length = 100)
    private String insuranceCompany;

    @Size(max = 50, message = "Insurance policy number must not exceed 50 characters")
    @Column(name = "insurance_policy_number", length = 50)
    private String insurancePolicyNumber;

    @Column(name = "insurance_expiry_date")
    private LocalDate insuranceExpiryDate;

    @DecimalMin(value = "0.0", message = "Insurance amount must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Insurance amount must have at most 8 integer digits and 2 decimal places")
    @Column(name = "insurance_amount", precision = 10, scale = 2)
    private BigDecimal insuranceAmount;

    // RC Book Details
    @Size(max = 50, message = "RC number must not exceed 50 characters")
    @Column(name = "rc_number", length = 50)
    private String rcNumber;

    @Column(name = "rc_expiry_date")
    private LocalDate rcExpiryDate;

    // Permit Details
    @Size(max = 50, message = "Permit number must not exceed 50 characters")
    @Column(name = "permit_number", length = 50)
    private String permitNumber;

    @Column(name = "permit_expiry_date")
    private LocalDate permitExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "permit_type", length = 50)
    private PermitType permitType;

    // Fitness Details
    @Size(max = 50, message = "Fitness certificate number must not exceed 50 characters")
    @Column(name = "fitness_certificate_number", length = 50)
    private String fitnessCertificateNumber;

    @Column(name = "fitness_expiry_date")
    private LocalDate fitnessExpiryDate;

    // PUC Details
    @Size(max = 50, message = "PUC certificate number must not exceed 50 characters")
    @Column(name = "puc_certificate_number", length = 50)
    private String pucCertificateNumber;

    @Column(name = "puc_expiry_date")
    private LocalDate pucExpiryDate;

    @DecimalMin(value = "0.0", message = "Current mileage must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Current mileage must have at most 8 integer digits and 2 decimal places")
    @Column(name = "current_mileage", precision = 10, scale = 2)
    private BigDecimal currentMileage = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips = new ArrayList<>();

    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Maintenance> maintenanceRecords = new ArrayList<>();

    // Constructors
    public Truck() {
    }

    public Truck(String truckNumber, String model, String make, BigDecimal capacityTons) {
        this.truckNumber = truckNumber;
        this.model = model;
        this.make = make;
        this.capacityTons = capacityTons;
    }

    // Business Methods
    public boolean isInsuranceExpired() {
        return insuranceExpiryDate != null && insuranceExpiryDate.isBefore(LocalDate.now());
    }

    public boolean isInsuranceExpiringSoon(int daysThreshold) {
        return insuranceExpiryDate != null && 
               insuranceExpiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public boolean isRcExpired() {
        return rcExpiryDate != null && rcExpiryDate.isBefore(LocalDate.now());
    }

    public boolean isPermitExpired() {
        return permitExpiryDate != null && permitExpiryDate.isBefore(LocalDate.now());
    }

    public boolean isFitnessExpired() {
        return fitnessExpiryDate != null && fitnessExpiryDate.isBefore(LocalDate.now());
    }

    public boolean isPucExpired() {
        return pucExpiryDate != null && pucExpiryDate.isBefore(LocalDate.now());
    }

    public boolean hasAnyDocumentExpired() {
        return isInsuranceExpired() || isRcExpired() || isPermitExpired() || 
               isFitnessExpired() || isPucExpired();
    }

    public boolean hasAnyDocumentExpiringSoon(int daysThreshold) {
        LocalDate threshold = LocalDate.now().plusDays(daysThreshold);
        return (insuranceExpiryDate != null && insuranceExpiryDate.isBefore(threshold)) ||
               (rcExpiryDate != null && rcExpiryDate.isBefore(threshold)) ||
               (permitExpiryDate != null && permitExpiryDate.isBefore(threshold)) ||
               (fitnessExpiryDate != null && fitnessExpiryDate.isBefore(threshold)) ||
               (pucExpiryDate != null && pucExpiryDate.isBefore(threshold));
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
        trip.setTruck(this);
    }

    public void removeTrip(Trip trip) {
        trips.remove(trip);
        trip.setTruck(null);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setTruck(this);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setTruck(null);
    }

    public void addMaintenanceRecord(Maintenance maintenance) {
        maintenanceRecords.add(maintenance);
        maintenance.setTruck(this);
    }

    public void removeMaintenanceRecord(Maintenance maintenance) {
        maintenanceRecords.remove(maintenance);
        maintenance.setTruck(null);
    }

    // Getters and Setters
    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public BigDecimal getCapacityTons() {
        return capacityTons;
    }

    public void setCapacityTons(BigDecimal capacityTons) {
        this.capacityTons = capacityTons;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public LocalDate getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public BigDecimal getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(BigDecimal insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public LocalDate getRcExpiryDate() {
        return rcExpiryDate;
    }

    public void setRcExpiryDate(LocalDate rcExpiryDate) {
        this.rcExpiryDate = rcExpiryDate;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public LocalDate getPermitExpiryDate() {
        return permitExpiryDate;
    }

    public void setPermitExpiryDate(LocalDate permitExpiryDate) {
        this.permitExpiryDate = permitExpiryDate;
    }

    public PermitType getPermitType() {
        return permitType;
    }

    public void setPermitType(PermitType permitType) {
        this.permitType = permitType;
    }

    public String getFitnessCertificateNumber() {
        return fitnessCertificateNumber;
    }

    public void setFitnessCertificateNumber(String fitnessCertificateNumber) {
        this.fitnessCertificateNumber = fitnessCertificateNumber;
    }

    public LocalDate getFitnessExpiryDate() {
        return fitnessExpiryDate;
    }

    public void setFitnessExpiryDate(LocalDate fitnessExpiryDate) {
        this.fitnessExpiryDate = fitnessExpiryDate;
    }

    public String getPucCertificateNumber() {
        return pucCertificateNumber;
    }

    public void setPucCertificateNumber(String pucCertificateNumber) {
        this.pucCertificateNumber = pucCertificateNumber;
    }

    public LocalDate getPucExpiryDate() {
        return pucExpiryDate;
    }

    public void setPucExpiryDate(LocalDate pucExpiryDate) {
        this.pucExpiryDate = pucExpiryDate;
    }

    public BigDecimal getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(BigDecimal currentMileage) {
        this.currentMileage = currentMileage;
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

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Maintenance> getMaintenanceRecords() {
        return maintenanceRecords;
    }

    public void setMaintenanceRecords(List<Maintenance> maintenanceRecords) {
        this.maintenanceRecords = maintenanceRecords;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "id=" + getId() +
                ", truckNumber='" + truckNumber + '\'' +
                ", model='" + model + '\'' +
                ", make='" + make + '\'' +
                ", capacityTons=" + capacityTons +
                ", isActive=" + isActive +
                '}';
    }
}

