package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Truck entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TruckDTO {
    
    private Long id;
    
    @NotBlank(message = "Truck number is required")
    @Size(min = 4, max = 20, message = "Truck number must be between 4 and 20 characters")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$", 
             message = "Invalid truck number format (e.g., MH12AB1234)")
    private String truckNumber;
    
    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    private String model;
    
    @NotNull(message = "Capacity is required")
    @DecimalMin(value = "0.1", message = "Capacity must be greater than 0")
    @Digits(integer = 5, fraction = 2, message = "Invalid capacity format")
    private BigDecimal capacity; // in tons
    
    @NotBlank(message = "Fuel type is required")
    @Pattern(regexp = "^(DIESEL|PETROL|CNG|LPG)$", message = "Fuel type must be DIESEL, PETROL, CNG, or LPG")
    private String fuelType;
    
    @DecimalMin(value = "0.0", message = "Fuel tank capacity cannot be negative")
    @Digits(integer = 4, fraction = 2, message = "Invalid fuel tank capacity format")
    private BigDecimal fuelTankCapacity; // in liters
    
    @DecimalMin(value = "0.0", message = "Mileage cannot be negative")
    @Digits(integer = 3, fraction = 2, message = "Invalid mileage format")
    private BigDecimal mileage; // km per liter
    
    @Past(message = "Purchase date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;
    
    @DecimalMin(value = "0.0", message = "Purchase price cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid purchase price format")
    private BigDecimal purchasePrice;
    
    // Document Details
    @NotBlank(message = "RC book number is required")
    @Size(min = 5, max = 30, message = "RC book number must be between 5 and 30 characters")
    private String rcBookNumber;
    
    @NotNull(message = "RC expiry date is required")
    @Future(message = "RC expiry date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rcExpiryDate;
    
    @NotBlank(message = "Insurance policy number is required")
    @Size(min = 5, max = 50, message = "Insurance policy number must be between 5 and 50 characters")
    private String insurancePolicyNumber;
    
    @NotNull(message = "Insurance expiry date is required")
    @Future(message = "Insurance expiry date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate insuranceExpiryDate;
    
    @Size(max = 50, message = "Permit number cannot exceed 50 characters")
    private String permitNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate permitExpiryDate;
    
    @Size(max = 50, message = "Fitness certificate number cannot exceed 50 characters")
    private String fitnessCertificateNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fitnessExpiryDate;
    
    @Size(max = 50, message = "PUC certificate number cannot exceed 50 characters")
    private String pucCertificateNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pucExpiryDate;
    
    // Operational Details
    @DecimalMin(value = "0.0", message = "Current odometer reading cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid odometer reading format")
    private BigDecimal currentOdometerReading; // in km
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastServiceDate;
    
    @DecimalMin(value = "0.0", message = "Next service due cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid next service due format")
    private BigDecimal nextServiceDue; // odometer reading
    
    private String remarks;
    
    private Boolean isActive = true;
    
    // Performance metrics (read-only)
    private Integer totalTrips;
    private BigDecimal totalDistanceCovered;
    private BigDecimal totalFuelConsumed;
    private BigDecimal averageFuelEfficiency;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal profitability;
    private Double utilizationPercentage;
    private Integer maintenanceCount;
    private BigDecimal maintenanceCost;
    
    // Constructors
    public TruckDTO() {}
    
    public TruckDTO(String truckNumber, String model, BigDecimal capacity, String fuelType) {
        this.truckNumber = truckNumber;
        this.model = model;
        this.capacity = capacity;
        this.fuelType = fuelType;
    }
    
    public TruckDTO(String truckNumber, String model, BigDecimal capacity, String fuelType,
                    String rcBookNumber, LocalDate rcExpiryDate, 
                    String insurancePolicyNumber, LocalDate insuranceExpiryDate) {
        this.truckNumber = truckNumber;
        this.model = model;
        this.capacity = capacity;
        this.fuelType = fuelType;
        this.rcBookNumber = rcBookNumber;
        this.rcExpiryDate = rcExpiryDate;
        this.insurancePolicyNumber = insurancePolicyNumber;
        this.insuranceExpiryDate = insuranceExpiryDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public BigDecimal getCapacity() {
        return capacity;
    }
    
    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public BigDecimal getFuelTankCapacity() {
        return fuelTankCapacity;
    }
    
    public void setFuelTankCapacity(BigDecimal fuelTankCapacity) {
        this.fuelTankCapacity = fuelTankCapacity;
    }
    
    public BigDecimal getMileage() {
        return mileage;
    }
    
    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
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
    
    public String getRcBookNumber() {
        return rcBookNumber;
    }
    
    public void setRcBookNumber(String rcBookNumber) {
        this.rcBookNumber = rcBookNumber;
    }
    
    public LocalDate getRcExpiryDate() {
        return rcExpiryDate;
    }
    
    public void setRcExpiryDate(LocalDate rcExpiryDate) {
        this.rcExpiryDate = rcExpiryDate;
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
    
    public BigDecimal getCurrentOdometerReading() {
        return currentOdometerReading;
    }
    
    public void setCurrentOdometerReading(BigDecimal currentOdometerReading) {
        this.currentOdometerReading = currentOdometerReading;
    }
    
    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }
    
    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }
    
    public BigDecimal getNextServiceDue() {
        return nextServiceDue;
    }
    
    public void setNextServiceDue(BigDecimal nextServiceDue) {
        this.nextServiceDue = nextServiceDue;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // Performance metrics getters and setters
    public Integer getTotalTrips() {
        return totalTrips;
    }
    
    public void setTotalTrips(Integer totalTrips) {
        this.totalTrips = totalTrips;
    }
    
    public BigDecimal getTotalDistanceCovered() {
        return totalDistanceCovered;
    }
    
    public void setTotalDistanceCovered(BigDecimal totalDistanceCovered) {
        this.totalDistanceCovered = totalDistanceCovered;
    }
    
    public BigDecimal getTotalFuelConsumed() {
        return totalFuelConsumed;
    }
    
    public void setTotalFuelConsumed(BigDecimal totalFuelConsumed) {
        this.totalFuelConsumed = totalFuelConsumed;
    }
    
    public BigDecimal getAverageFuelEfficiency() {
        return averageFuelEfficiency;
    }
    
    public void setAverageFuelEfficiency(BigDecimal averageFuelEfficiency) {
        this.averageFuelEfficiency = averageFuelEfficiency;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }
    
    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
    
    public BigDecimal getProfitability() {
        return profitability;
    }
    
    public void setProfitability(BigDecimal profitability) {
        this.profitability = profitability;
    }
    
    public Double getUtilizationPercentage() {
        return utilizationPercentage;
    }
    
    public void setUtilizationPercentage(Double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }
    
    public Integer getMaintenanceCount() {
        return maintenanceCount;
    }
    
    public void setMaintenanceCount(Integer maintenanceCount) {
        this.maintenanceCount = maintenanceCount;
    }
    
    public BigDecimal getMaintenanceCost() {
        return maintenanceCost;
    }
    
    public void setMaintenanceCost(BigDecimal maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }
    
    // Utility methods
    public boolean isRcExpiringSoon(int days) {
        return rcExpiryDate != null && rcExpiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public boolean isInsuranceExpiringSoon(int days) {
        return insuranceExpiryDate != null && insuranceExpiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public boolean isPermitExpiringSoon(int days) {
        return permitExpiryDate != null && permitExpiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public boolean isFitnessExpiringSoon(int days) {
        return fitnessExpiryDate != null && fitnessExpiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public boolean isPucExpiringSoon(int days) {
        return pucExpiryDate != null && pucExpiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public boolean hasAnyDocumentExpiringSoon(int days) {
        return isRcExpiringSoon(days) || isInsuranceExpiringSoon(days) || 
               isPermitExpiringSoon(days) || isFitnessExpiringSoon(days) || isPucExpiringSoon(days);
    }
    
    public boolean isServiceDue() {
        return currentOdometerReading != null && nextServiceDue != null &&
               currentOdometerReading.compareTo(nextServiceDue) >= 0;
    }
    
    public boolean isProfitable() {
        return profitability != null && profitability.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isHighUtilization() {
        return utilizationPercentage != null && utilizationPercentage > 80.0;
    }
    
    @Override
    public String toString() {
        return "TruckDTO{" +
                "id=" + id +
                ", truckNumber='" + truckNumber + '\'' +
                ", model='" + model + '\'' +
                ", capacity=" + capacity +
                ", fuelType='" + fuelType + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
