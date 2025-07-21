package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TyreDetail Entity - Represents detailed tyre tracking with warranty information
 * Manages tyre purchase, installation, and warranty tracking
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "tyre_details", indexes = {
    @Index(name = "idx_tyre_maintenance", columnList = "maintenance_id"),
    @Index(name = "idx_tyre_truck", columnList = "truck_id"),
    @Index(name = "idx_tyre_company", columnList = "tyre_company"),
    @Index(name = "idx_tyre_purchase_date", columnList = "purchase_date"),
    @Index(name = "idx_tyre_active", columnList = "is_active")
})
public class TyreDetail extends BaseEntity {

    public enum TyrePosition {
        FRONT_LEFT("Front Left"),
        FRONT_RIGHT("Front Right"),
        REAR_LEFT_OUTER("Rear Left Outer"),
        REAR_LEFT_INNER("Rear Left Inner"),
        REAR_RIGHT_OUTER("Rear Right Outer"),
        REAR_RIGHT_INNER("Rear Right Inner"),
        SPARE("Spare Tyre");

        private final String displayName;

        TyrePosition(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotNull(message = "Maintenance record is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tyre_maintenance"))
    private Maintenance maintenance;

    @NotNull(message = "Truck is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tyre_truck"))
    private Truck truck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_tyre_shop"))
    private Shop shop;

    @NotBlank(message = "Tyre company is required")
    @Size(max = 50, message = "Tyre company must not exceed 50 characters")
    @Column(name = "tyre_company", nullable = false, length = 50)
    private String tyreCompany;

    @Size(max = 50, message = "Tyre model must not exceed 50 characters")
    @Column(name = "tyre_model", length = 50)
    private String tyreModel;

    @NotBlank(message = "Tyre size is required")
    @Size(max = 20, message = "Tyre size must not exceed 20 characters")
    @Column(name = "tyre_size", nullable = false, length = 20)
    private String tyreSize;

    @NotNull(message = "Tyre position is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "tyre_position", nullable = false, length = 20)
    private TyrePosition tyrePosition;

    @NotNull(message = "Purchase date is required")
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.01", message = "Purchase price must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Purchase price must have at most 6 integer digits and 2 decimal places")
    @Column(name = "purchase_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal purchasePrice;

    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Min(value = 0, message = "Warranty months must be non-negative")
    @Max(value = 60, message = "Warranty months must not exceed 60")
    @Column(name = "warranty_months")
    private Integer warrantyMonths = 0;

    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    @Min(value = 0, message = "Warranty KM must be non-negative")
    @Column(name = "warranty_km")
    private Integer warrantyKm = 0;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @DecimalMin(value = "0.0", message = "Mileage at installation must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Mileage must have at most 8 integer digits and 2 decimal places")
    @Column(name = "mileage_at_installation", precision = 10, scale = 2)
    private BigDecimal mileageAtInstallation;

    @DecimalMin(value = "0.0", message = "GST amount must be non-negative")
    @Digits(integer = 4, fraction = 2, message = "GST amount must have at most 4 integer digits and 2 decimal places")
    @Column(name = "gst_amount", precision = 6, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "GST percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "GST percentage must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "GST percentage must have at most 3 integer digits and 2 decimal places")
    @Column(name = "gst_percentage", precision = 5, scale = 2)
    private BigDecimal gstPercentage = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public TyreDetail() {
    }

    public TyreDetail(Maintenance maintenance, Truck truck, String tyreCompany, String tyreSize,
                      TyrePosition tyrePosition, LocalDate purchaseDate, BigDecimal purchasePrice) {
        this.maintenance = maintenance;
        this.truck = truck;
        this.tyreCompany = tyreCompany;
        this.tyreSize = tyreSize;
        this.tyrePosition = tyrePosition;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
    }

    // Business Methods
    public boolean hasWarranty() {
        return (warrantyMonths != null && warrantyMonths > 0) || 
               (warrantyKm != null && warrantyKm > 0);
    }

    public boolean isWarrantyExpired() {
        return warrantyExpiryDate != null && warrantyExpiryDate.isBefore(LocalDate.now());
    }

    public boolean isWarrantyExpiringSoon(int daysThreshold) {
        return warrantyExpiryDate != null && 
               warrantyExpiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public void calculateWarrantyExpiryDate() {
        if (warrantyMonths != null && warrantyMonths > 0 && purchaseDate != null) {
            this.warrantyExpiryDate = purchaseDate.plusMonths(warrantyMonths);
        }
    }

    public boolean isWarrantyKmExceeded(BigDecimal currentMileage) {
        if (warrantyKm == null || warrantyKm <= 0 || 
            mileageAtInstallation == null || currentMileage == null) {
            return false;
        }
        
        BigDecimal kmTravelled = currentMileage.subtract(mileageAtInstallation);
        return kmTravelled.compareTo(new BigDecimal(warrantyKm)) > 0;
    }

    public BigDecimal getTotalPriceWithGst() {
        if (gstAmount != null) {
            return purchasePrice.add(gstAmount);
        }
        return purchasePrice;
    }

    public void calculateGstAmount() {
        if (gstPercentage != null && gstPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.gstAmount = purchasePrice.multiply(gstPercentage)
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    public boolean isFrontTyre() {
        return TyrePosition.FRONT_LEFT.equals(tyrePosition) || 
               TyrePosition.FRONT_RIGHT.equals(tyrePosition);
    }

    public boolean isRearTyre() {
        return TyrePosition.REAR_LEFT_OUTER.equals(tyrePosition) ||
               TyrePosition.REAR_LEFT_INNER.equals(tyrePosition) ||
               TyrePosition.REAR_RIGHT_OUTER.equals(tyrePosition) ||
               TyrePosition.REAR_RIGHT_INNER.equals(tyrePosition);
    }

    public boolean isSpareTyre() {
        return TyrePosition.SPARE.equals(tyrePosition);
    }

    // Getters and Setters
    public Maintenance getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
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

    public String getTyreCompany() {
        return tyreCompany;
    }

    public void setTyreCompany(String tyreCompany) {
        this.tyreCompany = tyreCompany;
    }

    public String getTyreModel() {
        return tyreModel;
    }

    public void setTyreModel(String tyreModel) {
        this.tyreModel = tyreModel;
    }

    public String getTyreSize() {
        return tyreSize;
    }

    public void setTyreSize(String tyreSize) {
        this.tyreSize = tyreSize;
    }

    public TyrePosition getTyrePosition() {
        return tyrePosition;
    }

    public void setTyrePosition(TyrePosition tyrePosition) {
        this.tyrePosition = tyrePosition;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
        calculateWarrantyExpiryDate();
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
        calculateGstAmount();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        calculateWarrantyExpiryDate();
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    public Integer getWarrantyKm() {
        return warrantyKm;
    }

    public void setWarrantyKm(Integer warrantyKm) {
        this.warrantyKm = warrantyKm;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public BigDecimal getMileageAtInstallation() {
        return mileageAtInstallation;
    }

    public void setMileageAtInstallation(BigDecimal mileageAtInstallation) {
        this.mileageAtInstallation = mileageAtInstallation;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
        calculateGstAmount();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "TyreDetail{" +
                "id=" + getId() +
                ", tyreCompany='" + tyreCompany + '\'' +
                ", tyreSize='" + tyreSize + '\'' +
                ", tyrePosition=" + tyrePosition +
                ", purchaseDate=" + purchaseDate +
                ", purchasePrice=" + purchasePrice +
                ", isActive=" + isActive +
                '}';
    }
}

