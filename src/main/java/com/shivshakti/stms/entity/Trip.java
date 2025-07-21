package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Trip Entity - Core operational entity linking trucks, drivers, and business activities
 * Manages trip information, status tracking, and fuel consumption
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "trips", indexes = {
    @Index(name = "idx_trip_number", columnList = "trip_number"),
    @Index(name = "idx_trip_truck", columnList = "truck_id"),
    @Index(name = "idx_trip_driver", columnList = "driver_id"),
    @Index(name = "idx_trip_status", columnList = "status"),
    @Index(name = "idx_trip_start_date", columnList = "start_date")
})
public class Trip extends BaseEntity {

    public enum TripStatus {
        PENDING("Pending"),
        RUNNING("Running"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String displayName;

        TripStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Trip number is required")
    @Size(max = 50, message = "Trip number must not exceed 50 characters")
    @Column(name = "trip_number", nullable = false, unique = true, length = 50)
    private String tripNumber;

    @NotNull(message = "Truck is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trip_truck"))
    private Truck truck;

    @NotNull(message = "Driver is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trip_driver"))
    private Driver driver;

    @NotBlank(message = "Source location is required")
    @Size(max = 100, message = "Source location must not exceed 100 characters")
    @Column(name = "source_location", nullable = false, length = 100)
    private String sourceLocation;

    @NotBlank(message = "Destination location is required")
    @Size(max = 100, message = "Destination location must not exceed 100 characters")
    @Column(name = "destination_location", nullable = false, length = 100)
    private String destinationLocation;

    @DecimalMin(value = "0.0", message = "Distance must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Distance must have at most 6 integer digits and 2 decimal places")
    @Column(name = "distance_km", precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "actual_start_datetime")
    private LocalDateTime actualStartDatetime;

    @Column(name = "actual_end_datetime")
    private LocalDateTime actualEndDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private TripStatus status = TripStatus.PENDING;

    @DecimalMin(value = "0.0", message = "Fuel used must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Fuel used must have at most 6 integer digits and 2 decimal places")
    @Column(name = "fuel_used_liters", precision = 8, scale = 2)
    private BigDecimal fuelUsedLiters = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Fuel cost must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Fuel cost must have at most 8 integer digits and 2 decimal places")
    @Column(name = "fuel_cost", precision = 10, scale = 2)
    private BigDecimal fuelCost = BigDecimal.ZERO;

    @Size(max = 100, message = "Goods type must not exceed 100 characters")
    @Column(name = "goods_type", length = 100)
    private String goodsType;

    @DecimalMin(value = "0.0", message = "Total weight must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Total weight must have at most 6 integer digits and 2 decimal places")
    @Column(name = "total_weight_tons", precision = 8, scale = 2)
    private BigDecimal totalWeightTons;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Relationships
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Builty> builties = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Income> incomes = new ArrayList<>();

    // Constructors
    public Trip() {
    }

    public Trip(String tripNumber, Truck truck, Driver driver, String sourceLocation, String destinationLocation, LocalDate startDate) {
        this.tripNumber = tripNumber;
        this.truck = truck;
        this.driver = driver;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.startDate = startDate;
    }

    // Business Methods
    public boolean isPending() {
        return TripStatus.PENDING.equals(status);
    }

    public boolean isRunning() {
        return TripStatus.RUNNING.equals(status);
    }

    public boolean isCompleted() {
        return TripStatus.COMPLETED.equals(status);
    }

    public boolean isCancelled() {
        return TripStatus.CANCELLED.equals(status);
    }

    public void startTrip() {
        if (isPending()) {
            this.status = TripStatus.RUNNING;
            this.actualStartDatetime = LocalDateTime.now();
        }
    }

    public void completeTrip() {
        if (isRunning()) {
            this.status = TripStatus.COMPLETED;
            this.actualEndDatetime = LocalDateTime.now();
            if (this.endDate == null) {
                this.endDate = LocalDate.now();
            }
        }
    }

    public void cancelTrip() {
        if (isPending() || isRunning()) {
            this.status = TripStatus.CANCELLED;
        }
    }

    public BigDecimal getFuelEfficiency() {
        if (fuelUsedLiters != null && distanceKm != null && 
            fuelUsedLiters.compareTo(BigDecimal.ZERO) > 0) {
            return distanceKm.divide(fuelUsedLiters, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpenses() {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalIncome() {
        return incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getProfitLoss() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    public void addBuilty(Builty builty) {
        builties.add(builty);
        builty.setTrip(this);
    }

    public void removeBuilty(Builty builty) {
        builties.remove(builty);
        builty.setTrip(null);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setTrip(this);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setTrip(null);
    }

    public void addIncome(Income income) {
        incomes.add(income);
        income.setTrip(this);
    }

    public void removeIncome(Income income) {
        incomes.remove(income);
        income.setTrip(null);
    }

    // Getters and Setters
    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
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

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getActualStartDatetime() {
        return actualStartDatetime;
    }

    public void setActualStartDatetime(LocalDateTime actualStartDatetime) {
        this.actualStartDatetime = actualStartDatetime;
    }

    public LocalDateTime getActualEndDatetime() {
        return actualEndDatetime;
    }

    public void setActualEndDatetime(LocalDateTime actualEndDatetime) {
        this.actualEndDatetime = actualEndDatetime;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public BigDecimal getFuelUsedLiters() {
        return fuelUsedLiters;
    }

    public void setFuelUsedLiters(BigDecimal fuelUsedLiters) {
        this.fuelUsedLiters = fuelUsedLiters;
    }

    public BigDecimal getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(BigDecimal fuelCost) {
        this.fuelCost = fuelCost;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public BigDecimal getTotalWeightTons() {
        return totalWeightTons;
    }

    public void setTotalWeightTons(BigDecimal totalWeightTons) {
        this.totalWeightTons = totalWeightTons;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<Builty> getBuilties() {
        return builties;
    }

    public void setBuilties(List<Builty> builties) {
        this.builties = builties;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + getId() +
                ", tripNumber='" + tripNumber + '\'' +
                ", sourceLocation='" + sourceLocation + '\'' +
                ", destinationLocation='" + destinationLocation + '\'' +
                ", startDate=" + startDate +
                ", status=" + status +
                '}';
    }
}

