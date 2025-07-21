package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExpenseCategory entity
 * Provides CRUD operations and custom queries for expense category management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find category by name
     */
    Optional<ExpenseCategory> findByCategoryName(String categoryName);
    
    /**
     * Find category by name (case insensitive)
     */
    Optional<ExpenseCategory> findByCategoryNameIgnoreCase(String categoryName);
    
    /**
     * Find all active categories
     */
    List<ExpenseCategory> findByIsActiveTrue();
    
    /**
     * Find all inactive categories
     */
    List<ExpenseCategory> findByIsActiveFalse();
    
    /**
     * Find categories by name containing (case insensitive)
     */
    List<ExpenseCategory> findByCategoryNameContainingIgnoreCase(String categoryName);
    
    /**
     * Find categories by description containing (case insensitive)
     */
    List<ExpenseCategory> findByDescriptionContainingIgnoreCase(String description);

    // ===============================================
    // CATEGORY TYPE QUERIES
    // ===============================================
    
    /**
     * Find fuel category
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.categoryName = 'FUEL' AND ec.isActive = true")
    Optional<ExpenseCategory> findFuelCategory();
    
    /**
     * Find toll category
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.categoryName = 'TOLL' AND ec.isActive = true")
    Optional<ExpenseCategory> findTollCategory();
    
    /**
     * Find tyre category
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.categoryName = 'TYRE' AND ec.isActive = true")
    Optional<ExpenseCategory> findTyreCategory();
    
    /**
     * Find repair category
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.categoryName = 'REPAIR' AND ec.isActive = true")
    Optional<ExpenseCategory> findRepairCategory();
    
    /**
     * Find driver allowance category
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.categoryName = 'DRIVER_ALLOWANCE' AND ec.isActive = true")
    Optional<ExpenseCategory> findDriverAllowanceCategory();
    
    /**
     * Find maintenance category
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.categoryName = 'MAINTENANCE' AND ec.isActive = true")
    Optional<ExpenseCategory> findMaintenanceCategory();

    // ===============================================
    // EXPENSE ANALYSIS QUERIES
    // ===============================================
    
    /**
     * Find categories with expenses
     */
    @Query("SELECT DISTINCT ec FROM ExpenseCategory ec JOIN ec.expenses e WHERE ec.isActive = true")
    List<ExpenseCategory> findCategoriesWithExpenses();
    
    /**
     * Get category usage statistics
     */
    @Query("SELECT ec, COUNT(e) as expenseCount, COALESCE(SUM(e.amount), 0) as totalAmount " +
           "FROM ExpenseCategory ec LEFT JOIN ec.expenses e " +
           "WHERE ec.isActive = true " +
           "GROUP BY ec " +
           "ORDER BY expenseCount DESC, totalAmount DESC")
    List<Object[]> getCategoryUsageStatistics();
    
    /**
     * Find most used categories
     */
    @Query("SELECT ec, COUNT(e) as expenseCount " +
           "FROM ExpenseCategory ec JOIN ec.expenses e " +
           "WHERE ec.isActive = true " +
           "GROUP BY ec " +
           "ORDER BY expenseCount DESC")
    Page<Object[]> findMostUsedCategories(Pageable pageable);
    
    /**
     * Find categories with highest expense amounts
     */
    @Query("SELECT ec, COALESCE(SUM(e.amount), 0) as totalAmount " +
           "FROM ExpenseCategory ec JOIN ec.expenses e " +
           "WHERE ec.isActive = true " +
           "GROUP BY ec " +
           "ORDER BY totalAmount DESC")
    Page<Object[]> findCategoriesWithHighestExpenses(Pageable pageable);

    // ===============================================
    // PERIOD-BASED ANALYSIS
    // ===============================================
    
    /**
     * Get category expense summary for date range
     */
    @Query("SELECT ec, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM ExpenseCategory ec LEFT JOIN ec.expenses e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY ec " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryExpenseSummaryForPeriod(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Get monthly category trends
     */
    @Query("SELECT ec.categoryName, " +
           "EXTRACT(YEAR FROM e.expenseDate) as year, " +
           "EXTRACT(MONTH FROM e.expenseDate) as month, " +
           "COUNT(e) as expenseCount, " +
           "COALESCE(SUM(e.amount), 0) as totalAmount " +
           "FROM ExpenseCategory ec JOIN ec.expenses e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY ec.categoryName, EXTRACT(YEAR FROM e.expenseDate), EXTRACT(MONTH FROM e.expenseDate) " +
           "ORDER BY ec.categoryName, year DESC, month DESC")
    List<Object[]> getMonthlyCategoryTrends(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total expenses by category for period
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM ExpenseCategory ec JOIN ec.expenses e " +
           "WHERE ec.id = :categoryId AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpensesByCategoryForPeriod(@Param("categoryId") Long categoryId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // ===============================================
    // TRUCK-WISE CATEGORY ANALYSIS
    // ===============================================
    
    /**
     * Get category usage by truck
     */
    @Query("SELECT ec, e.truck, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM ExpenseCategory ec JOIN ec.expenses e " +
           "WHERE e.truck.id = :truckId " +
           "GROUP BY ec, e.truck " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryUsageByTruck(@Param("truckId") Long truckId);
    
    /**
     * Find categories with expenses for specific truck
     */
    @Query("SELECT DISTINCT ec FROM ExpenseCategory ec JOIN ec.expenses e " +
           "WHERE e.truck.id = :truckId AND ec.isActive = true")
    List<ExpenseCategory> findCategoriesWithExpensesForTruck(@Param("truckId") Long truckId);

    // ===============================================
    // APPROVAL ANALYSIS
    // ===============================================
    
    /**
     * Get category-wise approval statistics
     */
    @Query("SELECT ec, " +
           "COUNT(e) as totalExpenses, " +
           "COUNT(CASE WHEN e.isApproved = true THEN 1 END) as approvedExpenses, " +
           "COUNT(CASE WHEN e.isApproved = false THEN 1 END) as pendingExpenses " +
           "FROM ExpenseCategory ec LEFT JOIN ec.expenses e " +
           "WHERE ec.isActive = true " +
           "GROUP BY ec " +
           "ORDER BY totalExpenses DESC")
    List<Object[]> getCategoryApprovalStatistics();
    
    /**
     * Find categories with pending approvals
     */
    @Query("SELECT DISTINCT ec FROM ExpenseCategory ec JOIN ec.expenses e " +
           "WHERE e.isApproved = false AND ec.isActive = true")
    List<ExpenseCategory> findCategoriesWithPendingApprovals();

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search categories by multiple criteria
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE " +
           "(:categoryName IS NULL OR LOWER(ec.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%'))) AND " +
           "(:description IS NULL OR LOWER(ec.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:isActive IS NULL OR ec.isActive = :isActive)")
    Page<ExpenseCategory> searchCategories(@Param("categoryName") String categoryName,
                                          @Param("description") String description,
                                          @Param("isActive") Boolean isActive,
                                          Pageable pageable);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get category statistics
     */
    @Query("SELECT " +
           "COUNT(ec) as totalCategories, " +
           "COUNT(CASE WHEN ec.isActive = true THEN 1 END) as activeCategories, " +
           "COUNT(CASE WHEN ec.isActive = false THEN 1 END) as inactiveCategories " +
           "FROM ExpenseCategory ec")
    Object[] getCategoryStatistics();
    
    /**
     * Get category expense distribution
     */
    @Query("SELECT ec.categoryName, " +
           "COUNT(e) as expenseCount, " +
           "COALESCE(SUM(e.amount), 0) as totalAmount, " +
           "COALESCE(AVG(e.amount), 0) as averageAmount, " +
           "COALESCE(MIN(e.amount), 0) as minAmount, " +
           "COALESCE(MAX(e.amount), 0) as maxAmount " +
           "FROM ExpenseCategory ec LEFT JOIN ec.expenses e " +
           "WHERE ec.isActive = true " +
           "GROUP BY ec.categoryName " +
           "ORDER BY totalAmount DESC")
    List<Object[]> getCategoryExpenseDistribution();
    
    /**
     * Get unused categories
     */
    @Query("SELECT ec FROM ExpenseCategory ec WHERE ec.isActive = true AND " +
           "NOT EXISTS (SELECT 1 FROM ec.expenses)")
    List<ExpenseCategory> getUnusedCategories();
    
    /**
     * Get category performance ranking
     */
    @Query("SELECT ec, " +
           "COUNT(e) as transactionCount, " +
           "COALESCE(SUM(e.amount), 0) as totalAmount, " +
           "COALESCE(AVG(e.amount), 0) as avgTransaction " +
           "FROM ExpenseCategory ec LEFT JOIN ec.expenses e " +
           "WHERE ec.isActive = true " +
           "GROUP BY ec " +
           "ORDER BY totalAmount DESC, transactionCount DESC")
    List<Object[]> getCategoryPerformanceRanking();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if category name exists for different category
     */
    boolean existsByCategoryNameIgnoreCaseAndIdNot(String categoryName, Long id);
    
    /**
     * Count active categories
     */
    long countByIsActiveTrue();
    
    /**
     * Count categories with expenses
     */
    @Query("SELECT COUNT(DISTINCT ec) FROM ExpenseCategory ec WHERE ec.isActive = true AND " +
           "EXISTS (SELECT 1 FROM ec.expenses)")
    long countCategoriesWithExpenses();
    
    /**
     * Count expenses for category
     */
    @Query("SELECT COUNT(e) FROM ExpenseCategory ec JOIN ec.expenses e WHERE ec.id = :categoryId")
    long countExpensesForCategory(@Param("categoryId") Long categoryId);
    
    /**
     * Check if category has expenses
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ExpenseCategory ec LEFT JOIN ec.expenses e WHERE ec.id = :categoryId")
    boolean hasExpenses(@Param("categoryId") Long categoryId);
}

