package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Expense;
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
 * Repository interface for Expense entity
 * Provides CRUD operations and custom queries for expense management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find expense by expense number
     */
    Optional<Expense> findByExpenseNumber(String expenseNumber);
    
    /**
     * Find expenses by truck
     */
    List<Expense> findByTruckId(Long truckId);
    
    /**
     * Find expenses by trip
     */
    List<Expense> findByTripId(Long tripId);
    
    /**
     * Find expenses by category
     */
    List<Expense> findByCategoryId(Long categoryId);
    
    /**
     * Find expenses by shop
     */
    List<Expense> findByShopId(Long shopId);
    
    /**
     * Find expenses by date range
     */
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // APPROVAL STATUS QUERIES
    // ===============================================
    
    /**
     * Find pending approval expenses
     */
    @Query("SELECT e FROM Expense e WHERE e.isApproved = false ORDER BY e.expenseDate ASC")
    List<Expense> findPendingApprovalExpenses();
    
    /**
     * Find approved expenses
     */
    @Query("SELECT e FROM Expense e WHERE e.isApproved = true ORDER BY e.expenseDate DESC")
    List<Expense> findApprovedExpenses();
    
    /**
     * Find expenses approved by specific person
     */
    List<Expense> findByApprovedBy(String approvedBy);
    
    /**
     * Find high value expenses requiring approval
     */
    @Query("SELECT e FROM Expense e WHERE e.amount > :threshold AND e.isApproved = false ORDER BY e.amount DESC")
    List<Expense> findHighValueExpensesPendingApproval(@Param("threshold") BigDecimal threshold);

    // ===============================================
    // CATEGORY-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get expense summary by category
     */
    @Query("SELECT e.category, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM Expense e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getExpenseSummaryByCategory(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    /**
     * Find fuel expenses
     */
    @Query("SELECT e FROM Expense e JOIN e.category c WHERE c.categoryName = 'FUEL' ORDER BY e.expenseDate DESC")
    List<Expense> findFuelExpenses();
    
    /**
     * Find maintenance expenses
     */
    @Query("SELECT e FROM Expense e JOIN e.category c WHERE c.categoryName = 'MAINTENANCE' ORDER BY e.expenseDate DESC")
    List<Expense> findMaintenanceExpenses();
    
    /**
     * Find toll expenses
     */
    @Query("SELECT e FROM Expense e JOIN e.category c WHERE c.categoryName = 'TOLL' ORDER BY e.expenseDate DESC")
    List<Expense> findTollExpenses();
    
    /**
     * Calculate total fuel cost for period
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e JOIN e.category c " +
           "WHERE c.categoryName = 'FUEL' AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalFuelCost(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    // ===============================================
    // TRUCK-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get truck-wise expense summary
     */
    @Query("SELECT e.truck, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM Expense e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.truck " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTruckWiseExpenseSummary(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * Find trucks with highest expenses
     */
    @Query("SELECT e.truck, COALESCE(SUM(e.amount), 0) as totalExpense " +
           "FROM Expense e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.truck " +
           "ORDER BY totalExpense DESC")
    Page<Object[]> findTrucksWithHighestExpenses(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                Pageable pageable);
    
    /**
     * Get truck expense breakdown by category
     */
    @Query("SELECT e.category.categoryName, COALESCE(SUM(e.amount), 0) " +
           "FROM Expense e " +
           "WHERE e.truck.id = :truckId AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category.categoryName " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTruckExpenseBreakdown(@Param("truckId") Long truckId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // ===============================================
    // TRIP-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get trip-wise expense summary
     */
    @Query("SELECT e.trip, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM Expense e " +
           "WHERE e.trip IS NOT NULL AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.trip " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTripWiseExpenseSummary(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total expenses for trip
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.trip.id = :tripId")
    BigDecimal calculateTotalExpensesForTrip(@Param("tripId") Long tripId);

    // ===============================================
    // SHOP-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get shop-wise expense summary
     */
    @Query("SELECT e.shop, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM Expense e " +
           "WHERE e.shop IS NOT NULL AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.shop " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getShopWiseExpenseSummary(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Find preferred shops by expense volume
     */
    @Query("SELECT e.shop, COUNT(e) as transactionCount, COALESCE(SUM(e.amount), 0) as totalAmount " +
           "FROM Expense e " +
           "WHERE e.shop IS NOT NULL " +
           "GROUP BY e.shop " +
           "ORDER BY transactionCount DESC, totalAmount DESC")
    List<Object[]> findPreferredShopsByVolume();

    // ===============================================
    // FINANCIAL ANALYSIS
    // ===============================================
    
    /**
     * Calculate total expenses for period
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpensesForPeriod(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total GST for period
     */
    @Query("SELECT COALESCE(SUM(e.gstAmount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalGSTForPeriod(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * Get expense trend analysis
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM e.expenseDate) as year, " +
           "EXTRACT(MONTH FROM e.expenseDate) as month, " +
           "COUNT(e) as expenseCount, " +
           "COALESCE(SUM(e.amount), 0) as totalAmount " +
           "FROM Expense e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM e.expenseDate), EXTRACT(MONTH FROM e.expenseDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getExpenseTrendAnalysis(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search expenses by multiple criteria
     */
    @Query("SELECT e FROM Expense e WHERE " +
           "(:expenseNumber IS NULL OR e.expenseNumber = :expenseNumber) AND " +
           "(:truckId IS NULL OR e.truck.id = :truckId) AND " +
           "(:tripId IS NULL OR e.trip.id = :tripId) AND " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:shopId IS NULL OR e.shop.id = :shopId) AND " +
           "(:isApproved IS NULL OR e.isApproved = :isApproved) AND " +
           "(:startDate IS NULL OR e.expenseDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenseDate <= :endDate) AND " +
           "(:minAmount IS NULL OR e.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR e.amount <= :maxAmount)")
    Page<Expense> searchExpenses(@Param("expenseNumber") String expenseNumber,
                                @Param("truckId") Long truckId,
                                @Param("tripId") Long tripId,
                                @Param("categoryId") Long categoryId,
                                @Param("shopId") Long shopId,
                                @Param("isApproved") Boolean isApproved,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("minAmount") BigDecimal minAmount,
                                @Param("maxAmount") BigDecimal maxAmount,
                                Pageable pageable);
    
    /**
     * Find expenses by amount range
     */
    List<Expense> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Find expenses by description containing
     */
    List<Expense> findByDescriptionContainingIgnoreCase(String description);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly expense summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM e.expenseDate) as year, " +
           "EXTRACT(MONTH FROM e.expenseDate) as month, " +
           "COUNT(e) as expenseCount, " +
           "COUNT(CASE WHEN e.isApproved = true THEN 1 END) as approvedCount, " +
           "COALESCE(SUM(e.amount), 0) as totalAmount, " +
           "COALESCE(SUM(e.gstAmount), 0) as totalGST " +
           "FROM Expense e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM e.expenseDate), EXTRACT(MONTH FROM e.expenseDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyExpenseSummary(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * Get expense statistics
     */
    @Query("SELECT " +
           "COUNT(e) as totalExpenses, " +
           "COUNT(CASE WHEN e.isApproved = true THEN 1 END) as approvedExpenses, " +
           "COUNT(CASE WHEN e.isApproved = false THEN 1 END) as pendingExpenses, " +
           "COALESCE(SUM(e.amount), 0) as totalAmount, " +
           "COALESCE(AVG(e.amount), 0) as averageAmount, " +
           "COALESCE(SUM(e.gstAmount), 0) as totalGST " +
           "FROM Expense e")
    Object[] getExpenseStatistics();
    
    /**
     * Get top expense categories
     */
    @Query("SELECT e.category.categoryName, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM Expense e " +
           "GROUP BY e.category.categoryName " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getTopExpenseCategories();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if expense number exists for different expense
     */
    boolean existsByExpenseNumberAndIdNot(String expenseNumber, Long id);
    
    /**
     * Count approved expenses
     */
    long countByIsApprovedTrue();
    
    /**
     * Count pending approval expenses
     */
    long countByIsApprovedFalse();
    
    /**
     * Count expenses for truck in date range
     */
    long countByTruckIdAndExpenseDateBetween(Long truckId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Count expenses by category
     */
    long countByCategoryId(Long categoryId);
}

