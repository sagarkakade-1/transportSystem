package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Maintenance entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MaintenanceDTO {
    
    private Long id;
    
    @NotBlank(message = "Maintenance number is required")
    @Size(min = 5, max = 20, message = "Maintenance number must be between 5 and 20 characters")
    private String maintenanceNumber;
    
    @NotNull(message = "Truck ID is required")
    private Long truckId;
    
    @NotBlank(message = "Maintenance type is required")
    @Pattern(regexp = "^(PREVENTIVE|CORRECTIVE|EMERGENCY|SCHEDULED|BREAKDOWN)$", 
             message = "Invalid maintenance type")
    private String maintenanceType;
    
    @NotBlank(message = "Service category is required")
    @Pattern(regexp = "^(ENGINE|TRANSMISSION|BRAKES|TYRES|ELECTRICAL|BODY|GENERAL|OTHER)$", 
             message = "Invalid service category")
    private String serviceCategory;
    
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;
    
    @NotNull(message = "Scheduled date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate completedDate;
    
    @NotNull(message = "Current odometer reading is required")
    @DecimalMin(value = "0.0", message = "Odometer reading cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid odometer reading format")
    private BigDecimal currentOdometer;
    
    @DecimalMin(value = "0.0", message = "Next service odometer cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid next service odometer format")
    private BigDecimal nextServiceOdometer;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(SCHEDULED|IN_PROGRESS|COMPLETED|CANCELLED|OVERDUE)$", 
             message = "Status must be SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, or OVERDUE")
    private String status = "SCHEDULED";
    
    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$", 
             message = "Priority must be LOW, MEDIUM, HIGH, or CRITICAL")
    private String priority = "MEDIUM";
    
    @Size(max = 100, message = "Service provider cannot exceed 100 characters")
    private String serviceProvider;
    
    @Size(max = 200, message = "Service location cannot exceed 200 characters")
    private String serviceLocation;
    
    @DecimalMin(value = "0.0", message = "Labor cost cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid labor cost format")
    private BigDecimal laborCost = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Parts cost cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid parts cost format")
    private BigDecimal partsCost = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Other charges cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid other charges format")
    private BigDecimal otherCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "GST amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid GST amount format")
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @Size(max = 500, message = "Work performed cannot exceed 500 characters")
    private String workPerformed;
    
    @Size(max = 500, message = "Parts replaced cannot exceed 500 characters")
    private String partsReplaced;
    
    @Size(max = 500, message = "Recommendations cannot exceed 500 characters")
    private String recommendations;
    
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
    
    @Size(max = 50, message = "Invoice number cannot exceed 50 characters")
    private String invoiceNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;
    
    @Size(max = 50, message = "Warranty period cannot exceed 50 characters")
    private String warrantyPeriod;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyExpiryDate;
    
    private Boolean isRecurring = false;
    
    @Min(value = 1, message = "Service interval must be at least 1")
    private Integer serviceIntervalDays;
    
    @DecimalMin(value = "0.0", message = "Service interval KM cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid service interval KM format")
    private BigDecimal serviceIntervalKm;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextServiceDate;
    
    // Related entity information (read-only)
    private String truckNumber;
    private String truckModel;
    
    // Calculated fields (read-only)
    private BigDecimal totalCost; // labor + parts + other + gst
    private Integer daysOverdue;
    private Boolean isOverdue;
    private BigDecimal kmSinceLastService;
    private Integer daysSinceLastService;
    
    // Constructors
    public MaintenanceDTO() {}
    
    public MaintenanceDTO(Long truckId, String maintenanceType, String serviceCategory, 
                         String description, LocalDate scheduledDate, BigDecimal currentOdometer) {
        this.truckId = truckId;
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
    
    public Long getTruckId() { return truckId; }
    public void setTruckId(Long truckId) { this.truckId = truckId; }
    
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
    
    // Related entity getters and setters
    public String getTruckNumber() { return truckNumber; }
    public void setTruckNumber(String truckNumber) { this.truckNumber = truckNumber; }
    
    public String getTruckModel() { return truckModel; }
    public void setTruckModel(String truckModel) { this.truckModel = truckModel; }
    
    // Calculated fields getters and setters
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public Integer getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(Integer daysOverdue) { this.daysOverdue = daysOverdue; }
    
    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }
    
    public BigDecimal getKmSinceLastService() { return kmSinceLastService; }
    public void setKmSinceLastService(BigDecimal kmSinceLastService) { this.kmSinceLastService = kmSinceLastService; }
    
    public Integer getDaysSinceLastService() { return daysSinceLastService; }
    public void setDaysSinceLastService(Integer daysSinceLastService) { this.daysSinceLastService = daysSinceLastService; }
    
    // Utility methods
    public boolean isScheduled() { return "SCHEDULED".equals(status); }
    public boolean isInProgress() { return "IN_PROGRESS".equals(status); }
    public boolean isCompleted() { return "COMPLETED".equals(status); }
    public boolean isCancelled() { return "CANCELLED".equals(status); }
    public boolean isOverdue() { return "OVERDUE".equals(status); }
    
    public boolean isHighPriority() { return "HIGH".equals(priority) || "CRITICAL".equals(priority); }
    public boolean isCritical() { return "CRITICAL".equals(priority); }
    
    public boolean hasWarranty() { 
        return warrantyExpiryDate != null && warrantyExpiryDate.isAfter(LocalDate.now()); 
    }
    
    public BigDecimal calculateTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        if (laborCost != null) total = total.add(laborCost);
        if (partsCost != null) total = total.add(partsCost);
        if (otherCharges != null) total = total.add(otherCharges);
        if (gstAmount != null) total = total.add(gstAmount);
        return total;
    }
    
    @Override
    public String toString() {
        return "MaintenanceDTO{" +
                "id=" + id +
                ", maintenanceNumber='" + maintenanceNumber + '\'' +
                ", truckNumber='" + truckNumber + '\'' +
                ", maintenanceType='" + maintenanceType + '\'' +
                ", serviceCategory='" + serviceCategory + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}

