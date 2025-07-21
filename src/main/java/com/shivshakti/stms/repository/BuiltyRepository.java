package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Builty;
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
 * Repository interface for Builty entity
 * Provides CRUD operations and custom queries for builty/invoice management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface BuiltyRepository extends JpaRepository<Builty, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find builty by builty number
     */
    Optional<Builty> findByBuiltyNumber(String builtyNumber);
    
    /**
     * Find builties by client
     */
    List<Builty> findByClientId(Long clientId);
    
    /**
     * Find builties by trip
     */
    List<Builty> findByTripId(Long tripId);
    
    /**
     * Find builties by payment status
     */
    List<Builty> findByPaymentStatus(Builty.PaymentStatus paymentStatus);
    
    /**
     * Find builties by date range
     */
    List<Builty> findByBuiltyDateBetween(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // PAYMENT STATUS QUERIES
    // ===============================================
    
    /**
     * Find pending builties
     */
    @Query("SELECT b FROM Builty b WHERE b.paymentStatus = 'PENDING' ORDER BY b.builtyDate ASC")
    List<Builty> findPendingBuilties();
    
    /**
     * Find partially paid builties
     */
    @Query("SELECT b FROM Builty b WHERE b.paymentStatus = 'PARTIAL' ORDER BY b.builtyDate ASC")
    List<Builty> findPartiallyPaidBuilties();
    
    /**
     * Find overdue builties (pending/partial beyond specified days)
     */
    @Query("SELECT b FROM Builty b WHERE " +
           "b.paymentStatus IN ('PENDING', 'PARTIAL') AND " +
           "b.builtyDate < CURRENT_DATE - :days " +
           "ORDER BY b.builtyDate ASC")
    List<Builty> findOverdueBuilties(@Param("days") int days);
    
    /**
     * Find builties with balance amount greater than specified amount
     */
    List<Builty> findByBalanceAmountGreaterThan(BigDecimal amount);

    // ===============================================
    // CLIENT-WISE ANALYSIS
    // ===============================================
    
    /**
     * Find builties by client and payment status
     */
    List<Builty> findByClientIdAndPaymentStatus(Long clientId, Builty.PaymentStatus paymentStatus);
    
    /**
     * Get client-wise outstanding summary
     */
    @Query("SELECT b.client, COUNT(b), COALESCE(SUM(b.balanceAmount), 0) " +
           "FROM Builty b " +
           "WHERE b.paymentStatus IN ('PENDING', 'PARTIAL') " +
           "GROUP BY b.client " +
           "ORDER BY SUM(b.balanceAmount) DESC")
    List<Object[]> getClientWiseOutstandingSummary();
    
    /**
     * Get client business summary for date range
     */
    @Query("SELECT b.client, COUNT(b), " +
           "COALESCE(SUM(b.totalCharges), 0) as totalBusiness, " +
           "COALESCE(SUM(b.advanceReceived), 0) as totalAdvance, " +
           "COALESCE(SUM(b.balanceAmount), 0) as totalOutstanding " +
           "FROM Builty b " +
           "WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY b.client " +
           "ORDER BY totalBusiness DESC")
    List<Object[]> getClientBusinessSummary(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // ===============================================
    // FINANCIAL ANALYSIS
    // ===============================================
    
    /**
     * Calculate total outstanding amount
     */
    @Query("SELECT COALESCE(SUM(b.balanceAmount), 0) FROM Builty b WHERE b.paymentStatus IN ('PENDING', 'PARTIAL')")
    BigDecimal calculateTotalOutstandingAmount();
    
    /**
     * Calculate total business value for period
     */
    @Query("SELECT COALESCE(SUM(b.totalCharges), 0) FROM Builty b WHERE b.builtyDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalBusinessValue(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total advance received for period
     */
    @Query("SELECT COALESCE(SUM(b.advanceReceived), 0) FROM Builty b WHERE b.builtyDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalAdvanceReceived(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Get revenue analysis
     */
    @Query("SELECT " +
           "COALESCE(SUM(b.totalCharges), 0) as totalCharges, " +
           "COALESCE(SUM(b.loadingCharges), 0) as totalLoadingCharges, " +
           "COALESCE(SUM(b.unloadingCharges), 0) as totalUnloadingCharges, " +
           "COALESCE(SUM(b.detentionCharges), 0) as totalDetentionCharges, " +
           "COALESCE(SUM(b.otherCharges), 0) as totalOtherCharges " +
           "FROM Builty b " +
           "WHERE b.builtyDate BETWEEN :startDate AND :endDate")
    Object[] getRevenueAnalysis(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    // ===============================================
    // GOODS AND WEIGHT ANALYSIS
    // ===============================================
    
    /**
     * Find builties by goods type
     */
    List<Builty> findByGoodsTypeContainingIgnoreCase(String goodsType);
    
    /**
     * Get goods type summary
     */
    @Query("SELECT b.goodsType, COUNT(b), COALESCE(SUM(b.weightTons), 0), COALESCE(SUM(b.totalCharges), 0) " +
           "FROM Builty b " +
           "WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY b.goodsType " +
           "ORDER BY COUNT(b) DESC")
    List<Object[]> getGoodsTypeSummary(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
    
    /**
     * Find builties by weight range
     */
    List<Builty> findByWeightTonsBetween(BigDecimal minWeight, BigDecimal maxWeight);
    
    /**
     * Get weight distribution analysis
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN b.weightTons <= 5 THEN 1 END) as upTo5Tons, " +
           "COUNT(CASE WHEN b.weightTons > 5 AND b.weightTons <= 10 THEN 1 END) as from5To10Tons, " +
           "COUNT(CASE WHEN b.weightTons > 10 AND b.weightTons <= 20 THEN 1 END) as from10To20Tons, " +
           "COUNT(CASE WHEN b.weightTons > 20 THEN 1 END) as above20Tons " +
           "FROM Builty b " +
           "WHERE b.builtyDate BETWEEN :startDate AND :endDate")
    Object[] getWeightDistributionAnalysis(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // ===============================================
    // RATE AND PRICING ANALYSIS
    // ===============================================
    
    /**
     * Get rate analysis by goods type
     */
    @Query("SELECT b.goodsType, " +
           "COALESCE(AVG(b.ratePerTon), 0) as avgRate, " +
           "COALESCE(MIN(b.ratePerTon), 0) as minRate, " +
           "COALESCE(MAX(b.ratePerTon), 0) as maxRate, " +
           "COUNT(b) as builtyCount " +
           "FROM Builty b " +
           "WHERE b.ratePerTon IS NOT NULL AND b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY b.goodsType " +
           "ORDER BY avgRate DESC")
    List<Object[]> getRateAnalysisByGoodsType(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * Find high value builties
     */
    @Query("SELECT b FROM Builty b WHERE b.totalCharges > :threshold ORDER BY b.totalCharges DESC")
    List<Builty> findHighValueBuilties(@Param("threshold") BigDecimal threshold);

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search builties by multiple criteria
     */
    @Query("SELECT b FROM Builty b WHERE " +
           "(:builtyNumber IS NULL OR b.builtyNumber = :builtyNumber) AND " +
           "(:clientId IS NULL OR b.client.id = :clientId) AND " +
           "(:tripId IS NULL OR b.trip.id = :tripId) AND " +
           "(:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus) AND " +
           "(:goodsType IS NULL OR LOWER(b.goodsType) LIKE LOWER(CONCAT('%', :goodsType, '%'))) AND " +
           "(:startDate IS NULL OR b.builtyDate >= :startDate) AND " +
           "(:endDate IS NULL OR b.builtyDate <= :endDate) AND " +
           "(:minAmount IS NULL OR b.totalCharges >= :minAmount) AND " +
           "(:maxAmount IS NULL OR b.totalCharges <= :maxAmount)")
    Page<Builty> searchBuilties(@Param("builtyNumber") String builtyNumber,
                               @Param("clientId") Long clientId,
                               @Param("tripId") Long tripId,
                               @Param("paymentStatus") Builty.PaymentStatus paymentStatus,
                               @Param("goodsType") String goodsType,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate,
                               @Param("minAmount") BigDecimal minAmount,
                               @Param("maxAmount") BigDecimal maxAmount,
                               Pageable pageable);

    // ===============================================
    // AGING ANALYSIS
    // ===============================================
    
    /**
     * Get builty aging report
     */
    @Query("SELECT b, " +
           "CASE " +
           "WHEN b.builtyDate >= CURRENT_DATE - 30 THEN 'CURRENT' " +
           "WHEN b.builtyDate >= CURRENT_DATE - 60 THEN '30_DAYS' " +
           "WHEN b.builtyDate >= CURRENT_DATE - 90 THEN '60_DAYS' " +
           "ELSE '90_PLUS_DAYS' " +
           "END as agingBucket, " +
           "b.balanceAmount " +
           "FROM Builty b " +
           "WHERE b.paymentStatus IN ('PENDING', 'PARTIAL') " +
           "ORDER BY b.builtyDate ASC")
    List<Object[]> getBuiltyAgingReport();
    
    /**
     * Get aging summary
     */
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN b.builtyDate >= CURRENT_DATE - 30 THEN b.balanceAmount ELSE 0 END), 0) as current, " +
           "COALESCE(SUM(CASE WHEN b.builtyDate BETWEEN CURRENT_DATE - 60 AND CURRENT_DATE - 31 THEN b.balanceAmount ELSE 0 END), 0) as days30, " +
           "COALESCE(SUM(CASE WHEN b.builtyDate BETWEEN CURRENT_DATE - 90 AND CURRENT_DATE - 61 THEN b.balanceAmount ELSE 0 END), 0) as days60, " +
           "COALESCE(SUM(CASE WHEN b.builtyDate < CURRENT_DATE - 90 THEN b.balanceAmount ELSE 0 END), 0) as days90Plus " +
           "FROM Builty b " +
           "WHERE b.paymentStatus IN ('PENDING', 'PARTIAL')")
    Object[] getAgingSummary();

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly builty summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM b.builtyDate) as year, " +
           "EXTRACT(MONTH FROM b.builtyDate) as month, " +
           "COUNT(b) as builtyCount, " +
           "COALESCE(SUM(b.totalCharges), 0) as totalCharges, " +
           "COALESCE(SUM(b.advanceReceived), 0) as totalAdvance, " +
           "COALESCE(SUM(b.balanceAmount), 0) as totalOutstanding, " +
           "COUNT(CASE WHEN b.paymentStatus = 'PAID' THEN 1 END) as paidCount " +
           "FROM Builty b " +
           "WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM b.builtyDate), EXTRACT(MONTH FROM b.builtyDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyBuiltySummary(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * Get builty statistics
     */
    @Query("SELECT " +
           "COUNT(b) as totalBuilties, " +
           "COUNT(CASE WHEN b.paymentStatus = 'PENDING' THEN 1 END) as pendingBuilties, " +
           "COUNT(CASE WHEN b.paymentStatus = 'PARTIAL' THEN 1 END) as partialBuilties, " +
           "COUNT(CASE WHEN b.paymentStatus = 'PAID' THEN 1 END) as paidBuilties, " +
           "COALESCE(SUM(b.totalCharges), 0) as totalCharges, " +
           "COALESCE(SUM(b.balanceAmount), 0) as totalOutstanding, " +
           "COALESCE(AVG(b.totalCharges), 0) as averageCharges " +
           "FROM Builty b")
    Object[] getBuiltyStatistics();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if builty number exists for different builty
     */
    boolean existsByBuiltyNumberAndIdNot(String builtyNumber, Long id);
    
    /**
     * Count builties by payment status
     */
    long countByPaymentStatus(Builty.PaymentStatus paymentStatus);
    
    /**
     * Count overdue builties
     */
    @Query("SELECT COUNT(b) FROM Builty b WHERE " +
           "b.paymentStatus IN ('PENDING', 'PARTIAL') AND " +
           "b.builtyDate < CURRENT_DATE - :days")
    long countOverdueBuilties(@Param("days") int days);
    
    /**
     * Count builties for client in date range
     */
    long countByClientIdAndBuiltyDateBetween(Long clientId, LocalDate startDate, LocalDate endDate);
}

