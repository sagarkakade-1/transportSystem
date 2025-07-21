package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Income entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncomeDTO {
    
    private Long id;
    
    @NotBlank(message = "Income number is required")
    @Size(min = 5, max = 20, message = "Income number must be between 5 and 20 characters")
    private String incomeNumber;
    
    @NotBlank(message = "Income type is required")
    @Pattern(regexp = "^(FREIGHT|LOADING|UNLOADING|DETENTION|ADVANCE_REFUND|INSURANCE_CLAIM|OTHER)$", 
             message = "Invalid income type")
    private String incomeType;
    
    @NotBlank(message = "Income category is required")
    @Pattern(regexp = "^(OPERATIONAL|FREIGHT_CHARGES|ADDITIONAL_SERVICES|CLAIMS|REFUNDS|OTHER)$", 
             message = "Invalid income category")
    private String incomeCategory;
    
    @NotNull(message = "Income amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Income amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid income amount format")
    private BigDecimal amount;
    
    @NotNull(message = "Income date is required")
    @PastOrPresent(message = "Income date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate incomeDate;
    
    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    private String description;
    
    private Long tripId;
    private Long clientId;
    private Long builtyId;
    
    @Size(max = 100, message = "Payer name cannot exceed 100 characters")
    private String payerName;
    
    @Size(max = 50, message = "Invoice number cannot exceed 50 characters")
    private String invoiceNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;
    
    @DecimalMin(value = "0.0", message = "GST amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid GST amount format")
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "TDS amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid TDS amount format")
    private BigDecimal tdsAmount = BigDecimal.ZERO;
    
    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(CASH|CHEQUE|BANK_TRANSFER|UPI|CARD|OTHER)$", 
             message = "Invalid payment method")
    private String paymentMethod;
    
    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "^(PENDING|RECEIVED|PARTIALLY_RECEIVED|OVERDUE)$", 
             message = "Payment status must be PENDING, RECEIVED, PARTIALLY_RECEIVED, or OVERDUE")
    private String paymentStatus = "PENDING";
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDate;
    
    @Size(max = 50, message = "Reference number cannot exceed 50 characters")
    private String referenceNumber;
    
    @Size(max = 100, message = "Bank name cannot exceed 100 characters")
    private String bankName;
    
    @Size(max = 50, message = "Cheque number cannot exceed 50 characters")
    private String chequeNumber;
    
    @DecimalMin(value = "0.0", message = "Received amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid received amount format")
    private BigDecimal receivedAmount = BigDecimal.ZERO;
    
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
    
    private Boolean isRecurring = false;
    
    @Pattern(regexp = "^(MONTHLY|QUARTERLY|YEARLY|CUSTOM)$", 
             message = "Invalid recurring frequency")
    private String recurringFrequency;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextRecurringDate;
    
    // Related entity information (read-only)
    private String tripNumber;
    private String clientName;
    private String builtyNumber;
    
    // Calculated fields (read-only)
    private BigDecimal totalAmount; // amount + gst
    private BigDecimal netAmount; // total - tds
    private BigDecimal balanceAmount; // net - received
    private Integer daysOverdue;
    private Boolean isOverdue;
    
    // Constructors
    public IncomeDTO() {}
    
    public IncomeDTO(String incomeType, String incomeCategory, BigDecimal amount, 
                     LocalDate incomeDate, String description) {
        this.incomeType = incomeType;
        this.incomeCategory = incomeCategory;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIncomeNumber() { return incomeNumber; }
    public void setIncomeNumber(String incomeNumber) { this.incomeNumber = incomeNumber; }
    
    public String getIncomeType() { return incomeType; }
    public void setIncomeType(String incomeType) { this.incomeType = incomeType; }
    
    public String getIncomeCategory() { return incomeCategory; }
    public void setIncomeCategory(String incomeCategory) { this.incomeCategory = incomeCategory; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getIncomeDate() { return incomeDate; }
    public void setIncomeDate(LocalDate incomeDate) { this.incomeDate = incomeDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    
    public Long getBuiltyId() { return builtyId; }
    public void setBuiltyId(Long builtyId) { this.builtyId = builtyId; }
    
    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }
    
    public BigDecimal getGstAmount() { return gstAmount; }
    public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
    
    public BigDecimal getTdsAmount() { return tdsAmount; }
    public void setTdsAmount(BigDecimal tdsAmount) { this.tdsAmount = tdsAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public LocalDate getExpectedDate() { return expectedDate; }
    public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }
    
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getChequeNumber() { return chequeNumber; }
    public void setChequeNumber(String chequeNumber) { this.chequeNumber = chequeNumber; }
    
    public BigDecimal getReceivedAmount() { return receivedAmount; }
    public void setReceivedAmount(BigDecimal receivedAmount) { this.receivedAmount = receivedAmount; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }
    
    public String getRecurringFrequency() { return recurringFrequency; }
    public void setRecurringFrequency(String recurringFrequency) { this.recurringFrequency = recurringFrequency; }
    
    public LocalDate getNextRecurringDate() { return nextRecurringDate; }
    public void setNextRecurringDate(LocalDate nextRecurringDate) { this.nextRecurringDate = nextRecurringDate; }
    
    // Related entity getters and setters
    public String getTripNumber() { return tripNumber; }
    public void setTripNumber(String tripNumber) { this.tripNumber = tripNumber; }
    
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    
    public String getBuiltyNumber() { return builtyNumber; }
    public void setBuiltyNumber(String builtyNumber) { this.builtyNumber = builtyNumber; }
    
    // Calculated fields getters and setters
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
    
    public Integer getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(Integer daysOverdue) { this.daysOverdue = daysOverdue; }
    
    public Boolean getIsOverdue() { return isOverdue; }
    public void setIsOverdue(Boolean isOverdue) { this.isOverdue = isOverdue; }
    
    // Utility methods
    public boolean isPending() { return "PENDING".equals(paymentStatus); }
    public boolean isReceived() { return "RECEIVED".equals(paymentStatus); }
    public boolean isPartiallyReceived() { return "PARTIALLY_RECEIVED".equals(paymentStatus); }
    public boolean isOverdue() { return "OVERDUE".equals(paymentStatus); }
    public boolean hasOutstandingBalance() { return balanceAmount != null && balanceAmount.compareTo(BigDecimal.ZERO) > 0; }
    
    public BigDecimal calculateTotalAmount() {
        BigDecimal total = amount != null ? amount : BigDecimal.ZERO;
        if (gstAmount != null) total = total.add(gstAmount);
        return total;
    }
    
    public BigDecimal calculateNetAmount() {
        BigDecimal total = calculateTotalAmount();
        if (tdsAmount != null) total = total.subtract(tdsAmount);
        return total;
    }
    
    public BigDecimal calculateBalanceAmount() {
        BigDecimal net = calculateNetAmount();
        BigDecimal received = receivedAmount != null ? receivedAmount : BigDecimal.ZERO;
        return net.subtract(received);
    }
    
    @Override
    public String toString() {
        return "IncomeDTO{" +
                "id=" + id +
                ", incomeNumber='" + incomeNumber + '\'' +
                ", incomeType='" + incomeType + '\'' +
                ", incomeCategory='" + incomeCategory + '\'' +
                ", amount=" + amount +
                ", incomeDate=" + incomeDate +
                ", description='" + description + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}

