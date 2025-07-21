package com.shivshakti.stms.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Builty entity representing transport billing documents
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "builties")
public class Builty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "builty_number", unique = true, nullable = false, length = 20)
    private String builtyNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column(name = "consignor_name", nullable = false, length = 100)
    private String consignorName;
    
    @Column(name = "consignor_address", nullable = false, length = 200)
    private String consignorAddress;
    
    @Column(name = "consignor_phone", length = 15)
    private String consignorPhone;
    
    @Column(name = "consignee_name", nullable = false, length = 100)
    private String consigneeName;
    
    @Column(name = "consignee_address", nullable = false, length = 200)
    private String consigneeAddress;
    
    @Column(name = "consignee_phone", length = 15)
    private String consigneePhone;
    
    @Column(name = "goods_description", nullable = false, length = 200)
    private String goodsDescription;
    
    @Column(name = "goods_weight", precision = 8, scale = 2, nullable = false)
    private BigDecimal goodsWeight;
    
    @Column(name = "goods_value", precision = 12, scale = 2)
    private BigDecimal goodsValue;
    
    @Column(name = "number_of_packages", nullable = false)
    private Integer numberOfPackages;
    
    @Column(name = "package_type", length = 50)
    private String packageType;
    
    @Column(name = "freight_charges", precision = 10, scale = 2, nullable = false)
    private BigDecimal freightCharges;
    
    @Column(name = "loading_charges", precision = 8, scale = 2)
    private BigDecimal loadingCharges = BigDecimal.ZERO;
    
    @Column(name = "unloading_charges", precision = 8, scale = 2)
    private BigDecimal unloadingCharges = BigDecimal.ZERO;
    
    @Column(name = "other_charges", precision = 8, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;
    
    @Column(name = "gst_amount", precision = 8, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @Column(name = "advance_amount", precision = 10, scale = 2)
    private BigDecimal advanceAmount = BigDecimal.ZERO;
    
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "PENDING";
    
    @Column(name = "delivery_status", nullable = false, length = 20)
    private String deliveryStatus = "PENDING";
    
    @Column(name = "builty_date", nullable = false)
    private LocalDate builtyDate;
    
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
    
    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;
    
    @Column(name = "remarks", length = 500)
    private String remarks;
    
    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    // Constructors
    public Builty() {
        this.createdDate = LocalDateTime.now();
        this.builtyDate = LocalDate.now();
    }
    
    public Builty(String builtyNumber, Trip trip, Client client, String consignorName, 
                  String consigneeName, String goodsDescription, BigDecimal goodsWeight, 
                  BigDecimal freightCharges) {
        this();
        this.builtyNumber = builtyNumber;
        this.trip = trip;
        this.client = client;
        this.consignorName = consignorName;
        this.consigneeName = consigneeName;
        this.goodsDescription = goodsDescription;
        this.goodsWeight = goodsWeight;
        this.freightCharges = freightCharges;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBuiltyNumber() { return builtyNumber; }
    public void setBuiltyNumber(String builtyNumber) { this.builtyNumber = builtyNumber; }
    
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    
    public String getConsignorName() { return consignorName; }
    public void setConsignorName(String consignorName) { this.consignorName = consignorName; }
    
    public String getConsignorAddress() { return consignorAddress; }
    public void setConsignorAddress(String consignorAddress) { this.consignorAddress = consignorAddress; }
    
    public String getConsignorPhone() { return consignorPhone; }
    public void setConsignorPhone(String consignorPhone) { this.consignorPhone = consignorPhone; }
    
    public String getConsigneeName() { return consigneeName; }
    public void setConsigneeName(String consigneeName) { this.consigneeName = consigneeName; }
    
    public String getConsigneeAddress() { return consigneeAddress; }
    public void setConsigneeAddress(String consigneeAddress) { this.consigneeAddress = consigneeAddress; }
    
    public String getConsigneePhone() { return consigneePhone; }
    public void setConsigneePhone(String consigneePhone) { this.consigneePhone = consigneePhone; }
    
    public String getGoodsDescription() { return goodsDescription; }
    public void setGoodsDescription(String goodsDescription) { this.goodsDescription = goodsDescription; }
    
    public BigDecimal getGoodsWeight() { return goodsWeight; }
    public void setGoodsWeight(BigDecimal goodsWeight) { this.goodsWeight = goodsWeight; }
    
    public BigDecimal getGoodsValue() { return goodsValue; }
    public void setGoodsValue(BigDecimal goodsValue) { this.goodsValue = goodsValue; }
    
    public Integer getNumberOfPackages() { return numberOfPackages; }
    public void setNumberOfPackages(Integer numberOfPackages) { this.numberOfPackages = numberOfPackages; }
    
    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }
    
    public BigDecimal getFreightCharges() { return freightCharges; }
    public void setFreightCharges(BigDecimal freightCharges) { this.freightCharges = freightCharges; }
    
    public BigDecimal getLoadingCharges() { return loadingCharges; }
    public void setLoadingCharges(BigDecimal loadingCharges) { this.loadingCharges = loadingCharges; }
    
    public BigDecimal getUnloadingCharges() { return unloadingCharges; }
    public void setUnloadingCharges(BigDecimal unloadingCharges) { this.unloadingCharges = unloadingCharges; }
    
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    
    public BigDecimal getGstAmount() { return gstAmount; }
    public void setGstAmount(BigDecimal gstAmount) { this.gstAmount = gstAmount; }
    
    public BigDecimal getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(BigDecimal advanceAmount) { this.advanceAmount = advanceAmount; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    
    public LocalDate getBuiltyDate() { return builtyDate; }
    public void setBuiltyDate(LocalDate builtyDate) { this.builtyDate = builtyDate; }
    
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }
    
    public LocalDate getPaymentDueDate() { return paymentDueDate; }
    public void setPaymentDueDate(LocalDate paymentDueDate) { this.paymentDueDate = paymentDueDate; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    
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
        return "Builty{" +
                "id=" + id +
                ", builtyNumber='" + builtyNumber + '\'' +
                ", consignorName='" + consignorName + '\'' +
                ", consigneeName='" + consigneeName + '\'' +
                ", goodsDescription='" + goodsDescription + '\'' +
                ", freightCharges=" + freightCharges +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                '}';
    }
}

