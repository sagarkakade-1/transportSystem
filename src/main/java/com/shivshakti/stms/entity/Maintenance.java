package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintenance Entity - Represents vehicle maintenance and service records
 * Manages maintenance tracking with warranty and service scheduling
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "maintenance", indexes = {
    @Index(name = "idx_maintenance_number", columnList = "maintenance_number"),
    @Index(name = "idx_maintenance_truck", columnList = "truck_id"),
    @Index(name = "idx_maintenance_date", columnList = "maintenance_date"),
    @Index(name = "idx_maintenance_type", columnList = "maintenance_type")
})
public class Maintenance extends BaseEntity {

    public enum MaintenanceType {
        SERVICE("Regular Service"),
        REPAIR("Repair"),
        TYRE_CHANGE("Tyre Change"),
        OIL_CHANGE("Oil Change"),
        BRAKE_SERVICE("Brake Service"),
        ENGINE_REPAIR("Engine Repair"),
        TRANSMISSION_REPAIR("Transmission Repair"),
        ELECTRICAL_REPAIR("Electrical Repair"),
        BODY_REPAIR("Body Repair"),
        OTHER("Other Maintenance");

        private final String displayName;

        MaintenanceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Maintenance number is required")
    @Size(max = 50, message = "Maintenance number must not exceed 50 characters")
    @Column(name = "maintenance_number", nullable = false, unique = true, length = 50)
    private String maintenanceNumber;

    @NotNull(message = "Truck is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_maintenance_truck"))
    private Truck truck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_maintenance_shop"))
    private Shop shop;

    @NotNull(message = "Maintenance date is required")
    @Column(name = "maintenance_date", nullable = false)
    private LocalDate maintenanceDate;

    @NotNull(message = "Maintenance type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 50)
    private MaintenanceType maintenanceType;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Total cost is required")
    @DecimalMin(value = "0.01", message = "Total cost must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Total cost must have at most 8 integer digits and 2 decimal places")
    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @DecimalMin(value = "0.0", message = "Labour cost must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Labour cost must have at most 6 integer digits and 2 decimal places")
    @Column(name = "labour_cost", precision = 8, scale = 2)
    private BigDecimal labourCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Parts cost must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Parts cost must have at most 6 integer digits and 2 decimal places")
    @Column(name = "parts_cost", precision = 8, scale = 2)
    private BigDecimal partsCost = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Mileage at maintenance must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Mileage must have at most 8 integer digits and 2 decimal places")
    @Column(name = "mileage_at_maintenance", precision = 10, scale = 2)
    private BigDecimal mileageAtMaintenance;

    @DecimalMin(value = "0.0", message = "Next service mileage must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Next service mileage must have at most 8 integer digits and 2 decimal places")
    @Column(name = "next_service_mileage", precision = 10, scale = 2)
    private BigDecimal nextServiceMileage;

    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;

    @Min(value = 0, message = "Warranty period must be non-negative")
    @Max(value = 120, message = "Warranty period must not exceed 120 months")
    @Column(name = "warranty_period_months")
    private Integer warrantyPeriodMonths = 0;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Relationships
    @OneToMany(mappedBy = "maintenance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TyreDetail> tyreDetails = new ArrayList<>();

    // Constructors
    public Maintenance() {
    }

    public Maintenance(String maintenanceNumber, Truck truck, LocalDate maintenanceDate,
                       MaintenanceType maintenanceType, String description, BigDecimal totalCost) {
        this.maintenanceNumber = maintenanceNumber;
        this.truck = truck;
        this.maintenanceDate = maintenanceDate;
        this.maintenanceType = maintenanceType;
        this.description = description;
        this.totalCost = totalCost;
    }

    // Business Methods
    public boolean isService() {
        return MaintenanceType.SERVICE.equals(maintenanceType);
    }

    public boolean isRepair() {
        return MaintenanceType.REPAIR.equals(maintenanceType);
    }

    public boolean isTyreChange() {
        return MaintenanceType.TYRE_CHANGE.equals(maintenanceType);
    }

    public boolean isOilChange() {
        return MaintenanceType.OIL_CHANGE.equals(maintenanceType);
    }

    public boolean hasWarranty() {
        return warrantyPeriodMonths != null && warrantyPeriodMonths > 0;
    }

    public boolean isWarrantyExpired() {
        return warrantyExpiryDate != null && warrantyExpiryDate.isBefore(LocalDate.now());
    }

    public boolean isWarrantyExpiringSoon(int daysThreshold) {
        return warrantyExpiryDate != null && 
               warrantyExpiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public void calculateWarrantyExpiryDate() {
        if (warrantyPeriodMonths != null && warrantyPeriodMonths > 0 && maintenanceDate != null) {
            this.warrantyExpiryDate = maintenanceDate.plusMonths(warrantyPeriodMonths);
        }
    }

    public boolean isNextServiceDue() {
        return nextServiceDate != null && !nextServiceDate.isAfter(LocalDate.now());
    }

    public boolean isNextServiceDueSoon(int daysThreshold) {
        return nextServiceDate != null && 
               nextServiceDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public boolean isNextServiceMileageDue(BigDecimal currentMileage) {
        return nextServiceMileage != null && currentMileage != null &&
               currentMileage.compareTo(nextServiceMileage) >= 0;
    }

    public void addTyreDetail(TyreDetail tyreDetail) {
        tyreDetails.add(tyreDetail);
        tyreDetail.setMaintenance(this);
    }

    public void removeTyreDetail(TyreDetail tyreDetail) {
        tyreDetails.remove(tyreDetail);
        tyreDetail.setMaintenance(null);
    }

    // Getters and Setters
    public String getMaintenanceNumber() {
        return maintenanceNumber;
    }

    public void setMaintenanceNumber(String maintenanceNumber) {
        this.maintenanceNumber = maintenanceNumber;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
        calculateWarrantyExpiryDate();
    }

    public MaintenanceType getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(MaintenanceType maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getLabourCost() {
        return labourCost;
    }

    public void setLabourCost(BigDecimal labourCost) {
        this.labourCost = labourCost;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public void setPartsCost(BigDecimal partsCost) {
        this.partsCost = partsCost;
    }

    public BigDecimal getMileageAtMaintenance() {
        return mileageAtMaintenance;
    }

    public void setMileageAtMaintenance(BigDecimal mileageAtMaintenance) {
        this.mileageAtMaintenance = mileageAtMaintenance;
    }

    public BigDecimal getNextServiceMileage() {
        return nextServiceMileage;
    }

    public void setNextServiceMileage(BigDecimal nextServiceMileage) {
        this.nextServiceMileage = nextServiceMileage;
    }

    public LocalDate getNextServiceDate() {
        return nextServiceDate;
    }

    public void setNextServiceDate(LocalDate nextServiceDate) {
        this.nextServiceDate = nextServiceDate;
    }

    public Integer getWarrantyPeriodMonths() {
        return warrantyPeriodMonths;
    }

    public void setWarrantyPeriodMonths(Integer warrantyPeriodMonths) {
        this.warrantyPeriodMonths = warrantyPeriodMonths;
        calculateWarrantyExpiryDate();
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<TyreDetail> getTyreDetails() {
        return tyreDetails;
    }

    public void setTyreDetails(List<TyreDetail> tyreDetails) {
        this.tyreDetails = tyreDetails;
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "id=" + getId() +
                ", maintenanceNumber='" + maintenanceNumber + '\'' +
                ", maintenanceType=" + maintenanceType +
                ", maintenanceDate=" + maintenanceDate +
                ", totalCost=" + totalCost +
                ", description='" + description + '\'' +
                '}';
    }
}

