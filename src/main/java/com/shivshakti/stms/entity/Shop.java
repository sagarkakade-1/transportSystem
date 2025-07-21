package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Shop Entity - Represents service providers (fuel, repair, tyre shops)
 * Manages shop information for maintenance and expense tracking
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "shops", indexes = {
    @Index(name = "idx_shop_name", columnList = "name"),
    @Index(name = "idx_shop_type", columnList = "shop_type"),
    @Index(name = "idx_shop_active", columnList = "is_active")
})
public class Shop extends BaseEntity {

    public enum ShopType {
        FUEL("Fuel Station"),
        REPAIR("Repair Shop"),
        TYRE("Tyre Shop"),
        GENERAL("General Service");

        private final String displayName;

        ShopType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Shop name is required")
    @Size(max = 100, message = "Shop name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Shop type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "shop_type", nullable = false, length = 50)
    private ShopType shopType;

    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", 
             message = "GST number format is invalid")
    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Maintenance> maintenanceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TyreDetail> tyreDetails = new ArrayList<>();

    // Constructors
    public Shop() {
    }

    public Shop(String name, ShopType shopType) {
        this.name = name;
        this.shopType = shopType;
    }

    public Shop(String name, ShopType shopType, String contactPerson, String contactNumber) {
        this.name = name;
        this.shopType = shopType;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
    }

    // Business Methods
    public boolean isFuelStation() {
        return ShopType.FUEL.equals(shopType);
    }

    public boolean isRepairShop() {
        return ShopType.REPAIR.equals(shopType);
    }

    public boolean isTyreShop() {
        return ShopType.TYRE.equals(shopType);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setShop(this);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setShop(null);
    }

    public void addMaintenanceRecord(Maintenance maintenance) {
        maintenanceRecords.add(maintenance);
        maintenance.setShop(this);
    }

    public void removeMaintenanceRecord(Maintenance maintenance) {
        maintenanceRecords.remove(maintenance);
        maintenance.setShop(null);
    }

    public void addTyreDetail(TyreDetail tyreDetail) {
        tyreDetails.add(tyreDetail);
        tyreDetail.setShop(this);
    }

    public void removeTyreDetail(TyreDetail tyreDetail) {
        tyreDetails.remove(tyreDetail);
        tyreDetail.setShop(null);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public void setShopType(ShopType shopType) {
        this.shopType = shopType;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Maintenance> getMaintenanceRecords() {
        return maintenanceRecords;
    }

    public void setMaintenanceRecords(List<Maintenance> maintenanceRecords) {
        this.maintenanceRecords = maintenanceRecords;
    }

    public List<TyreDetail> getTyreDetails() {
        return tyreDetails;
    }

    public void setTyreDetails(List<TyreDetail> tyreDetails) {
        this.tyreDetails = tyreDetails;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", shopType=" + shopType +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

