package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Trip entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripDTO {
    
    private Long id;
    
    @NotBlank(message = "Trip number is required")
    @Size(min = 5, max = 20, message = "Trip number must be between 5 and 20 characters")
    private String tripNumber;
    
    @NotNull(message = "Truck ID is required")
    private Long truckId;
    
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    @NotNull(message = "Client ID is required")
    private Long clientId;
    
    @NotBlank(message = "Source location is required")
    @Size(min = 2, max = 100, message = "Source location must be between 2 and 100 characters")
    private String sourceLocation;
    
    @NotBlank(message = "Destination location is required")
    @Size(min = 2, max = 100, message = "Destination location must be between 2 and 100 characters")
    private String destinationLocation;
    
    @NotNull(message = "Planned start date is required")
    @Future(message = "Planned start date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedStartDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime plannedEndDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualStartDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualEndDate;
    
    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Invalid distance format")
    private BigDecimal distance; // in kilometers
    
    @DecimalMin(value = "0.0", message = "Load weight cannot be negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid load weight format")
    private BigDecimal loadWeight; // in tons
    
    @Size(max = 200, message = "Load description cannot exceed 200 characters")
    private String loadDescription;
    
    @NotNull(message = "Trip charges are required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Trip charges must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid trip charges format")
    private BigDecimal tripCharges;
    
    @DecimalMin(value = "0.0", message = "Advance amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid advance amount format")
    private BigDecimal advanceAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Fuel consumed cannot be negative")
    @Digits(integer = 6, fraction = 2, message = "Invalid fuel consumed format")
    private BigDecimal fuelConsumed; // in liters
    
    @DecimalMin(value = "0.0", message = "Fuel cost cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid fuel cost format")
    private BigDecimal fuelCost;
    
    @DecimalMin(value = "0.0", message = "Toll charges cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid toll charges format")
    private BigDecimal tollCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Other expenses cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid other expenses format")
    private BigDecimal otherExpenses = BigDecimal.ZERO;
    
    @NotBlank(message = "Trip status is required")
    @Pattern(regexp = "^(PLANNED|RUNNING|COMPLETED|CANCELLED)$", 
             message = "Trip status must be PLANNED, RUNNING, COMPLETED, or CANCELLED")
    private String status = "PLANNED";
    
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    
    // Related entity information (read-only)
    private String truckNumber;
    private String driverName;
    private String clientName;
    private String truckModel;
    private BigDecimal truckCapacity;
    
    // Calculated fields (read-only)
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private BigDecimal profitMargin;
    private BigDecimal fuelEfficiency; // km per liter
    private Long durationHours;
    private BigDecimal averageSpeed; // km/hour
    private BigDecimal balanceAmount;
    private Double capacityUtilization; // percentage
    
    // Constructors
    public TripDTO() {}
    
    public TripDTO(String tripNumber, Long truckId, Long driverId, Long clientId,
                   String sourceLocation, String destinationLocation, 
                   LocalDateTime plannedStartDate, BigDecimal distance, BigDecimal tripCharges) {
        this.tripNumber = tripNumber;
        this.truckId = truckId;
        this.driverId = driverId;
        this.clientId = clientId;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.plannedStartDate = plannedStartDate;
        this.distance = distance;
        this.tripCharges = tripCharges;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTripNumber() {
        return tripNumber;
    }
    
    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }
    
    public Long getTruckId() {
        return truckId;
    }
    
    public void setTruckId(Long truckId) {
        this.truckId = truckId;
    }
    
    public Long getDriverId() {
        return driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getSourceLocation() {
        return sourceLocation;
    }
    
    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }
    
    public String getDestinationLocation() {
        return destinationLocation;
    }
    
    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }
    
    public LocalDateTime getPlannedStartDate() {
        return plannedStartDate;
    }
    
    public void setPlannedStartDate(LocalDateTime plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }
    
    public LocalDateTime getPlannedEndDate() {
        return plannedEndDate;
    }
    
    public void setPlannedEndDate(LocalDateTime plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }
    
    public LocalDateTime getActualStartDate() {
        return actualStartDate;
    }
    
    public void setActualStartDate(LocalDateTime actualStartDate) {
        this.actualStartDate = actualStartDate;
    }
    
    public LocalDateTime getActualEndDate() {
        return actualEndDate;
    }
    
    public void setActualEndDate(LocalDateTime actualEndDate) {
        this.actualEndDate = actualEndDate;
    }
    
    public BigDecimal getDistance() {
        return distance;
    }
    
    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }
    
    public BigDecimal getLoadWeight() {
        return loadWeight;
    }
    
    public void setLoadWeight(BigDecimal loadWeight) {
        this.loadWeight = loadWeight;
    }
    
    public String getLoadDescription() {
        return loadDescription;
    }
    
    public void setLoadDescription(String loadDescription) {
        this.loadDescription = loadDescription;
    }
    
    public BigDecimal getTripCharges() {
        return tripCharges;
    }
    
    public void setTripCharges(BigDecimal tripCharges) {
        this.tripCharges = tripCharges;
    }
    
    public BigDecimal getAdvanceAmount() {
        return advanceAmount;
    }
    
    public void setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
    }
    
    public BigDecimal getFuelConsumed() {
        return fuelConsumed;
    }
    
    public void setFuelConsumed(BigDecimal fuelConsumed) {
        this.fuelConsumed = fuelConsumed;
    }
    
    public BigDecimal getFuelCost() {
        return fuelCost;
    }
    
    public void setFuelCost(BigDecimal fuelCost) {
        this.fuelCost = fuelCost;
    }
    
    public BigDecimal getTollCharges() {
        return tollCharges;
    }
    
    public void setTollCharges(BigDecimal tollCharges) {
        this.tollCharges = tollCharges;
    }
    
    public BigDecimal getOtherExpenses() {
        return otherExpenses;
    }
    
    public void setOtherExpenses(BigDecimal otherExpenses) {
        this.otherExpenses = otherExpenses;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    // Related entity getters and setters
    public String getTruckNumber() {
        return truckNumber;
    }
    
    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }
    
    public String getDriverName() {
        return driverName;
    }
    
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getTruckModel() {
        return truckModel;
    }
    
    public void setTruckModel(String truckModel) {
        this.truckModel = truckModel;
    }
    
    public BigDecimal getTruckCapacity() {
        return truckCapacity;
    }
    
    public void setTruckCapacity(BigDecimal truckCapacity) {
        this.truckCapacity = truckCapacity;
    }
    
    // Calculated fields getters and setters
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }
    
    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
    
    public BigDecimal getNetProfit() {
        return netProfit;
    }
    
    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }
    
    public BigDecimal getProfitMargin() {
        return profitMargin;
    }
    
    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }
    
    public BigDecimal getFuelEfficiency() {
        return fuelEfficiency;
    }
    
    public void setFuelEfficiency(BigDecimal fuelEfficiency) {
        this.fuelEfficiency = fuelEfficiency;
    }
    
    public Long getDurationHours() {
        return durationHours;
    }
    
    public void setDurationHours(Long durationHours) {
        this.durationHours = durationHours;
    }
    
    public BigDecimal getAverageSpeed() {
        return averageSpeed;
    }
    
    public void setAverageSpeed(BigDecimal averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
    
    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }
    
    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
    
    public Double getCapacityUtilization() {
        return capacityUtilization;
    }
    
    public void setCapacityUtilization(Double capacityUtilization) {
        this.capacityUtilization = capacityUtilization;
    }
    
    // Utility methods
    public boolean isPlanned() {
        return "PLANNED".equals(status);
    }
    
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
    
    public boolean isProfitable() {
        return netProfit != null && netProfit.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isOverloaded() {
        return loadWeight != null && truckCapacity != null && 
               loadWeight.compareTo(truckCapacity) > 0;
    }
    
    public boolean isDelayed() {
        if (actualEndDate != null && plannedEndDate != null) {
            return actualEndDate.isAfter(plannedEndDate);
        }
        return false;
    }
    
    public boolean hasOutstandingBalance() {
        return balanceAmount != null && balanceAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public BigDecimal calculateTotalExpenses() {
        BigDecimal total = BigDecimal.ZERO;
        if (fuelCost != null) total = total.add(fuelCost);
        if (tollCharges != null) total = total.add(tollCharges);
        if (otherExpenses != null) total = total.add(otherExpenses);
        return total;
    }
    
    public BigDecimal calculateNetProfit() {
        if (tripCharges == null) return BigDecimal.ZERO;
        return tripCharges.subtract(calculateTotalExpenses());
    }
    
    public BigDecimal calculateProfitMargin() {
        if (tripCharges == null || tripCharges.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return calculateNetProfit().divide(tripCharges, 4, BigDecimal.ROUND_HALF_UP)
                                  .multiply(BigDecimal.valueOf(100));
    }
    
    public BigDecimal calculateFuelEfficiency() {
        if (distance == null || fuelConsumed == null || 
            fuelConsumed.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return distance.divide(fuelConsumed, 2, BigDecimal.ROUND_HALF_UP);
    }
    
    public Double calculateCapacityUtilization() {
        if (loadWeight == null || truckCapacity == null || 
            truckCapacity.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return loadWeight.divide(truckCapacity, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
    }
    
    @Override
    public String toString() {
        return "TripDTO{" +
                "id=" + id +
                ", tripNumber='" + tripNumber + '\'' +
                ", truckNumber='" + truckNumber + '\'' +
                ", driverName='" + driverName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", sourceLocation='" + sourceLocation + '\'' +
                ", destinationLocation='" + destinationLocation + '\'' +
                ", status='" + status + '\'' +
                ", distance=" + distance +
                ", tripCharges=" + tripCharges +
                '}';
    }
}
