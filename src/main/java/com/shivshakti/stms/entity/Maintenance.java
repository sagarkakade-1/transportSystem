package com.shivshakti.stms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maintenance entity representing vehicle maintenance records
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "maintenances")
public class Maintenance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "maintenance_number", unique = true, nullable = false, length = 20)
    private String maintenanceNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;
    
    @Column(name = "maintenance_type", nullable = false, length = 20)
    private String maintenanceType;
    
    @Column(name = "service_category", nullable = false, length = 20)
    private String serviceCategory;
    
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;
    
    @Column(name = "completed_date")
    private LocalDate completedDate;
    
    @Column(name = "current_odometer", precision = 8, scale = 2, nullable = false)
    private BigDecimal currentOdometer;
    
    @Column(name = "next_service_odometer", precision = 8, scale = 2)
    private BigDecimal nextServiceOdometer;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "SCHEDULED";
    
    @Column(name = "priority", nullable = false, length = 20)
    private String priority = "MEDIUM";
    
    @Column(name = "service_provider", length = 100)
    private String serviceProvider;
    
    @Column(name = "service_location", length = 200)
    private String serviceLocation;
    
    @Column(name = "labor_cost", precision = 10, scale = 2)
    private BigDecimal laborCost = BigDecimal.ZERO;
    
    @Column(name = "parts_cost", precision = 10, scale = 2)
    private BigDecimal partsCost = BigDecimal.ZERO;
    
    @Column(name = "other_charges", precision = 10, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;
    
    @Column(name = "gst_amount", precision = 8, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @Column(name = "work_performed", length = 500)
    private String workPerformed;
    
    @Column(name = "parts_replaced", length = 500)
    private String partsReplaced;
    
    @Column(name = "recommendations", length = 500)
    private String recommendations;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;
    
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    
    @Column(name = "warranty_period", length = 50)
    private String warrantyPeriod;
    
    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;
    
    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;
    
    @Column(name = "service_interval_days")
    private Integer serviceIntervalDays;
    
    @Column(name = "service_interval_km", precision = 8, scale = 2)
    private BigDecimal serviceIntervalKm;
    
    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    // Constructors
    public Maintenance() {
        this.createdDate = LocalDateTime.now();
    }
    
    public Maintenance(String maintenanceNumber, Truck truck, String maintenanceType, 
                      String serviceCategory, String description, LocalDate scheduledDate, 
                      BigDecimal currentOdometer) {
        this();
        this.maintenanceNumber = maintenanceNumber;
        this.truck = truck;
        this.maintenanceType = maintenanceType;
        this.serviceCategory = serviceCategory;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.currentOdometer = currentOdometer;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMaintenanceNumber() { return maintenanceNumber; }
    public void setMaintenanceNumber(String maintenanceNumber) { this.maintenanceNumber = maintenanceNumber; }
    
    public Truck getTruck() { return truck; }
    public void setTruck(Truck truck) { this.truck = truck; }
    
    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
    
    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    
    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
    
    public BigDecimal getCurrentOdometer() { return currentOdometer; }
    public void setCurrentOdometer(BigDecimal currentOdometer) { this.currentOdometer = currentOdometer; }
    
    public BigDecimal getNextServiceOdometer() { return nextServiceOdometer; }
    public void setNextServiceOdometer(BigDecimal nextServiceOdometer) { this.nextServiceOdometer = nextServiceOdometer; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getServiceProvider() { return serviceProvider; }
    public void setServiceProvider(String serviceProvider) { this.serviceProvider = serviceProvider; }
    
    public String getServiceLocation() { return serviceLocation; }
    public void setServiceLocation(String serviceLocation) { this.serviceLocation = serviceLocation; }
    
    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }
    
    public BigDecimal getPartsCost() { return partsCost; }
    public void setPartsCost(BigDecimal partsCost) { this.partsCost = partsCost; }
    
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    
    public BigDecimal getGstAmount() { return gstAmount; }
    public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
    
    public String getWorkPerformed() { return workPerformed; }
    public void setWorkPerformed(String workPerformed) { this.workPerformed = workPerformed; }
    
    public String getPartsReplaced() { return partsReplaced; }
    public void setPartsReplaced(String partsReplaced) { this.partsReplaced = partsReplaced; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public String getWarrantyPeriod() { return warrantyPeriod; }
    public void setWarrantyPeriod(String warrantyPeriod) { this.warrantyPeriod = warrantyPeriod; }
    
    public LocalDate getWarrantyExpiryDate() { return warrantyExpiryDate; }
    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; }
    
    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }
    
    public Integer getServiceIntervalDays() { return serviceIntervalDays; }
    public void setServiceIntervalDays(Integer serviceIntervalDays) { this.serviceIntervalDays = serviceIntervalDays; }
    
    public BigDecimal getServiceIntervalKm() { return serviceIntervalKm; }
    public void setServiceIntervalKm(BigDecimal serviceIntervalKm) { this.serviceIntervalKm = serviceIntervalKm; }
    
    public LocalDate getNextServiceDate() { return nextServiceDate; }
    public void setNextServiceDate(LocalDate nextServiceDate) { this.nextServiceDate = nextServiceDate; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
    
    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Maintenance{" +
                "id=" + id +
                ", maintenanceNumber='" + maintenanceNumber + '\'' +
                ", maintenanceType='" + maintenanceType + '\'' +
                ", serviceCategory='" + serviceCategory + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}

