package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment Entity - Represents detailed payment tracking and reconciliation
 * Manages payment information with bank/cheque details and status tracking
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_number", columnList = "payment_number"),
    @Index(name = "idx_payment_client", columnList = "client_id"),
    @Index(name = "idx_payment_builty", columnList = "builty_id"),
    @Index(name = "idx_payment_date", columnList = "payment_date"),
    @Index(name = "idx_payment_status", columnList = "status")
})
public class Payment extends BaseEntity {

    public enum PaymentType {
        ADVANCE("Advance Payment"),
        PARTIAL("Partial Payment"),
        FULL("Full Payment");

        private final String displayName;

        PaymentType(String displayName) {
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

    public enum PaymentStatus {
        RECEIVED("Received"),
        CLEARED("Cleared"),
        BOUNCED("Bounced"),
        PENDING("Pending");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Payment number is required")
    @Size(max = 50, message = "Payment number must not exceed 50 characters")
    @Column(name = "payment_number", nullable = false, unique = true, length = 50)
    private String paymentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "builty_id", foreignKey = @ForeignKey(name = "fk_payment_builty"))
    private Builty builty;

    @NotNull(message = "Client is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_client"))
    private Client client;

    @NotNull(message = "Payment date is required")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Payment type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", length = 20)
    private PaymentMode paymentMode = PaymentMode.CASH;

    @Size(max = 50, message = "Cheque number must not exceed 50 characters")
    @Column(name = "cheque_number", length = 50)
    private String chequeNumber;

    @Column(name = "cheque_date")
    private LocalDate chequeDate;

    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    @Column(name = "branch_name", length = 100)
    private String branchName;

    @Size(max = 100, message = "Transaction reference must not exceed 100 characters")
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PaymentStatus status = PaymentStatus.RECEIVED;

    @Column(name = "cleared_date")
    private LocalDate clearedDate;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Constructors
    public Payment() {
    }

    public Payment(String paymentNumber, Client client, LocalDate paymentDate, 
                   BigDecimal amount, PaymentType paymentType) {
        this.paymentNumber = paymentNumber;
        this.client = client;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public Payment(String paymentNumber, Builty builty, Client client, LocalDate paymentDate,
                   BigDecimal amount, PaymentType paymentType, PaymentMode paymentMode) {
        this.paymentNumber = paymentNumber;
        this.builty = builty;
        this.client = client;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentMode = paymentMode;
    }

    // Business Methods
    public boolean isAdvancePayment() {
        return PaymentType.ADVANCE.equals(paymentType);
    }

    public boolean isPartialPayment() {
        return PaymentType.PARTIAL.equals(paymentType);
    }

    public boolean isFullPayment() {
        return PaymentType.FULL.equals(paymentType);
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

    public boolean isReceived() {
        return PaymentStatus.RECEIVED.equals(status);
    }

    public boolean isCleared() {
        return PaymentStatus.CLEARED.equals(status);
    }

    public boolean isBounced() {
        return PaymentStatus.BOUNCED.equals(status);
    }

    public boolean isPending() {
        return PaymentStatus.PENDING.equals(status);
    }

    public void markAsCleared() {
        this.status = PaymentStatus.CLEARED;
        this.clearedDate = LocalDate.now();
    }

    public void markAsBounced() {
        this.status = PaymentStatus.BOUNCED;
        this.clearedDate = null;
    }

    public void markAsPending() {
        this.status = PaymentStatus.PENDING;
        this.clearedDate = null;
    }

    public boolean requiresBankDetails() {
        return isBankTransfer() || isChequePayment();
    }

    public boolean requiresChequeDetails() {
        return isChequePayment();
    }

    public boolean isChequeExpired() {
        if (chequeDate == null) {
            return false;
        }
        // Cheques are typically valid for 3 months
        return chequeDate.plusMonths(3).isBefore(LocalDate.now());
    }

    // Getters and Setters
    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
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

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
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

    public LocalDate getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(LocalDate chequeDate) {
        this.chequeDate = chequeDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDate getClearedDate() {
        return clearedDate;
    }

    public void setClearedDate(LocalDate clearedDate) {
        this.clearedDate = clearedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + getId() +
                ", paymentNumber='" + paymentNumber + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentType=" + paymentType +
                ", paymentMode=" + paymentMode +
                ", status=" + status +
                '}';
    }
}

