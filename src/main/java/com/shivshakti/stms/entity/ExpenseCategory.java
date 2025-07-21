package com.shivshakti.stms.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ExpenseCategory Entity - Represents expense categories for classification
 * Manages standardized expense types like FUEL, TOLL, TYRE, REPAIR, etc.
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "expense_categories", indexes = {
    @Index(name = "idx_expense_category_name", columnList = "category_name"),
    @Index(name = "idx_expense_category_active", columnList = "is_active")
})
public class ExpenseCategory extends BaseEntity {

    public enum CategoryType {
        FUEL("Fuel"),
        TOLL("Toll Tax"),
        TYRE("Tyre"),
        REPAIR("Repair"),
        DRIVER_ALLOWANCE("Driver Allowance"),
        MAINTENANCE("Maintenance"),
        INSURANCE("Insurance"),
        PERMIT("Permit"),
        OTHER("Other");

        private final String displayName;

        CategoryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    @Column(name = "category_name", nullable = false, unique = true, length = 50)
    private String categoryName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> expenses = new ArrayList<>();

    // Constructors
    public ExpenseCategory() {
    }

    public ExpenseCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public ExpenseCategory(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public ExpenseCategory(CategoryType categoryType) {
        this.categoryName = categoryType.name();
        this.description = categoryType.getDisplayName();
    }

    // Business Methods
    public boolean isFuelCategory() {
        return CategoryType.FUEL.name().equals(categoryName);
    }

    public boolean isTollCategory() {
        return CategoryType.TOLL.name().equals(categoryName);
    }

    public boolean isTyreCategory() {
        return CategoryType.TYRE.name().equals(categoryName);
    }

    public boolean isRepairCategory() {
        return CategoryType.REPAIR.name().equals(categoryName);
    }

    public boolean isDriverAllowanceCategory() {
        return CategoryType.DRIVER_ALLOWANCE.name().equals(categoryName);
    }

    public boolean isMaintenanceCategory() {
        return CategoryType.MAINTENANCE.name().equals(categoryName);
    }

    public CategoryType getCategoryType() {
        try {
            return CategoryType.valueOf(categoryName);
        } catch (IllegalArgumentException e) {
            return CategoryType.OTHER;
        }
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        expense.setCategory(this);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setCategory(null);
    }

    // Getters and Setters
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "ExpenseCategory{" +
                "id=" + getId() +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

