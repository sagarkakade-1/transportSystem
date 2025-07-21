package com.shivshakti.stms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Trip entity representing transport trips
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "trips")
public class Trip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trip_number", unique = true, nullable = false, length = 20)
    private String tripNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    
    @Column(name = "source_location", nullable = false, length = 100)
    private String sourceLocation;
    
    @Column(name = "destination_location", nullable = false, length = 100)
    private String destinationLocation;
    
    @Column(name = "distance", precision = 8, scale = 2)
    private BigDecimal distance;
    
    @Column(name = "load_weight", precision = 8, scale = 2)
    private BigDecimal loadWeight;
    
    @Column(name = "load_description", length = 200)
    private String loadDescription;
    
    @Column(name = "planned_start_date", nullable = false)
    private LocalDateTime plannedStartDate;
    
    @Column(name = "planned_end_date")
    private LocalDateTime plannedEndDate;
    
    @Column(name = "actual_start_date")
    private LocalDateTime actualStartDate;
    
    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;
    
    @Column(name = "trip_charges", precision = 10, scale = 2, nullable = false)
    private BigDecimal tripCharges;
    
    @Column(name = "advance_amount", precision = 10, scale = 2)
    private BigDecimal advanceAmount = BigDecimal.ZERO;
    
    @Column(name = "fuel_consumed", precision = 8, scale = 2)
    private BigDecimal fuelConsumed;
    
    @Column(name = "fuel_cost", precision = 10, scale = 2)
    private BigDecimal fuelCost;
    
    @Column(name = "toll_charges", precision = 8, scale = 2)
    private BigDecimal tollCharges;
    
    @Column(name = "other_expenses", precision = 8, scale = 2)
    private BigDecimal otherExpenses;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PLANNED";
    
    @Column(name = "start_odometer", precision = 10, scale = 2)
    private BigDecimal startOdometer;
    
    @Column(name = "end_odometer", precision = 10, scale = 2)
    private BigDecimal endOdometer;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    // Relationships
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Builty> builties;
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses;
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Income> incomes;
    
    // Constructors
    public Trip() {
        this.createdDate = LocalDateTime.now();
    }
    
    public Trip(String tripNumber, Truck truck, Driver driver, String sourceLocation, 
                String destinationLocation, LocalDateTime plannedStartDate, BigDecimal tripCharges) {
        this();
        this.tripNumber = tripNumber;
        this.truck = truck;
        this.driver = driver;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.plannedStartDate = plannedStartDate;
        this.tripCharges = tripCharges;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTripNumber() { return tripNumber; }
    public void setTripNumber(String tripNumber) { this.tripNumber = tripNumber; }
    
    public Truck getTruck() { return truck; }
    public void setTruck(Truck truck) { this.truck = truck; }
    
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public String getSourceLocation() { return sourceLocation; }
    public void setSourceLocation(String sourceLocation) { this.sourceLocation = sourceLocation; }
    
    public String getDestinationLocation() { return destinationLocation; }
    public void setDestinationLocation(String destinationLocation) { this.destinationLocation = destinationLocation; }
    
    public BigDecimal getDistance() { return distance; }
    public void setDistance(BigDecimal distance) { this.distance = distance; }
    
    public BigDecimal getLoadWeight() { return loadWeight; }
    public void setLoadWeight(BigDecimal loadWeight) { this.loadWeight = loadWeight; }
    
    public String getLoadDescription() { return loadDescription; }
    public void setLoadDescription(String loadDescription) { this.loadDescription = loadDescription; }
    
    public LocalDateTime getPlannedStartDate() { return plannedStartDate; }
    public void setPlannedStartDate(LocalDateTime plannedStartDate) { this.plannedStartDate = plannedStartDate; }
    
    public LocalDateTime getPlannedEndDate() { return plannedEndDate; }
    public void setPlannedEndDate(LocalDateTime plannedEndDate) { this.plannedEndDate = plannedEndDate; }
    
    public LocalDateTime getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(LocalDateTime actualStartDate) { this.actualStartDate = actualStartDate; }
    
    public LocalDateTime getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDateTime actualEndDate) { this.actualEndDate = actualEndDate; }
    
    public BigDecimal getTripCharges() { return tripCharges; }
    public void setTripCharges(BigDecimal tripCharges) { this.tripCharges = tripCharges; }
    
    public BigDecimal getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(BigDecimal advanceAmount) { this.advanceAmount = advanceAmount; }
    
    public BigDecimal getFuelConsumed() { return fuelConsumed; }
    public void setFuelConsumed(BigDecimal fuelConsumed) { this.fuelConsumed = fuelConsumed; }
    
    public BigDecimal getFuelCost() { return fuelCost; }
    public void setFuelCost(BigDecimal fuelCost) { this.fuelCost = fuelCost; }
    
    public BigDecimal getTollCharges() { return tollCharges; }
    public void setTollCharges(BigDecimal tollCharges) { this.tollCharges = tollCharges; }
    
    public BigDecimal getOtherExpenses() { return otherExpenses; }
    public void setOtherExpenses(BigDecimal otherExpenses) { this.otherExpenses = otherExpenses; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getStartOdometer() { return startOdometer; }
    public void setStartOdometer(BigDecimal startOdometer) { this.startOdometer = startOdometer; }
    
    public BigDecimal getEndOdometer() { return endOdometer; }
    public void setEndOdometer(BigDecimal endOdometer) { this.endOdometer = endOdometer; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
    
    public List<Builty> getBuilties() { return builties; }
    public void setBuilties(List<Builty> builties) { this.builties = builties; }
    
    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
    
    public List<Income> getIncomes() { return incomes; }
    public void setIncomes(List<Income> incomes) { this.incomes = incomes; }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", tripNumber='" + tripNumber + '\'' +
                ", sourceLocation='" + sourceLocation + '\'' +
                ", destinationLocation='" + destinationLocation + '\'' +
                ", status='" + status + '\'' +
                ", tripCharges=" + tripCharges +
                '}';
    }
}

