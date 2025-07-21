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
 * Provides data access operations for expense management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Basic queries
    Optional<Expense> findByExpenseNumber(String expenseNumber);
    Page<Expense> findByExpenseType(String expenseType, Pageable pageable);
    Page<Expense> findByExpenseCategory(String expenseCategory, Pageable pageable);
    Page<Expense> findByTripId(Long tripId, Pageable pageable);
    Page<Expense> findByTruckId(Long truckId, Pageable pageable);
    Page<Expense> findByDriverId(Long driverId, Pageable pageable);
    Page<Expense> findByPaymentStatus(String paymentStatus, Pageable pageable);
    Page<Expense> findByApprovalStatus(String approvalStatus, Pageable pageable);
    Page<Expense> findByReimbursementStatus(String reimbursementStatus, Pageable pageable);
    Page<Expense> findByIsRecurring(Boolean isRecurring, Pageable pageable);
    
    // Search queries
    @Query("SELECT e FROM Expense e WHERE " +
           "(:expenseNumber IS NULL OR e.expenseNumber LIKE %:expenseNumber%) AND " +
           "(:expenseType IS NULL OR e.expenseType = :expenseType) AND " +
           "(:expenseCategory IS NULL OR e.expenseCategory = :expenseCategory) AND " +
           "(:paymentStatus IS NULL OR e.paymentStatus = :paymentStatus) AND " +
           "(:approvalStatus IS NULL OR e.approvalStatus = :approvalStatus) AND " +
           "(:startDate IS NULL OR e.expenseDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenseDate <= :endDate)")
    Page<Expense> searchExpenses(@Param("expenseNumber") String expenseNumber,
                                @Param("expenseType") String expenseType,
                                @Param("expenseCategory") String expenseCategory,
                                @Param("paymentStatus") String paymentStatus,
                                @Param("approvalStatus") String approvalStatus,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                Pageable pageable);
    
    // Approval workflow queries
    @Query("SELECT e FROM Expense e WHERE e.approvalStatus = 'APPROVED' AND e.approvalDate BETWEEN :startDate AND :endDate")
    Page<Expense> findApprovedExpenses(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);
    
    @Query("SELECT e FROM Expense e WHERE e.approvalStatus = 'REJECTED' AND e.approvalDate BETWEEN :startDate AND :endDate")
    Page<Expense> findRejectedExpenses(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);
    
    @Query("SELECT e.approvalStatus, COUNT(e), SUM(e.amount) FROM Expense e WHERE " +
           "e.expenseDate BETWEEN :startDate AND :endDate GROUP BY e.approvalStatus")
    List<Object[]> getApprovalSummary(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    // Payment queries
    @Query("SELECT e FROM Expense e WHERE e.paymentStatus = 'PENDING' AND e.approvalStatus = 'APPROVED'")
    Page<Expense> findOverdueExpenses(Pageable pageable);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paymentStatus = 'PENDING' AND e.approvalStatus = 'APPROVED'")
    BigDecimal calculateTotalPendingAmount();
    
    @Query("SELECT e.paymentStatus, COUNT(e), SUM(e.amount) FROM Expense e WHERE " +
           "e.expenseDate BETWEEN :startDate AND :endDate GROUP BY e.paymentStatus")
    List<Object[]> getPaymentSummary(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    // Reimbursement queries
    @Query("SELECT e FROM Expense e WHERE e.reimbursementStatus = 'PROCESSED' AND e.expenseDate BETWEEN :startDate AND :endDate")
    Page<Expense> findProcessedReimbursements(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             Pageable pageable);
    
    @Query("SELECT SUM(e.reimbursementAmount) FROM Expense e WHERE e.reimbursementStatus = 'PROCESSED' AND " +
           "e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalReimbursementAmount(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
    
    // Recurring expenses
    @Query("SELECT e FROM Expense e WHERE e.isRecurring = true AND e.nextRecurringDate <= :date")
    List<Expense> findRecurringExpensesDue(@Param("date") LocalDate date);
    
    // Reporting queries
    @Query("SELECT e.expenseType, COUNT(e), SUM(e.amount) FROM Expense e WHERE " +
           "e.expenseDate BETWEEN :startDate AND :endDate GROUP BY e.expenseType")
    List<Object[]> getExpenseByTypeReport(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e.expenseCategory, COUNT(e), SUM(e.amount) FROM Expense e WHERE " +
           "e.expenseDate BETWEEN :startDate AND :endDate GROUP BY e.expenseCategory")
    List<Object[]> getExpenseByCategoryReport(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.truckNumber, COUNT(e), SUM(e.amount) FROM Expense e JOIN e.truck t WHERE " +
           "e.expenseDate BETWEEN :startDate AND :endDate GROUP BY t.id, t.truckNumber")
    List<Object[]> getTruckWiseExpenseReport(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT d.name, COUNT(e), SUM(e.amount) FROM Expense e JOIN e.driver d WHERE " +
           "e.expenseDate BETWEEN :startDate AND :endDate GROUP BY d.id, d.name")
    List<Object[]> getDriverWiseExpenseReport(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e.vendorName, COUNT(e), SUM(e.amount) FROM Expense e WHERE " +
           "e.vendorName IS NOT NULL AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.vendorName")
    List<Object[]> getVendorWiseExpenseReport(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT MONTH(e.expenseDate), YEAR(e.expenseDate), COUNT(e), SUM(e.amount) FROM Expense e WHERE " +
           "e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate) " +
           "ORDER BY YEAR(e.expenseDate), MONTH(e.expenseDate)")
    List<Object[]> getMonthlyExpenseSummary(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpenses(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Expense e WHERE " +
           "(:startDate IS NULL OR e.expenseDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenseDate <= :endDate) AND " +
           "(:expenseType IS NULL OR e.expenseType = :expenseType) AND " +
           "(:status IS NULL OR e.paymentStatus = :status)")
    List<Expense> findExpensesForReport(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       @Param("expenseType") String expenseType,
                                       @Param("status") String status);
    
    // Statistics queries
    @Query("SELECT COUNT(e), " +
           "COUNT(CASE WHEN e.paymentStatus = 'PENDING' THEN 1 END), " +
           "COUNT(CASE WHEN e.paymentStatus = 'PAID' THEN 1 END), " +
           "COUNT(CASE WHEN e.approvalStatus = 'PENDING' THEN 1 END), " +
           "COUNT(CASE WHEN e.approvalStatus = 'APPROVED' THEN 1 END), " +
           "SUM(e.amount) " +
           "FROM Expense e")
    Object[] getExpenseStatistics();
    
    // Utility queries
    boolean existsByExpenseNumberAndIdNot(String expenseNumber, Long id);
    long countByExpenseTypeAndPaymentStatus(String expenseType, String paymentStatus);
    
    @Query("SELECT e.expenseNumber FROM Expense e WHERE e.expenseNumber LIKE :pattern ORDER BY e.expenseNumber DESC")
    String findLastExpenseNumberForDate(@Param("pattern") String pattern);
}

