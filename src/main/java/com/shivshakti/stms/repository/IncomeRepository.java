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
 * Provides data access operations for income management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // Basic queries
    Optional<Income> findByIncomeNumber(String incomeNumber);
    Page<Income> findByIncomeType(String incomeType, Pageable pageable);
    Page<Income> findByIncomeCategory(String incomeCategory, Pageable pageable);
    Page<Income> findByTripId(Long tripId, Pageable pageable);
    Page<Income> findByClientId(Long clientId, Pageable pageable);
    Page<Income> findByBuiltyId(Long builtyId, Pageable pageable);
    Page<Income> findByPaymentStatus(String paymentStatus, Pageable pageable);
    Page<Income> findByIsRecurring(Boolean isRecurring, Pageable pageable);
    
    // Search queries
    @Query("SELECT i FROM Income i WHERE " +
           "(:incomeNumber IS NULL OR i.incomeNumber LIKE %:incomeNumber%) AND " +
           "(:incomeType IS NULL OR i.incomeType = :incomeType) AND " +
           "(:incomeCategory IS NULL OR i.incomeCategory = :incomeCategory) AND " +
           "(:paymentStatus IS NULL OR i.paymentStatus = :paymentStatus) AND " +
           "(:startDate IS NULL OR i.incomeDate >= :startDate) AND " +
           "(:endDate IS NULL OR i.incomeDate <= :endDate)")
    Page<Income> searchIncomes(@Param("incomeNumber") String incomeNumber,
                              @Param("incomeType") String incomeType,
                              @Param("incomeCategory") String incomeCategory,
                              @Param("paymentStatus") String paymentStatus,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              Pageable pageable);
    
    // Payment queries
    @Query("SELECT i FROM Income i WHERE i.paymentStatus = 'OVERDUE'")
    Page<Income> findOverdueIncomes(Pageable pageable);
    
    @Query("SELECT i FROM Income i WHERE i.paymentStatus = 'PARTIALLY_RECEIVED'")
    Page<Income> findPartiallyReceivedIncomes(Pageable pageable);
    
    @Query("SELECT SUM(i.amount + i.gstAmount - i.tdsAmount) FROM Income i WHERE i.paymentStatus = 'PENDING'")
    BigDecimal calculateTotalPendingAmount();
    
    @Query("SELECT SUM(i.receivedAmount) FROM Income i WHERE i.paymentStatus = 'RECEIVED' AND " +
           "i.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalReceivedAmount(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i.paymentStatus, COUNT(i), SUM(i.amount + i.gstAmount - i.tdsAmount) FROM Income i WHERE " +
           "i.incomeDate BETWEEN :startDate AND :endDate GROUP BY i.paymentStatus")
    List<Object[]> getPaymentSummary(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    // Recurring income queries
    @Query("SELECT i FROM Income i WHERE i.isRecurring = true AND i.nextRecurringDate <= :date")
    List<Income> findRecurringIncomesDue(@Param("date") LocalDate date);
    
    // Reporting queries
    @Query("SELECT i.incomeType, COUNT(i), SUM(i.amount) FROM Income i WHERE " +
           "i.incomeDate BETWEEN :startDate AND :endDate GROUP BY i.incomeType")
    List<Object[]> getIncomeByTypeReport(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i.incomeCategory, COUNT(i), SUM(i.amount) FROM Income i WHERE " +
           "i.incomeDate BETWEEN :startDate AND :endDate GROUP BY i.incomeCategory")
    List<Object[]> getIncomeByCategoryReport(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c.name, COUNT(i), SUM(i.amount + i.gstAmount - i.tdsAmount) FROM Income i JOIN i.client c WHERE " +
           "i.incomeDate BETWEEN :startDate AND :endDate GROUP BY c.id, c.name")
    List<Object[]> getClientWiseIncomeReport(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.tripNumber, COUNT(i), SUM(i.amount + i.gstAmount - i.tdsAmount) FROM Income i JOIN i.trip t WHERE " +
           "i.incomeDate BETWEEN :startDate AND :endDate GROUP BY t.id, t.tripNumber")
    List<Object[]> getTripWiseIncomeReport(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT MONTH(i.incomeDate), YEAR(i.incomeDate), COUNT(i), SUM(i.amount) FROM Income i WHERE " +
           "i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(i.incomeDate), MONTH(i.incomeDate) " +
           "ORDER BY YEAR(i.incomeDate), MONTH(i.incomeDate)")
    List<Object[]> getMonthlyIncomeSummary(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.incomeDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalIncome(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i FROM Income i WHERE " +
           "(:startDate IS NULL OR i.incomeDate >= :startDate) AND " +
           "(:endDate IS NULL OR i.incomeDate <= :endDate) AND " +
           "(:incomeType IS NULL OR i.incomeType = :incomeType) AND " +
           "(:status IS NULL OR i.paymentStatus = :status)")
    List<Income> findIncomesForReport(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("incomeType") String incomeType,
                                     @Param("status") String status);
    
    // Cash flow and aging reports
    @Query("SELECT DATE(i.incomeDate), SUM(i.receivedAmount), SUM(i.amount + i.gstAmount - i.tdsAmount - i.receivedAmount) " +
           "FROM Income i WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(i.incomeDate) ORDER BY DATE(i.incomeDate)")
    List<Object[]> getCashFlowReport(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT " +
           "COUNT(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) <= 30 THEN 1 END), " +
           "COUNT(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) BETWEEN 31 AND 60 THEN 1 END), " +
           "COUNT(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) BETWEEN 61 AND 90 THEN 1 END), " +
           "COUNT(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) > 90 THEN 1 END), " +
           "SUM(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) <= 30 THEN i.amount + i.gstAmount - i.tdsAmount - i.receivedAmount ELSE 0 END), " +
           "SUM(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) BETWEEN 31 AND 60 THEN i.amount + i.gstAmount - i.tdsAmount - i.receivedAmount ELSE 0 END), " +
           "SUM(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) BETWEEN 61 AND 90 THEN i.amount + i.gstAmount - i.tdsAmount - i.receivedAmount ELSE 0 END), " +
           "SUM(CASE WHEN DATEDIFF(CURRENT_DATE, i.expectedDate) > 90 THEN i.amount + i.gstAmount - i.tdsAmount - i.receivedAmount ELSE 0 END) " +
           "FROM Income i WHERE i.paymentStatus IN ('PENDING', 'PARTIALLY_RECEIVED', 'OVERDUE')")
    List<Object[]> getAgingReport();
    
    // Statistics queries
    @Query("SELECT COUNT(i), " +
           "COUNT(CASE WHEN i.paymentStatus = 'PENDING' THEN 1 END), " +
           "COUNT(CASE WHEN i.paymentStatus = 'RECEIVED' THEN 1 END), " +
           "COUNT(CASE WHEN i.paymentStatus = 'PARTIALLY_RECEIVED' THEN 1 END), " +
           "COUNT(CASE WHEN i.paymentStatus = 'OVERDUE' THEN 1 END), " +
           "SUM(i.amount) " +
           "FROM Income i")
    Object[] getIncomeStatistics();
    
    // Utility queries
    boolean existsByIncomeNumberAndIdNot(String incomeNumber, Long id);
    long countByIncomeTypeAndPaymentStatus(String incomeType, String paymentStatus);
    
    @Query("SELECT i.incomeNumber FROM Income i WHERE i.incomeNumber LIKE :pattern ORDER BY i.incomeNumber DESC")
    String findLastIncomeNumberForDate(@Param("pattern") String pattern);
}

