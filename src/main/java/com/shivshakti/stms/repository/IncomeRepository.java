package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Income;
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
 * Repository interface for Income entity
 * Provides CRUD operations and custom queries for income management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find income by income number
     */
    Optional<Income> findByIncomeNumber(String incomeNumber);
    
    /**
     * Find incomes by client
     */
    List<Income> findByClientId(Long clientId);
    
    /**
     * Find incomes by builty
     */
    List<Income> findByBuiltyId(Long builtyId);
    
    /**
     * Find incomes by trip
     */
    List<Income> findByTripId(Long tripId);
    
    /**
     * Find incomes by income type
     */
    List<Income> findByIncomeType(Income.IncomeType incomeType);
    
    /**
     * Find incomes by payment mode
     */
    List<Income> findByPaymentMode(Income.PaymentMode paymentMode);
    
    /**
     * Find incomes by date range
     */
    List<Income> findByIncomeDateBetween(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // INCOME TYPE ANALYSIS
    // ===============================================
    
    /**
     * Find trip payment incomes
     */
    @Query("SELECT i FROM Income i WHERE i.incomeType = 'TRIP_PAYMENT' ORDER BY i.incomeDate DESC")
    List<Income> findTripPaymentIncomes();
    
    /**
     * Find advance payment incomes
     */
    @Query("SELECT i FROM Income i WHERE i.incomeType = 'ADVANCE' ORDER BY i.incomeDate DESC")
    List<Income> findAdvancePaymentIncomes();
    
    /**
     * Find detention charges
     */
    @Query("SELECT i FROM Income i WHERE i.incomeType = 'DETENTION' ORDER BY i.incomeDate DESC")
    List<Income> findDetentionCharges();
    
    /**
     * Get income summary by type
     */
    @Query("SELECT i.incomeType, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.incomeType " +
           "ORDER BY SUM(i.amount) DESC")
    List<Object[]> getIncomeSummaryByType(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total advance payments for period
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i " +
           "WHERE i.incomeType = 'ADVANCE' AND i.incomeDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalAdvancePayments(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // ===============================================
    // PAYMENT MODE ANALYSIS
    // ===============================================
    
    /**
     * Find cash payments
     */
    @Query("SELECT i FROM Income i WHERE i.paymentMode = 'CASH' ORDER BY i.incomeDate DESC")
    List<Income> findCashPayments();
    
    /**
     * Find cheque payments
     */
    @Query("SELECT i FROM Income i WHERE i.paymentMode = 'CHEQUE' ORDER BY i.incomeDate DESC")
    List<Income> findChequePayments();
    
    /**
     * Find bank transfer payments
     */
    @Query("SELECT i FROM Income i WHERE i.paymentMode IN ('BANK_TRANSFER', 'NEFT', 'RTGS', 'UPI') ORDER BY i.incomeDate DESC")
    List<Income> findBankTransferPayments();
    
    /**
     * Get payment mode distribution
     */
    @Query("SELECT i.paymentMode, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.paymentMode " +
           "ORDER BY SUM(i.amount) DESC")
    List<Object[]> getPaymentModeDistribution(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // ===============================================
    // CLIENT-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get client-wise income summary
     */
    @Query("SELECT i.client, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.client " +
           "ORDER BY SUM(i.amount) DESC")
    List<Object[]> getClientWiseIncomeSummary(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * Find top paying clients
     */
    @Query("SELECT i.client, COALESCE(SUM(i.amount), 0) as totalPayment " +
           "FROM Income i " +
           "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.client " +
           "ORDER BY totalPayment DESC")
    Page<Object[]> findTopPayingClients(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       Pageable pageable);
    
    /**
     * Get client payment pattern
     */
    @Query("SELECT i.client, i.paymentMode, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "WHERE i.client.id = :clientId " +
           "GROUP BY i.client, i.paymentMode " +
           "ORDER BY SUM(i.amount) DESC")
    List<Object[]> getClientPaymentPattern(@Param("clientId") Long clientId);

    // ===============================================
    // TRIP-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get trip-wise income summary
     */
    @Query("SELECT i.trip, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "WHERE i.trip IS NOT NULL AND i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.trip " +
           "ORDER BY SUM(i.amount) DESC")
    List<Object[]> getTripWiseIncomeSummary(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total income for trip
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.trip.id = :tripId")
    BigDecimal calculateTotalIncomeForTrip(@Param("tripId") Long tripId);

    // ===============================================
    // FINANCIAL ANALYSIS
    // ===============================================
    
    /**
     * Calculate total income for period
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.incomeDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalIncomeForPeriod(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Get income trend analysis
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM i.incomeDate) as year, " +
           "EXTRACT(MONTH FROM i.incomeDate) as month, " +
           "COUNT(i) as incomeCount, " +
           "COALESCE(SUM(i.amount), 0) as totalAmount " +
           "FROM Income i " +
           "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM i.incomeDate), EXTRACT(MONTH FROM i.incomeDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getIncomeTrendAnalysis(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * Get daily income summary
     */
    @Query("SELECT i.incomeDate, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.incomeDate " +
           "ORDER BY i.incomeDate DESC")
    List<Object[]> getDailyIncomeSummary(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // ===============================================
    // BANK DETAILS ANALYSIS
    // ===============================================
    
    /**
     * Find incomes by bank name
     */
    List<Income> findByBankNameContainingIgnoreCase(String bankName);
    
    /**
     * Find incomes by cheque number
     */
    Optional<Income> findByChequeNumber(String chequeNumber);
    
    /**
     * Find incomes by transaction reference
     */
    Optional<Income> findByTransactionReference(String transactionReference);
    
    /**
     * Get bank-wise income summary
     */
    @Query("SELECT i.bankName, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "WHERE i.bankName IS NOT NULL AND i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.bankName " +
           "ORDER BY SUM(i.amount) DESC")
    List<Object[]> getBankWiseIncomeSummary(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search incomes by multiple criteria
     */
    @Query("SELECT i FROM Income i WHERE " +
           "(:incomeNumber IS NULL OR i.incomeNumber = :incomeNumber) AND " +
           "(:clientId IS NULL OR i.client.id = :clientId) AND " +
           "(:builtyId IS NULL OR i.builty.id = :builtyId) AND " +
           "(:tripId IS NULL OR i.trip.id = :tripId) AND " +
           "(:incomeType IS NULL OR i.incomeType = :incomeType) AND " +
           "(:paymentMode IS NULL OR i.paymentMode = :paymentMode) AND " +
           "(:startDate IS NULL OR i.incomeDate >= :startDate) AND " +
           "(:endDate IS NULL OR i.incomeDate <= :endDate) AND " +
           "(:minAmount IS NULL OR i.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR i.amount <= :maxAmount)")
    Page<Income> searchIncomes(@Param("incomeNumber") String incomeNumber,
                              @Param("clientId") Long clientId,
                              @Param("builtyId") Long builtyId,
                              @Param("tripId") Long tripId,
                              @Param("incomeType") Income.IncomeType incomeType,
                              @Param("paymentMode") Income.PaymentMode paymentMode,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("minAmount") BigDecimal minAmount,
                              @Param("maxAmount") BigDecimal maxAmount,
                              Pageable pageable);
    
    /**
     * Find incomes by amount range
     */
    List<Income> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Find high value incomes
     */
    @Query("SELECT i FROM Income i WHERE i.amount > :threshold ORDER BY i.amount DESC")
    List<Income> findHighValueIncomes(@Param("threshold") BigDecimal threshold);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly income summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM i.incomeDate) as year, " +
           "EXTRACT(MONTH FROM i.incomeDate) as month, " +
           "COUNT(i) as incomeCount, " +
           "COALESCE(SUM(i.amount), 0) as totalAmount, " +
           "COUNT(CASE WHEN i.incomeType = 'ADVANCE' THEN 1 END) as advanceCount, " +
           "COUNT(CASE WHEN i.paymentMode = 'CASH' THEN 1 END) as cashCount " +
           "FROM Income i " +
           "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM i.incomeDate), EXTRACT(MONTH FROM i.incomeDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyIncomeSummary(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * Get income statistics
     */
    @Query("SELECT " +
           "COUNT(i) as totalIncomes, " +
           "COALESCE(SUM(i.amount), 0) as totalAmount, " +
           "COALESCE(AVG(i.amount), 0) as averageAmount, " +
           "COUNT(CASE WHEN i.incomeType = 'TRIP_PAYMENT' THEN 1 END) as tripPayments, " +
           "COUNT(CASE WHEN i.incomeType = 'ADVANCE' THEN 1 END) as advancePayments, " +
           "COUNT(CASE WHEN i.paymentMode = 'CASH' THEN 1 END) as cashPayments " +
           "FROM Income i")
    Object[] getIncomeStatistics();
    
    /**
     * Get top income sources
     */
    @Query("SELECT i.incomeType, COUNT(i), COALESCE(SUM(i.amount), 0) " +
           "FROM Income i " +
           "GROUP BY i.incomeType " +
           "ORDER BY SUM(i.amount) DESC")
    List<Object[]> getTopIncomeSources();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if income number exists for different income
     */
    boolean existsByIncomeNumberAndIdNot(String incomeNumber, Long id);
    
    /**
     * Check if cheque number exists for different income
     */
    boolean existsByChequeNumberAndIdNot(String chequeNumber, Long id);
    
    /**
     * Check if transaction reference exists for different income
     */
    boolean existsByTransactionReferenceAndIdNot(String transactionReference, Long id);
    
    /**
     * Count incomes by type
     */
    long countByIncomeType(Income.IncomeType incomeType);
    
    /**
     * Count incomes by payment mode
     */
    long countByPaymentMode(Income.PaymentMode paymentMode);
    
    /**
     * Count incomes for client in date range
     */
    long countByClientIdAndIncomeDateBetween(Long clientId, LocalDate startDate, LocalDate endDate);
}

