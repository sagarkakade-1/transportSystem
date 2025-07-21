package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Client Entity - Represents clients/customers in the transport system
 * Manages client information, credit limits, and outstanding balances
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "clients", indexes = {
    @Index(name = "idx_client_name", columnList = "name"),
    @Index(name = "idx_client_company", columnList = "company_name"),
    @Index(name = "idx_client_contact", columnList = "contact_number"),
    @Index(name = "idx_client_active", columnList = "is_active")
})
public class Client extends BaseEntity {

    @NotBlank(message = "Client name is required")
    @Size(max = 100, message = "Client name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 150, message = "Company name must not exceed 150 characters")
    @Column(name = "company_name", length = 150)
    private String companyName;

    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", 
             message = "GST number format is invalid")
    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", 
             message = "PAN number format is invalid")
    @Column(name = "pan_number", length = 15)
    private String panNumber;

    @DecimalMin(value = "0.0", message = "Credit limit must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Credit limit must have at most 10 integer digits and 2 decimal places")
    @Column(name = "credit_limit", precision = 12, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Outstanding balance must be non-negative")
    @Digits(integer = 10, fraction = 2, message = "Outstanding balance must have at most 10 integer digits and 2 decimal places")
    @Column(name = "outstanding_balance", precision = 12, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Builty> builties = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Income> incomes = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    // Constructors
    public Client() {
    }

    public Client(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public Client(String name, String companyName, String contactNumber, String email) {
        this.name = name;
        this.companyName = companyName;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Business Methods
    public boolean isCreditLimitExceeded() {
        return outstandingBalance != null && creditLimit != null && 
               outstandingBalance.compareTo(creditLimit) > 0;
    }

    public BigDecimal getAvailableCredit() {
        if (creditLimit == null || outstandingBalance == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal available = creditLimit.subtract(outstandingBalance);
        return available.compareTo(BigDecimal.ZERO) > 0 ? available : BigDecimal.ZERO;
    }

    public boolean hasOutstandingBalance() {
        return outstandingBalance != null && outstandingBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    public void addBuilty(Builty builty) {
        builties.add(builty);
        builty.setClient(this);
    }

    public void removeBuilty(Builty builty) {
        builties.remove(builty);
        builty.setClient(null);
    }

    public void addIncome(Income income) {
        incomes.add(income);
        income.setClient(this);
    }

    public void removeIncome(Income income) {
        incomes.remove(income);
        income.setClient(null);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setClient(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setClient(null);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Builty> getBuilties() {
        return builties;
    }

    public void setBuilties(List<Builty> builties) {
        this.builties = builties;
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
        return "Client{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", outstandingBalance=" + outstandingBalance +
                ", isActive=" + isActive +
                '}';
    }
}

