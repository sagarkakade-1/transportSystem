package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Builty entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuiltyDTO {
    
    private Long id;
    
    @NotBlank(message = "Builty number is required")
    @Size(min = 5, max = 20, message = "Builty number must be between 5 and 20 characters")
    private String builtyNumber;
    
    @NotNull(message = "Trip ID is required")
    private Long tripId;
    
    @NotNull(message = "Client ID is required")
    private Long clientId;
    
    @NotBlank(message = "Consignor name is required")
    @Size(min = 2, max = 100, message = "Consignor name must be between 2 and 100 characters")
    private String consignorName;
    
    @NotBlank(message = "Consignor address is required")
    @Size(min = 5, max = 200, message = "Consignor address must be between 5 and 200 characters")
    private String consignorAddress;
    
    @Size(max = 15, message = "Consignor phone cannot exceed 15 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Invalid phone number format")
    private String consignorPhone;
    
    @NotBlank(message = "Consignee name is required")
    @Size(min = 2, max = 100, message = "Consignee name must be between 2 and 100 characters")
    private String consigneeName;
    
    @NotBlank(message = "Consignee address is required")
    @Size(min = 5, max = 200, message = "Consignee address must be between 5 and 200 characters")
    private String consigneeAddress;
    
    @Size(max = 15, message = "Consignee phone cannot exceed 15 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Invalid phone number format")
    private String consigneePhone;
    
    @NotBlank(message = "Goods description is required")
    @Size(min = 2, max = 200, message = "Goods description must be between 2 and 200 characters")
    private String goodsDescription;
    
    @NotNull(message = "Goods weight is required")
    @DecimalMin(value = "0.1", message = "Goods weight must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Invalid goods weight format")
    private BigDecimal goodsWeight; // in tons
    
    @DecimalMin(value = "0.0", message = "Goods value cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid goods value format")
    private BigDecimal goodsValue;
    
    @NotNull(message = "Number of packages is required")
    @Min(value = 1, message = "Number of packages must be at least 1")
    private Integer numberOfPackages;
    
    @Size(max = 50, message = "Package type cannot exceed 50 characters")
    private String packageType;
    
    @NotNull(message = "Freight charges are required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Freight charges must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid freight charges format")
    private BigDecimal freightCharges;
    
    @DecimalMin(value = "0.0", message = "Loading charges cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid loading charges format")
    private BigDecimal loadingCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Unloading charges cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid unloading charges format")
    private BigDecimal unloadingCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Other charges cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid other charges format")
    private BigDecimal otherCharges = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "GST amount cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid GST amount format")
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.0", message = "Advance amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid advance amount format")
    private BigDecimal advanceAmount = BigDecimal.ZERO;
    
    @NotBlank(message = "Payment status is required")
    @Pattern(regexp = "^(PENDING|PARTIAL|PAID|OVERDUE)$", 
             message = "Payment status must be PENDING, PARTIAL, PAID, or OVERDUE")
    private String paymentStatus = "PENDING";
    
    @NotBlank(message = "Delivery status is required")
    @Pattern(regexp = "^(PENDING|IN_TRANSIT|DELIVERED|RETURNED)$", 
             message = "Delivery status must be PENDING, IN_TRANSIT, DELIVERED, or RETURNED")
    private String deliveryStatus = "PENDING";
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate builtyDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDueDate;
    
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
    
    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    private String specialInstructions;
    
    // Related entity information (read-only)
    private String tripNumber;
    private String clientName;
    private String truckNumber;
    private String driverName;
    
    // Calculated fields (read-only)
    private BigDecimal totalCharges;
    private BigDecimal balanceAmount;
    private BigDecimal totalAmount; // including GST
    private Integer daysOverdue;
    private Boolean isOverdue;
    
    // Constructors
    public BuiltyDTO() {}
    
    public BuiltyDTO(String builtyNumber, Long tripId, Long clientId, String consignorName,
                     String consigneeName, String goodsDescription, BigDecimal goodsWeight,
                     BigDecimal freightCharges) {
        this.builtyNumber = builtyNumber;
        this.tripId = tripId;
        this.clientId = clientId;
        this.consignorName = consignorName;
        this.consigneeName = consigneeName;
        this.goodsDescription = goodsDescription;
        this.goodsWeight = goodsWeight;
        this.freightCharges = freightCharges;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBuiltyNumber() {
        return builtyNumber;
    }
    
    public void setBuiltyNumber(String builtyNumber) {
        this.builtyNumber = builtyNumber;
    }
    
    public Long getTripId() {
        return tripId;
    }
    
    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getConsignorName() {
        return consignorName;
    }
    
    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }
    
    public String getConsignorAddress() {
        return consignorAddress;
    }
    
    public void setConsignorAddress(String consignorAddress) {
        this.consignorAddress = consignorAddress;
    }
    
    public String getConsignorPhone() {
        return consignorPhone;
    }
    
    public void setConsignorPhone(String consignorPhone) {
        this.consignorPhone = consignorPhone;
    }
    
    public String getConsigneeName() {
        return consigneeName;
    }
    
    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }
    
    public String getConsigneeAddress() {
        return consigneeAddress;
    }
    
    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }
    
    public String getConsigneePhone() {
        return consigneePhone;
    }
    
    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }
    
    public String getGoodsDescription() {
        return goodsDescription;
    }
    
    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }
    
    public BigDecimal getGoodsWeight() {
        return goodsWeight;
    }
    
    public void setGoodsWeight(BigDecimal goodsWeight) {
        this.goodsWeight = goodsWeight;
    }
    
    public BigDecimal getGoodsValue() {
        return goodsValue;
    }
    
    public void setGoodsValue(BigDecimal goodsValue) {
        this.goodsValue = goodsValue;
    }
    
    public Integer getNumberOfPackages() {
        return numberOfPackages;
    }
    
    public void setNumberOfPackages(Integer numberOfPackages) {
        this.numberOfPackages = numberOfPackages;
    }
    
    public String getPackageType() {
        return packageType;
    }
    
    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }
    
    public BigDecimal getFreightCharges() {
        return freightCharges;
    }
    
    public void setFreightCharges(BigDecimal freightCharges) {
        this.freightCharges = freightCharges;
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
    
    public BigDecimal getOtherCharges() {
        return otherCharges;
    }
    
    public void setOtherCharges(BigDecimal otherCharges) {
        this.otherCharges = otherCharges;
    }
    
    public BigDecimal getGstAmount() {
        return gstAmount;
    }
    
    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }
    
    public BigDecimal getAdvanceAmount() {
        return advanceAmount;
    }
    
    public void setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
    
    public LocalDate getBuiltyDate() {
        return builtyDate;
    }
    
    public void setBuiltyDate(LocalDate builtyDate) {
        this.builtyDate = builtyDate;
    }
    
    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public LocalDate getPaymentDueDate() {
        return paymentDueDate;
    }
    
    public void setPaymentDueDate(LocalDate paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    // Related entity getters and setters
    public String getTripNumber() {
        return tripNumber;
    }
    
    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
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
    
    // Calculated fields getters and setters
    public BigDecimal getTotalCharges() {
        return totalCharges;
    }
    
    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }
    
    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }
    
    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
    
    public boolean isPartiallyPaid() {
        return "PARTIAL".equals(paymentStatus);
    }
    
    public boolean isPaid() {
        return "PAID".equals(paymentStatus);
    }
    
    public boolean isOverdue() {
        return "OVERDUE".equals(paymentStatus);
    }
    
    public boolean isDelivered() {
        return "DELIVERED".equals(deliveryStatus);
    }
    
    public boolean isInTransit() {
        return "IN_TRANSIT".equals(deliveryStatus);
    }
    
    public boolean hasOutstandingBalance() {
        return balanceAmount != null && balanceAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public BigDecimal calculateTotalCharges() {
        BigDecimal total = freightCharges != null ? freightCharges : BigDecimal.ZERO;
        if (loadingCharges != null) total = total.add(loadingCharges);
        if (unloadingCharges != null) total = total.add(unloadingCharges);
        if (otherCharges != null) total = total.add(otherCharges);
        return total;
    }
    
    public BigDecimal calculateTotalAmount() {
        BigDecimal charges = calculateTotalCharges();
        if (gstAmount != null) charges = charges.add(gstAmount);
        return charges;
    }
    
    public BigDecimal calculateBalanceAmount() {
        BigDecimal total = calculateTotalAmount();
        BigDecimal advance = advanceAmount != null ? advanceAmount : BigDecimal.ZERO;
        return total.subtract(advance);
    }
    
    @Override
    public String toString() {
        return "BuiltyDTO{" +
                "id=" + id +
                ", builtyNumber='" + builtyNumber + '\'' +
                ", tripNumber='" + tripNumber + '\'' +
                ", clientName='" + clientName + '\'' +
                ", consignorName='" + consignorName + '\'' +
                ", consigneeName='" + consigneeName + '\'' +
                ", goodsDescription='" + goodsDescription + '\'' +
                ", goodsWeight=" + goodsWeight +
                ", freightCharges=" + freightCharges +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                '}';
    }
}
