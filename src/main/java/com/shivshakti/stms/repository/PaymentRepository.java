package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Payment;
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
 * Repository interface for Payment entity
 * Provides CRUD operations and custom queries for payment management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find payment by payment number
     */
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    
    /**
     * Find payments by client
     */
    List<Payment> findByClientId(Long clientId);
    
    /**
     * Find payments by builty
     */
    List<Payment> findByBuiltyId(Long builtyId);
    
    /**
     * Find payments by payment type
     */
    List<Payment> findByPaymentType(Payment.PaymentType paymentType);
    
    /**
     * Find payments by payment mode
     */
    List<Payment> findByPaymentMode(Payment.PaymentMode paymentMode);
    
    /**
     * Find payments by status
     */
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    /**
     * Find payments by date range
     */
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // PAYMENT STATUS QUERIES
    // ===============================================
    
    /**
     * Find received payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'RECEIVED' ORDER BY p.paymentDate DESC")
    List<Payment> findReceivedPayments();
    
    /**
     * Find cleared payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'CLEARED' ORDER BY p.clearedDate DESC")
    List<Payment> findClearedPayments();
    
    /**
     * Find bounced payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'BOUNCED' ORDER BY p.paymentDate DESC")
    List<Payment> findBouncedPayments();
    
    /**
     * Find pending payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' ORDER BY p.paymentDate ASC")
    List<Payment> findPendingPayments();
    
    /**
     * Get payment status distribution
     */
    @Query("SELECT p.status, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.status " +
           "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getPaymentStatusDistribution(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    // ===============================================
    // PAYMENT TYPE ANALYSIS
    // ===============================================
    
    /**
     * Find advance payments
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentType = 'ADVANCE' ORDER BY p.paymentDate DESC")
    List<Payment> findAdvancePayments();
    
    /**
     * Find partial payments
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentType = 'PARTIAL' ORDER BY p.paymentDate DESC")
    List<Payment> findPartialPayments();
    
    /**
     * Find full payments
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentType = 'FULL' ORDER BY p.paymentDate DESC")
    List<Payment> findFullPayments();
    
    /**
     * Get payment type distribution
     */
    @Query("SELECT p.paymentType, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.paymentType " +
           "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getPaymentTypeDistribution(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // ===============================================
    // PAYMENT MODE ANALYSIS
    // ===============================================
    
    /**
     * Find cash payments
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentMode = 'CASH' ORDER BY p.paymentDate DESC")
    List<Payment> findCashPayments();
    
    /**
     * Find cheque payments
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentMode = 'CHEQUE' ORDER BY p.paymentDate DESC")
    List<Payment> findChequePayments();
    
    /**
     * Find bank transfer payments
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentMode IN ('BANK_TRANSFER', 'NEFT', 'RTGS', 'UPI') ORDER BY p.paymentDate DESC")
    List<Payment> findBankTransferPayments();
    
    /**
     * Get payment mode distribution
     */
    @Query("SELECT p.paymentMode, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.paymentMode " +
           "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getPaymentModeDistribution(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // ===============================================
    // CLIENT-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get client-wise payment summary
     */
    @Query("SELECT p.client, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.client " +
           "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getClientWisePaymentSummary(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    /**
     * Find top paying clients
     */
    @Query("SELECT p.client, COALESCE(SUM(p.amount), 0) as totalPayment " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'CLEARED' " +
           "GROUP BY p.client " +
           "ORDER BY totalPayment DESC")
    Page<Object[]> findTopPayingClients(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       Pageable pageable);
    
    /**
     * Get client payment behavior
     */
    @Query("SELECT p.client, p.paymentMode, p.paymentType, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.client.id = :clientId " +
           "GROUP BY p.client, p.paymentMode, p.paymentType " +
           "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getClientPaymentBehavior(@Param("clientId") Long clientId);

    // ===============================================
    // CHEQUE MANAGEMENT
    // ===============================================
    
    /**
     * Find payment by cheque number
     */
    Optional<Payment> findByChequeNumber(String chequeNumber);
    
    /**
     * Find payments by bank name
     */
    List<Payment> findByBankNameContainingIgnoreCase(String bankName);
    
    /**
     * Find expired cheques
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentMode = 'CHEQUE' AND p.chequeDate < CURRENT_DATE - 90 AND p.status != 'CLEARED'")
    List<Payment> findExpiredCheques();
    
    /**
     * Find cheques due for clearance
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentMode = 'CHEQUE' AND p.status = 'RECEIVED' ORDER BY p.chequeDate ASC")
    List<Payment> findChequesDueForClearance();
    
    /**
     * Get bank-wise cheque summary
     */
    @Query("SELECT p.bankName, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.paymentMode = 'CHEQUE' AND p.bankName IS NOT NULL " +
           "GROUP BY p.bankName " +
           "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getBankWiseChequeSummary();

    // ===============================================
    // FINANCIAL ANALYSIS
    // ===============================================
    
    /**
     * Calculate total payments for period
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalPaymentsForPeriod(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate cleared payments for period
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'CLEARED'")
    BigDecimal calculateClearedPaymentsForPeriod(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate pending payments amount
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status IN ('RECEIVED', 'PENDING')")
    BigDecimal calculatePendingPaymentsAmount();
    
    /**
     * Get payment collection efficiency
     */
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN p.status = 'CLEARED' THEN p.amount ELSE 0 END), 0) as clearedAmount, " +
           "COALESCE(SUM(CASE WHEN p.status = 'BOUNCED' THEN p.amount ELSE 0 END), 0) as bouncedAmount, " +
           "COALESCE(SUM(p.amount), 0) as totalAmount " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    Object[] getPaymentCollectionEfficiency(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // ===============================================
    // BUILTY-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get builty payment summary
     */
    @Query("SELECT p.builty, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.builty IS NOT NULL " +
           "GROUP BY p.builty " +
           "ORDER BY SUM(p.amount) DESC")
    List<Object[]> getBuiltyPaymentSummary();
    
    /**
     * Find payments for builty
     */
    @Query("SELECT p FROM Payment p WHERE p.builty.id = :builtyId ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsForBuilty(@Param("builtyId") Long builtyId);
    
    /**
     * Calculate total payments for builty
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.builty.id = :builtyId AND p.status = 'CLEARED'")
    BigDecimal calculateTotalPaymentsForBuilty(@Param("builtyId") Long builtyId);

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search payments by multiple criteria
     */
    @Query("SELECT p FROM Payment p WHERE " +
           "(:paymentNumber IS NULL OR p.paymentNumber = :paymentNumber) AND " +
           "(:clientId IS NULL OR p.client.id = :clientId) AND " +
           "(:builtyId IS NULL OR p.builty.id = :builtyId) AND " +
           "(:paymentType IS NULL OR p.paymentType = :paymentType) AND " +
           "(:paymentMode IS NULL OR p.paymentMode = :paymentMode) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.paymentDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.paymentDate <= :endDate) AND " +
           "(:minAmount IS NULL OR p.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR p.amount <= :maxAmount)")
    Page<Payment> searchPayments(@Param("paymentNumber") String paymentNumber,
                                @Param("clientId") Long clientId,
                                @Param("builtyId") Long builtyId,
                                @Param("paymentType") Payment.PaymentType paymentType,
                                @Param("paymentMode") Payment.PaymentMode paymentMode,
                                @Param("status") Payment.PaymentStatus status,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("minAmount") BigDecimal minAmount,
                                @Param("maxAmount") BigDecimal maxAmount,
                                Pageable pageable);
    
    /**
     * Find payments by amount range
     */
    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Find high value payments
     */
    @Query("SELECT p FROM Payment p WHERE p.amount > :threshold ORDER BY p.amount DESC")
    List<Payment> findHighValuePayments(@Param("threshold") BigDecimal threshold);
    
    /**
     * Find payments by transaction reference
     */
    Optional<Payment> findByTransactionReference(String transactionReference);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly payment summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM p.paymentDate) as year, " +
           "EXTRACT(MONTH FROM p.paymentDate) as month, " +
           "COUNT(p) as paymentCount, " +
           "COALESCE(SUM(p.amount), 0) as totalAmount, " +
           "COUNT(CASE WHEN p.status = 'CLEARED' THEN 1 END) as clearedCount, " +
           "COUNT(CASE WHEN p.status = 'BOUNCED' THEN 1 END) as bouncedCount " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyPaymentSummary(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * Get payment statistics
     */
    @Query("SELECT " +
           "COUNT(p) as totalPayments, " +
           "COALESCE(SUM(p.amount), 0) as totalAmount, " +
           "COALESCE(AVG(p.amount), 0) as averageAmount, " +
           "COUNT(CASE WHEN p.status = 'CLEARED' THEN 1 END) as clearedPayments, " +
           "COUNT(CASE WHEN p.status = 'BOUNCED' THEN 1 END) as bouncedPayments, " +
           "COUNT(CASE WHEN p.paymentMode = 'CASH' THEN 1 END) as cashPayments, " +
           "COUNT(CASE WHEN p.paymentMode = 'CHEQUE' THEN 1 END) as chequePayments " +
           "FROM Payment p")
    Object[] getPaymentStatistics();
    
    /**
     * Get daily payment collection
     */
    @Query("SELECT p.paymentDate, COUNT(p), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'CLEARED' " +
           "GROUP BY p.paymentDate " +
           "ORDER BY p.paymentDate DESC")
    List<Object[]> getDailyPaymentCollection(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Get payment trend analysis
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM p.paymentDate) as year, " +
           "EXTRACT(MONTH FROM p.paymentDate) as month, " +
           "COUNT(p) as paymentCount, " +
           "COALESCE(SUM(p.amount), 0) as totalAmount " +
           "FROM Payment p " +
           "WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM p.paymentDate), EXTRACT(MONTH FROM p.paymentDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getPaymentTrendAnalysis(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if payment number exists for different payment
     */
    boolean existsByPaymentNumberAndIdNot(String paymentNumber, Long id);
    
    /**
     * Check if cheque number exists for different payment
     */
    boolean existsByChequeNumberAndIdNot(String chequeNumber, Long id);
    
    /**
     * Check if transaction reference exists for different payment
     */
    boolean existsByTransactionReferenceAndIdNot(String transactionReference, Long id);
    
    /**
     * Count payments by status
     */
    long countByStatus(Payment.PaymentStatus status);
    
    /**
     * Count payments by type
     */
    long countByPaymentType(Payment.PaymentType paymentType);
    
    /**
     * Count payments by mode
     */
    long countByPaymentMode(Payment.PaymentMode paymentMode);
    
    /**
     * Count payments for client in date range
     */
    long countByClientIdAndPaymentDateBetween(Long clientId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Count bounced cheques
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMode = 'CHEQUE' AND p.status = 'BOUNCED'")
    long countBouncedCheques();
}

