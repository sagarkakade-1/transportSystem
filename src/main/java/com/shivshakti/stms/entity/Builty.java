package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builty Entity - Represents billing/invoice documents in the transport system
 * Manages builty information, charges, and payment tracking
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "builty", indexes = {
    @Index(name = "idx_builty_number", columnList = "builty_number"),
    @Index(name = "idx_builty_trip", columnList = "trip_id"),
    @Index(name = "idx_builty_client", columnList = "client_id"),
    @Index(name = "idx_builty_date", columnList = "builty_date"),
    @Index(name = "idx_builty_payment_status", columnList = "payment_status")
})
public class Builty extends BaseEntity {

    public enum PaymentStatus {
        PENDING("Pending"),
        PARTIAL("Partial"),
        PAID("Paid");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Builty number is required")
    @Size(max = 50, message = "Builty number must not exceed 50 characters")
    @Column(name = "builty_number", nullable = false, unique = true, length = 50)
    private String builtyNumber;

    @NotNull(message = "Trip is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false, foreignKey = @ForeignKey(name = "fk_builty_trip"))
    private Trip trip;

    @NotNull(message = "Client is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_builty_client"))
    private Client client;

    @NotNull(message = "Builty date is required")
    @Column(name = "builty_date", nullable = false)
    private LocalDate builtyDate;

