package com.shivshakti.stms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Income entity representing transport income/revenue
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "incomes")
public class Income {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "income_number", unique = true, nullable = false, length = 20)
    private String incomeNumber;
    
    @Column(name = "income_type", nullable = false, length = 30)
    private String incomeType;
    
    @Column(name = "income_category", nullable = false, length = 30)
    private String incomeCategory;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;
    
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "builty_id")
    private Builty builty;
    
    @Column(name = "payer_name", length = 100)
    private String payerName;
    
    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;
    
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    
    @Column(name = "gst_amount", precision = 8, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @Column(name = "tds_amount", precision = 8, scale = 2)
    private BigDecimal tdsAmount = BigDecimal.ZERO;
    
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;
    
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "PENDING";
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "expected_date")
    private LocalDate expectedDate;
    
    @Column(name = "reference_number", length = 50)
    private String referenceNumber;
    
    @Column(name = "bank_name", length = 100)
    private String bankName;
    
    @Column(name = "cheque_number", length = 50)
    private String chequeNumber;
    
    @Column(name = "received_amount", precision = 10, scale = 2)
    private BigDecimal receivedAmount = BigDecimal.ZERO;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
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
    public Income() {
        this.createdDate = LocalDateTime.now();
    }
    
    public Income(String incomeNumber, String incomeType, String incomeCategory, 
                  BigDecimal amount, LocalDate incomeDate, String description) {
        this();
        this.incomeNumber = incomeNumber;
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
    
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public Builty getBuilty() { return builty; }
    public void setBuilty(Builty builty) { this.builty = builty; }
    
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
        return "Income{" +
                "id=" + id +
                ", incomeNumber='" + incomeNumber + '\'' +
                ", incomeType='" + incomeType + '\'' +
                ", incomeCategory='" + incomeCategory + '\'' +
                ", amount=" + amount +
                ", incomeDate=" + incomeDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}

