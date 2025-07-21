package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Income Entity - Represents all revenue sources in the transport system
 * Manages income tracking from various sources like trip payments, advances, etc.
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "income", indexes = {
    @Index(name = "idx_income_number", columnList = "income_number"),
    @Index(name = "idx_income_client", columnList = "client_id"),
    @Index(name = "idx_income_builty", columnList = "builty_id"),
    @Index(name = "idx_income_trip", columnList = "trip_id"),
    @Index(name = "idx_income_date", columnList = "income_date"),
    @Index(name = "idx_income_type", columnList = "income_type")
})
public class Income extends BaseEntity {

    public enum IncomeType {
        TRIP_PAYMENT("Trip Payment"),
        ADVANCE("Advance Payment"),
        DETENTION("Detention Charges"),
        LOADING_UNLOADING("Loading/Unloading Charges"),
        OTHER("Other Income");

        private final String displayName;

        IncomeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentMode {
        CASH("Cash"),
        CHEQUE("Cheque"),
        BANK_TRANSFER("Bank Transfer"),
        UPI("UPI"),
        NEFT("NEFT"),
        RTGS("RTGS");

        private final String displayName;

        PaymentMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Income number is required")
    @Size(max = 50, message = "Income number must not exceed 50 characters")
    @Column(name = "income_number", nullable = false, unique = true, length = 50)
    private String incomeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "builty_id", foreignKey = @ForeignKey(name = "fk_income_builty"))
    private Builty builty;

    @NotNull(message = "Client is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_income_client"))
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", foreignKey = @ForeignKey(name = "fk_income_trip"))
    private Trip trip;

    @NotNull(message = "Income date is required")
    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Income type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "income_type", nullable = false, length = 50)
    private IncomeType incomeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", length = 20)
    private PaymentMode paymentMode = PaymentMode.CASH;

    @Size(max = 50, message = "Cheque number must not exceed 50 characters")
    @Column(name = "cheque_number", length = 50)
    private String chequeNumber;

    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Size(max = 100, message = "Transaction reference must not exceed 100 characters")
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Constructors
    public Income() {
    }

    public Income(String incomeNumber, Client client, LocalDate incomeDate, 
                  BigDecimal amount, IncomeType incomeType) {
        this.incomeNumber = incomeNumber;
        this.client = client;
        this.incomeDate = incomeDate;
        this.amount = amount;
        this.incomeType = incomeType;
    }

    public Income(String incomeNumber, Builty builty, Client client, Trip trip,
                  LocalDate incomeDate, BigDecimal amount, IncomeType incomeType) {
        this.incomeNumber = incomeNumber;
        this.builty = builty;
        this.client = client;
        this.trip = trip;
        this.incomeDate = incomeDate;
        this.amount = amount;
        this.incomeType = incomeType;
    }

    // Business Methods
    public boolean isTripPayment() {
        return IncomeType.TRIP_PAYMENT.equals(incomeType);
    }

    public boolean isAdvancePayment() {
        return IncomeType.ADVANCE.equals(incomeType);
    }

    public boolean isDetentionCharges() {
        return IncomeType.DETENTION.equals(incomeType);
    }

    public boolean isCashPayment() {
        return PaymentMode.CASH.equals(paymentMode);
    }

    public boolean isChequePayment() {
        return PaymentMode.CHEQUE.equals(paymentMode);
    }

    public boolean isBankTransfer() {
        return PaymentMode.BANK_TRANSFER.equals(paymentMode) ||
               PaymentMode.NEFT.equals(paymentMode) ||
               PaymentMode.RTGS.equals(paymentMode) ||
               PaymentMode.UPI.equals(paymentMode);
    }

    public boolean requiresBankDetails() {
        return isBankTransfer() || isChequePayment();
    }

    public boolean requiresChequeDetails() {
        return isChequePayment();
    }

    // Getters and Setters
    public String getIncomeNumber() {
        return incomeNumber;
    }

    public void setIncomeNumber(String incomeNumber) {
        this.incomeNumber = incomeNumber;
    }

    public Builty getBuilty() {
        return builty;
    }

    public void setBuilty(Builty builty) {
        this.builty = builty;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(LocalDate incomeDate) {
        this.incomeDate = incomeDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public IncomeType getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(IncomeType incomeType) {
        this.incomeType = incomeType;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + getId() +
                ", incomeNumber='" + incomeNumber + '\'' +
                ", amount=" + amount +
                ", incomeDate=" + incomeDate +
                ", incomeType=" + incomeType +
                ", paymentMode=" + paymentMode +
                '}';
    }
}