    @NotBlank(message = "Goods type is required")
    @Size(max = 100, message = "Goods type must not exceed 100 characters")
    @Column(name = "goods_type", nullable = false, length = 100)
    private String goodsType;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Weight must have at most 6 integer digits and 2 decimal places")
    @Column(name = "weight_tons", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightTons;

    @DecimalMin(value = "0.0", message = "Rate per ton must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Rate per ton must have at most 6 integer digits and 2 decimal places")
    @Column(name = "rate_per_ton", precision = 8, scale = 2)
    private BigDecimal ratePerTon;

    @NotNull(message = "Total charges is required")
    @DecimalMin(value = "0.01", message = "Total charges must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total charges must have at most 10 integer digits and 2 decimal places")
    @Column(name = "total_charges", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCharges;

    @DecimalMin(value = "0.0", message = "Advance received must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Advance received must have at most 10 integer digits and 2 decimal places")
    @Column(name = "advance_received", precision = 12, scale = 2)
    private BigDecimal advanceReceived = BigDecimal.ZERO;

    @NotNull(message = "Balance amount is required")
    @DecimalMin(value = "0.0", message = "Balance amount must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Balance amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "balance_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceAmount;

    @DecimalMin(value = "0.0", message = "Loading charges must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Loading charges must have at most 6 integer digits and 2 decimal places")
    @Column(name = "loading_charges", precision = 8, scale = 2)
    private BigDecimal loadingCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Unloading charges must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Unloading charges must have at most 6 integer digits and 2 decimal places")
    @Column(name = "unloading_charges", precision = 8, scale = 2)
    private BigDecimal unloadingCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Detention charges must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Detention charges must have at most 6 integer digits and 2 decimal places")
    @Column(name = "detention_charges", precision = 8, scale = 2)
    private BigDecimal detentionCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Other charges must be non-negative")
    @Digits(integer = 6, fraction = 2, message = "Other charges must have at most 6 integer digits and 2 decimal places")
    @Column(name = "other_charges", precision = 8, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Size(max = 1000, message = "Remarks must not exceed 1000 characters")
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    // Relationships
    @OneToMany(mappedBy = "builty", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Income> incomes = new ArrayList<>();

    @OneToMany(mappedBy = "builty", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    // Constructors
    public Builty() {
    }

    public Builty(String builtyNumber, Trip trip, Client client, LocalDate builtyDate, 
                  String goodsType, BigDecimal weightTons, BigDecimal totalCharges) {
        this.builtyNumber = builtyNumber;
        this.trip = trip;
        this.client = client;
        this.builtyDate = builtyDate;
        this.goodsType = goodsType;
        this.weightTons = weightTons;
        this.totalCharges = totalCharges;
        this.balanceAmount = totalCharges;
    }

    // Business Methods
    public boolean isPending() {
        return PaymentStatus.PENDING.equals(paymentStatus);
    }

    public boolean isPartiallyPaid() {
        return PaymentStatus.PARTIAL.equals(paymentStatus);
    }

    public boolean isFullyPaid() {
        return PaymentStatus.PAID.equals(paymentStatus);
    }

    public void calculateBalanceAmount() {
        if (totalCharges != null && advanceReceived != null) {
            this.balanceAmount = totalCharges.subtract(advanceReceived);
            updatePaymentStatus();
        }
    }

    public void updatePaymentStatus() {
        if (balanceAmount == null || balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else if (advanceReceived != null && advanceReceived.compareTo(BigDecimal.ZERO) > 0) {
            this.paymentStatus = PaymentStatus.PARTIAL;
        } else {
            this.paymentStatus = PaymentStatus.PENDING;
        }
    }

    public BigDecimal getTotalAdditionalCharges() {
        BigDecimal total = BigDecimal.ZERO;
        if (loadingCharges != null) total = total.add(loadingCharges);
        if (unloadingCharges != null) total = total.add(unloadingCharges);
        if (detentionCharges != null) total = total.add(detentionCharges);
        if (otherCharges != null) total = total.add(otherCharges);
        return total;
    }

    public BigDecimal getGrandTotal() {
        return totalCharges.add(getTotalAdditionalCharges());
    }

    public void receivePayment(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.advanceReceived = this.advanceReceived.add(amount);
            calculateBalanceAmount();
        }
    }

    public void addIncome(Income income) {
        incomes.add(income);
        income.setBuilty(this);
    }

    public void removeIncome(Income income) {
        incomes.remove(income);
        income.setBuilty(null);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setBuilty(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setBuilty(null);
    }

    // Getters and Setters
    public String getBuiltyNumber() {
        return builtyNumber;
    }

    public void setBuiltyNumber(String builtyNumber) {
        this.builtyNumber = builtyNumber;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getBuiltyDate() {
        return builtyDate;
    }

    public void setBuiltyDate(LocalDate builtyDate) {
        this.builtyDate = builtyDate;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public BigDecimal getWeightTons() {
        return weightTons;
    }

    public void setWeightTons(BigDecimal weightTons) {
        this.weightTons = weightTons;
    }

    public BigDecimal getRatePerTon() {
        return ratePerTon;
    }

    public void setRatePerTon(BigDecimal ratePerTon) {
        this.ratePerTon = ratePerTon;
    }

    public BigDecimal getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
        calculateBalanceAmount();
    }

    public BigDecimal getAdvanceReceived() {
        return advanceReceived;
    }

    public void setAdvanceReceived(BigDecimal advanceReceived) {
        this.advanceReceived = advanceReceived;
        calculateBalanceAmount();
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public BigDecimal getLoadingCharges() {
        return loadingCharges;
    }

    public void setLoadingCharges(BigDecimal loadingCharges) {
        this.loadingCharges = loadingCharges;
    }

    public BigDecimal getUnloadingCharges() {
        return unloadingCharges;
    }

    public void setUnloadingCharges(BigDecimal unloadingCharges) {
        this.unloadingCharges = unloadingCharges;
    }

    public BigDecimal getDetentionCharges() {
        return detentionCharges;
    }

    public void setDetentionCharges(BigDecimal detentionCharges) {
        this.detentionCharges = detentionCharges;
    }

    public BigDecimal getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "Builty{" +
                "id=" + getId() +
                ", builtyNumber='" + builtyNumber + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", weightTons=" + weightTons +
                ", totalCharges=" + totalCharges +
                ", balanceAmount=" + balanceAmount +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}

