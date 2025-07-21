package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Expense Entity - Represents all operational expenses in the transport system
 * Manages categorized expense tracking with approval workflow
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "expenses", indexes = {
    @Index(name = "idx_expense_number", columnList = "expense_number"),
    @Index(name = "idx_expense_trip", columnList = "trip_id"),
    @Index(name = "idx_expense_truck", columnList = "truck_id"),
    @Index(name = "idx_expense_category", columnList = "category_id"),
    @Index(name = "idx_expense_date", columnList = "expense_date"),
    @Index(name = "idx_expense_approved", columnList = "is_approved")
})
public class Expense extends BaseEntity {

    @NotBlank(message = "Expense number is required")
    @Size(max = 50, message = "Expense number must not exceed 50 characters")
    @Column(name = "expense_number", nullable = false, unique = true, length = 50)
    private String expenseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", foreignKey = @ForeignKey(name = "fk_expense_trip"))
    private Trip trip;

    @NotNull(message = "Truck is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id", nullable = false, foreignKey = @ForeignKey(name = "fk_expense_truck"))
    private Truck truck;

    @NotNull(message = "Expense category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_expense_category"))
    private ExpenseCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_expense_shop"))
    private Shop shop;

    @NotNull(message = "Expense date is required")
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount must have at most 8 integer digits and 2 decimal places")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @DecimalMin(value = "0.0", message = "GST amount must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "GST amount must have at most 6 integer digits and 2 decimal places")
    @Column(name = "gst_amount", precision = 8, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "GST percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "GST percentage must not exceed 100")
    @Digits(integer = 3, fraction = 2, message = "GST percentage must have at most 3 integer digits and 2 decimal places")
    @Column(name = "gst_percentage", precision = 5, scale = 2)
    private BigDecimal gstPercentage = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Quantity must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Quantity must have at most 6 integer digits and 2 decimal places")
    @Column(name = "quantity", precision = 8, scale = 2)
    private BigDecimal quantity;

    @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Unit price must have at most 6 integer digits and 2 decimal places")
    @Column(name = "unit_price", precision = 8, scale = 2)
    private BigDecimal unitPrice;

    @Size(max = 20, message = "Unit must not exceed 20 characters")
    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Size(max = 100, message = "Approved by must not exceed 100 characters")
    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Constructors
    public Expense() {
    }

    public Expense(String expenseNumber, Truck truck, ExpenseCategory category, 
                   LocalDate expenseDate, BigDecimal amount, String description) {
        this.expenseNumber = expenseNumber;
        this.truck = truck;
        this.category = category;
        this.expenseDate = expenseDate;
        this.amount = amount;
        this.description = description;
    }

    // Business Methods
    public boolean isApproved() {
        return Boolean.TRUE.equals(isApproved);
    }

    public void approve(String approvedBy) {
        this.isApproved = true;
        this.approvedBy = approvedBy;
        this.approvedDate = LocalDateTime.now();
    }

    public void reject() {
        this.isApproved = false;
        this.approvedBy = null;
        this.approvedDate = null;
    }

    public BigDecimal getTotalAmountWithGst() {
        if (gstAmount != null) {
            return amount.add(gstAmount);
        }
        return amount;
    }

    public void calculateGstAmount() {
        if (gstPercentage != null && gstPercentage.compareTo(BigDecimal.ZERO) > 0) {
            this.gstAmount = amount.multiply(gstPercentage)
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    public boolean isFuelExpense() {
        return category != null && category.isFuelCategory();
    }

    public boolean isTollExpense() {
        return category != null && category.isTollCategory();
    }

    public boolean isTyreExpense() {
        return category != null && category.isTyreCategory();
    }

    public boolean isRepairExpense() {
        return category != null && category.isRepairCategory();
    }

    public boolean isDriverAllowanceExpense() {
        return category != null && category.isDriverAllowanceCategory();
    }

    public boolean isMaintenanceExpense() {
        return category != null && category.isMaintenanceCategory();
    }

    // Getters and Setters
    public String getExpenseNumber() {
        return expenseNumber;
    }

    public void setExpenseNumber(String expenseNumber) {
        this.expenseNumber = expenseNumber;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        calculateGstAmount();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDateTime approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + getId() +
                ", expenseNumber='" + expenseNumber + '\'' +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate +
                ", description='" + description + '\'' +
                ", isApproved=" + isApproved +
                '}';
    }
}

