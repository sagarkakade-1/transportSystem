package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Client entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientDTO {
    
    private Long id;
    
    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 100, message = "Client name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName;
    
    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    
    private String alternateContactNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
    
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", 
             message = "Invalid GST number format")
    private String gstNumber;
    
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", 
             message = "Invalid PAN number format")
    private String panNumber;
    
    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Invalid credit limit format")
    private BigDecimal creditLimit = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Outstanding balance cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Invalid outstanding balance format")
    private BigDecimal outstandingBalance = BigDecimal.ZERO;
    
    private String contactPerson;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact person number must be 10 digits")
    private String contactPersonNumber;
    
    private String paymentTerms;
    
    private String remarks;
    
    private Boolean isActive = true;
    
    // Business metrics (read-only)
    private Integer totalBuilties;
    private BigDecimal totalBusinessValue;
    private BigDecimal totalPaidAmount;
    private BigDecimal creditUtilization;
    private String paymentBehavior; // GOOD, AVERAGE, POOR
    private Integer overdueBuilties;
    
    // Constructors
    public ClientDTO() {}
    
    public ClientDTO(String name, String contactNumber, String address) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
    }
    
    public ClientDTO(String name, String companyName, String contactNumber, 
                     String address, BigDecimal creditLimit) {
        this.name = name;
        this.companyName = companyName;
        this.contactNumber = contactNumber;
        this.address = address;
        this.creditLimit = creditLimit;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }
    
    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
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
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactPersonNumber() {
        return contactPersonNumber;
    }
    
    public void setContactPersonNumber(String contactPersonNumber) {
        this.contactPersonNumber = contactPersonNumber;
    }
    
    public String getPaymentTerms() {
        return paymentTerms;
    }
    
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getTotalBuilties() {
        return totalBuilties;
    }
    
    public void setTotalBuilties(Integer totalBuilties) {
        this.totalBuilties = totalBuilties;
    }
    
    public BigDecimal getTotalBusinessValue() {
        return totalBusinessValue;
    }
    
    public void setTotalBusinessValue(BigDecimal totalBusinessValue) {
        this.totalBusinessValue = totalBusinessValue;
    }
    
    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
    }
    
    public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }
    
    public BigDecimal getCreditUtilization() {
        return creditUtilization;
    }
    
    public void setCreditUtilization(BigDecimal creditUtilization) {
        this.creditUtilization = creditUtilization;
    }
    
    public String getPaymentBehavior() {
        return paymentBehavior;
    }
    
    public void setPaymentBehavior(String paymentBehavior) {
        this.paymentBehavior = paymentBehavior;
    }
    
    public Integer getOverdueBuilties() {
        return overdueBuilties;
    }
    
    public void setOverdueBuilties(Integer overdueBuilties) {
        this.overdueBuilties = overdueBuilties;
    }
    
    // Utility methods
    public boolean hasOutstandingBalance() {
        return outstandingBalance != null && outstandingBalance.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isExceedingCreditLimit() {
        return creditLimit != null && outstandingBalance != null && 
               creditLimit.compareTo(BigDecimal.ZERO) > 0 &&
               outstandingBalance.compareTo(creditLimit) > 0;
    }
    
    public BigDecimal getAvailableCredit() {
        if (creditLimit == null || outstandingBalance == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal available = creditLimit.subtract(outstandingBalance);
        return available.compareTo(BigDecimal.ZERO) > 0 ? available : BigDecimal.ZERO;
    }
    
    public double getCreditUtilizationPercentage() {
        if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) == 0 || outstandingBalance == null) {
            return 0.0;
        }
        return outstandingBalance.divide(creditLimit, 4, BigDecimal.ROUND_HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
    }
    
    public boolean isGoodPaymentBehavior() {
        return "GOOD".equals(paymentBehavior);
    }
    
    public boolean isPoorPaymentBehavior() {
        return "POOR".equals(paymentBehavior);
    }
    
    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", creditLimit=" + creditLimit +
                ", outstandingBalance=" + outstandingBalance +
                ", isActive=" + isActive +
                '}';
    }
}
