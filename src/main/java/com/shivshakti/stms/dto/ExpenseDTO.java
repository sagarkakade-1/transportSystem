package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Expense entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpenseDTO {
    
    private Long id;
    
    @NotBlank(message = "Expense number is required")
    @Size(min = 5, max = 20, message = "Expense number must be between 5 and 20 characters")
    private String expenseNumber;
    
    @NotBlank(message = "Expense type is required")
    @Pattern(regexp = "^(FUEL|TOLL|MAINTENANCE|TYRE|REPAIR|DRIVER_ALLOWANCE|OFFICE|INSURANCE|OTHER)$", 
             message = "Invalid expense type")
    private String expenseType;
    
    @NotBlank(message = "Expense category is required")
    @Pattern(regexp = "^(OPERATIONAL|ADMINISTRATIVE|MAINTENANCE|FUEL|DRIVER_RELATED|VEHICLE_RELATED|OTHER)$", 
             message = "Invalid expense category")
    private String expenseCategory;
    
    @NotNull(message = "Expense amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Expense amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid expense amount format")
    private BigDecimal amount;
    
    @NotNull(message = "Expense date is required")
    @PastOrPresent(message = "Expense date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;
    
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;
    
    private Long tripId;
    private Long truckId;
    private Long driverId;
    
    @Size(max = 100, message = "Vendor name cannot exceed 100 characters")
    private String vendorName;
    
    @Size(max = 50, message = "Invoice number cannot exceed 50 characters")
    private String invoiceNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;
    
    @DecimalMin(value = "0.0", message = "GST amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid GST amount format")
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid discount amount format")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(CASH|CHEQUE|BANK_TRANSFER|UPI|CARD|OTHER)$", 
             message = "Invalid payment method")
    private String paymentMethod;
    
    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "^(PENDING|PAID|PARTIALLY_PAID|OVERDUE)$", 
             message = "Payment status must be PENDING, PAID, PARTIALLY_PAID, or OVERDUE")
    private String paymentStatus = "PENDING";
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;
    
    @Size(max = 50, message = "Reference number cannot exceed 50 characters")
    private String referenceNumber;
    
    @NotBlank(message = "Approval status is required")
    @Pattern(regexp = "^(PENDING|APPROVED|REJECTED|UNDER_REVIEW)$", 
             message = "Approval status must be PENDING, APPROVED, REJECTED, or UNDER_REVIEW")
    private String approvalStatus = "PENDING";
    
    private Long approvedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate approvalDate;
    
    @Size(max = 200, message = "Approval remarks cannot exceed 200 characters")
    private String approvalRemarks;
    
    @NotBlank(message = "Reimbursement status is required")
    @Pattern(regexp = "^(NOT_APPLICABLE|PENDING|PROCESSED|REJECTED)$", 
             message = "Invalid reimbursement status")
    private String reimbursementStatus = "NOT_APPLICABLE";
    
    @DecimalMin(value = "0.0", message = "Reimbursement amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid reimbursement amount format")
    private BigDecimal reimbursementAmount = BigDecimal.ZERO;
    
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
    
    @Size(max = 200, message = "Receipt path cannot exceed 200 characters")
    private String receiptPath;
    
    private Boolean isRecurring = false;
    
    @Pattern(regexp = "^(MONTHLY|QUARTERLY|YEARLY|CUSTOM)$", 
             message = "Invalid recurring frequency")
    private String recurringFrequency;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextRecurringDate;
    
    // Related entity information (read-only)
    private String tripNumber;
    private String truckNumber;
    private String driverName;
    private String approverName;
    
    // Calculated fields (read-only)
    private BigDecimal totalAmount; // amount + gst - discount
    private BigDecimal netAmount; // total - reimbursement
    private Integer daysOverdue;
    private Boolean isOverdue;
    
    // Constructors
    public ExpenseDTO() {}
    
    public ExpenseDTO(String expenseType, String expenseCategory, BigDecimal amount, 
                      LocalDate expenseDate, String description) {
        this.expenseType = expenseType;
        this.expenseCategory = expenseCategory;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getExpenseNumber() {
        return expenseNumber;
    }
    
    public void setExpenseNumber(String expenseNumber) {
        this.expenseNumber = expenseNumber;
    }
    
    public String getExpenseType() {
        return expenseType;
    }
    
    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }
    
    public String getExpenseCategory() {
        return expenseCategory;
    }
    
    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getExpenseDate() {
        return expenseDate;
    }
    
    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getTripId() {
        return tripId;
    }
    
    public void setTripId(Long tripId) {
        this.tripId = tripId;
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
    
    public String getVendorName() {
        return vendorName;
    }
    
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
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
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public Long getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDate getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public String getApprovalRemarks() {
        return approvalRemarks;
    }
    
    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
    }
    
    public String getReimbursementStatus() {
        return reimbursementStatus;
    }
    
    public void setReimbursementStatus(String reimbursementStatus) {
        this.reimbursementStatus = reimbursementStatus;
    }
    
    public BigDecimal getReimbursementAmount() {
        return reimbursementAmount;
    }
    
    public void setReimbursementAmount(BigDecimal reimbursementAmount) {
        this.reimbursementAmount = reimbursementAmount;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public String getReceiptPath() {
        return receiptPath;
    }
    
    public void setReceiptPath(String receiptPath) {
        this.receiptPath = receiptPath;
    }
    
    public Boolean getIsRecurring() {
        return isRecurring;
    }
    
    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }
    
    public String getRecurringFrequency() {
        return recurringFrequency;
    }
    
    public void setRecurringFrequency(String recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }
    
    public LocalDate getNextRecurringDate() {
        return nextRecurringDate;
    }
    
    public void setNextRecurringDate(LocalDate nextRecurringDate) {
        this.nextRecurringDate = nextRecurringDate;
    }
    
    // Related entity getters and setters
    public String getTripNumber() {
        return tripNumber;
    }
    
    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }
    
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
    
    public String getApproverName() {
        return approverName;
    }
    
    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
    
    // Calculated fields getters and setters
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
    
    public Integer getDaysOverdue() {
        return daysOverdue;
    }
    
    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }
    
    public Boolean getIsOverdue() {
        return isOverdue;
    }
    
    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }
    
    // Utility methods
    public boolean isPending() {
        return "PENDING".equals(paymentStatus);
    }
    
    public boolean isPaid() {
        return "PAID".equals(paymentStatus);
    }
    
    public boolean isPartiallyPaid() {
        return "PARTIALLY_PAID".equals(paymentStatus);
    }
    
    public boolean isOverdue() {
        return "OVERDUE".equals(paymentStatus);
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(approvalStatus);
    }
    
    public boolean isRejected() {
        return "REJECTED".equals(approvalStatus);
    }
    
    public boolean isPendingApproval() {
        return "PENDING".equals(approvalStatus);
    }
    
    public boolean isUnderReview() {
        return "UNDER_REVIEW".equals(approvalStatus);
    }
    
    public boolean requiresReimbursement() {
        return !"NOT_APPLICABLE".equals(reimbursementStatus);
    }
    
    public boolean isReimbursementPending() {
        return "PENDING".equals(reimbursementStatus);
    }
    
    public BigDecimal calculateTotalAmount() {
        BigDecimal total = amount != null ? amount : BigDecimal.ZERO;
        if (gstAmount != null) total = total.add(gstAmount);
        if (discountAmount != null) total = total.subtract(discountAmount);
        return total;
    }
    
    public BigDecimal calculateNetAmount() {
        BigDecimal total = calculateTotalAmount();
        if (reimbursementAmount != null) total = total.subtract(reimbursementAmount);
        return total;
    }
    
    @Override
    public String toString() {
        return "ExpenseDTO{" +
                "id=" + id +
                ", expenseNumber='" + expenseNumber + '\'' +
                ", expenseType='" + expenseType + '\'' +
                ", expenseCategory='" + expenseCategory + '\'' +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate +
                ", description='" + description + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                '}';
    }
}
