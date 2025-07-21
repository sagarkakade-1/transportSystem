package com.shivshakti.stms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Expense entity representing transport expenses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "expense_number", unique = true, nullable = false, length = 20)
    private String expenseNumber;
    
    @Column(name = "expense_type", nullable = false, length = 30)
    private String expenseType;
    
    @Column(name = "expense_category", nullable = false, length = 30)
    private String expenseCategory;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;
    
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private Truck truck;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    
    @Column(name = "vendor_name", length = 100)
    private String vendorName;
    
    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;
    
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    
    @Column(name = "gst_amount", precision = 8, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", precision = 8, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;
    
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "PENDING";
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "reference_number", length = 50)
    private String referenceNumber;
    
    @Column(name = "approval_status", nullable = false, length = 20)
    private String approvalStatus = "PENDING";
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approval_date")
    private LocalDate approvalDate;
    
    @Column(name = "approval_remarks", length = 200)
    private String approvalRemarks;
    
    @Column(name = "reimbursement_status", nullable = false, length = 20)
    private String reimbursementStatus = "NOT_APPLICABLE";
    
    @Column(name = "reimbursement_amount", precision = 10, scale = 2)
    private BigDecimal reimbursementAmount = BigDecimal.ZERO;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "receipt_path", length = 200)
    private String receiptPath;
    
    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;
    
    @Column(name = "recurring_frequency", length = 20)
    private String recurringFrequency;
    
    @Column(name = "next_recurring_date")
    private LocalDate nextRecurringDate;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    // Constructors
    public Expense() {
        this.createdDate = LocalDateTime.now();
    }
    
    public Expense(String expenseNumber, String expenseType, String expenseCategory, 
                   BigDecimal amount, LocalDate expenseDate, String description) {
        this();
        this.expenseNumber = expenseNumber;
        this.expenseType = expenseType;
        this.expenseCategory = expenseCategory;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getExpenseNumber() { return expenseNumber; }
    public void setExpenseNumber(String expenseNumber) { this.expenseNumber = expenseNumber; }
    
    public String getExpenseType() { return expenseType; }
    public void setExpenseType(String expenseType) { this.expenseType = expenseType; }
    
    public String getExpenseCategory() { return expenseCategory; }
    public void setExpenseCategory(String expenseCategory) { this.expenseCategory = expenseCategory; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    
    public Truck getTruck() { return truck; }
    public void setTruck(Truck truck) { this.truck = truck; }
    
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public BigDecimal getGstAmount() { return gstAmount; }
    public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    
    public String getApprovalRemarks() { return approvalRemarks; }
    public void setApprovalRemarks(String approvalRemarks) { this.approvalRemarks = approvalRemarks; }
    
    public String getReimbursementStatus() { return reimbursementStatus; }
    public void setReimbursementStatus(String reimbursementStatus) { this.reimbursementStatus = reimbursementStatus; }
    
    public BigDecimal getReimbursementAmount() { return reimbursementAmount; }
    public void setReimbursementAmount(BigDecimal reimbursementAmount) { this.reimbursementAmount = reimbursementAmount; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public String getReceiptPath() { return receiptPath; }
    public void setReceiptPath(String receiptPath) { this.receiptPath = receiptPath; }
    
    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }
    
    public String getRecurringFrequency() { return recurringFrequency; }
    public void setRecurringFrequency(String recurringFrequency) { this.recurringFrequency = recurringFrequency; }
    
    public LocalDate getNextRecurringDate() { return nextRecurringDate; }
    public void setNextRecurringDate(LocalDate nextRecurringDate) { this.nextRecurringDate = nextRecurringDate; }
    
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
        return "Expense{" +
                "id=" + id +
                ", expenseNumber='" + expenseNumber + '\'' +
                ", expenseType='" + expenseType + '\'' +
                ", expenseCategory='" + expenseCategory + '\'' +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                '}';
    }
}

