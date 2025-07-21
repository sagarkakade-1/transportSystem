package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.ExpenseDTO;
import com.shivshakti.stms.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Expense management
 * Defines business operations for expense-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface ExpenseService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    ExpenseDTO createExpense(ExpenseDTO expenseDTO);
    ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO);
    Optional<ExpenseDTO> getExpenseById(Long id);
    Page<ExpenseDTO> getAllExpenses(Pageable pageable);
    void deleteExpense(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    Page<ExpenseDTO> searchExpenses(String expenseNumber, String expenseType, String expenseCategory,
                                   String paymentStatus, String approvalStatus, LocalDate startDate, 
                                   LocalDate endDate, Pageable pageable);
    
    Optional<ExpenseDTO> findByExpenseNumber(String expenseNumber);
    Page<ExpenseDTO> getExpensesByType(String expenseType, Pageable pageable);
    Page<ExpenseDTO> getExpensesByCategory(String expenseCategory, Pageable pageable);
    Page<ExpenseDTO> getExpensesByTrip(Long tripId, Pageable pageable);
    Page<ExpenseDTO> getExpensesByTruck(Long truckId, Pageable pageable);
    Page<ExpenseDTO> getExpensesByDriver(Long driverId, Pageable pageable);

    // ===============================================
    // APPROVAL WORKFLOW
    // ===============================================
    
    ExpenseDTO approveExpense(Long id, Long approverId, String remarks);
    ExpenseDTO rejectExpense(Long id, Long approverId, String remarks);
    ExpenseDTO submitForReview(Long id, String remarks);
    Page<ExpenseDTO> getPendingApprovals(Pageable pageable);
    Page<ExpenseDTO> getApprovedExpenses(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<ExpenseDTO> getRejectedExpenses(LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Object[]> getApprovalSummary(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================
    
    ExpenseDTO updatePaymentStatus(Long id, String paymentStatus, LocalDate paymentDate, String remarks);
    ExpenseDTO markAsPaid(Long id, LocalDate paymentDate, String paymentMethod, String referenceNumber);
    Page<ExpenseDTO> getPendingPayments(Pageable pageable);
    Page<ExpenseDTO> getOverdueExpenses(Pageable pageable);
    BigDecimal calculateTotalPendingAmount();
    List<Object[]> getPaymentSummary(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // REIMBURSEMENT MANAGEMENT
    // ===============================================
    
    ExpenseDTO processReimbursement(Long id, BigDecimal reimbursementAmount, String remarks);
    ExpenseDTO rejectReimbursement(Long id, String remarks);
    Page<ExpenseDTO> getPendingReimbursements(Pageable pageable);
    Page<ExpenseDTO> getProcessedReimbursements(LocalDate startDate, LocalDate endDate, Pageable pageable);
    BigDecimal calculateTotalReimbursementAmount(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // RECURRING EXPENSES
    // ===============================================
    
    ExpenseDTO createRecurringExpense(ExpenseDTO expenseDTO, String frequency, LocalDate nextDate);
    List<ExpenseDTO> generateRecurringExpenses(LocalDate forDate);
    Page<ExpenseDTO> getRecurringExpenses(Pageable pageable);
    ExpenseDTO updateRecurringSchedule(Long id, String frequency, LocalDate nextDate);

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================
    
    Object[] getExpenseStatistics();
    List<Object[]> getMonthlyExpenseSummary(LocalDate startDate, LocalDate endDate);
    List<Object[]> getExpenseByTypeReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getExpenseByCategoryReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getTruckWiseExpenseReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getDriverWiseExpenseReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getVendorWiseExpenseReport(LocalDate startDate, LocalDate endDate);
    BigDecimal calculateTotalExpenses(LocalDate startDate, LocalDate endDate);
    List<ExpenseDTO> generateExpenseReport(LocalDate startDate, LocalDate endDate, String expenseType, String status);

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    void validateExpenseForCreation(ExpenseDTO expenseDTO);
    void validateExpenseForUpdate(Long id, ExpenseDTO expenseDTO);
    boolean isExpenseNumberUnique(String expenseNumber, Long excludeId);
    boolean canDeleteExpense(Long id);
    boolean canApproveExpense(Long id, Long approverId);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    ExpenseDTO convertToDTO(Expense expense);
    Expense convertToEntity(ExpenseDTO expenseDTO);
    String generateExpenseNumber();
    LocalDate calculateNextRecurringDate(LocalDate currentDate, String frequency);
    long getExpenseCount(String expenseType, String status);
}
