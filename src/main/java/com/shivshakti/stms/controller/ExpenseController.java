package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.ExpenseDTO;
import com.shivshakti.stms.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Expense management
 * Provides comprehensive API endpoints for expense operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO createdExpense = expenseService.createExpense(expenseDTO);
        return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(updatedExpense);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long id) {
        Optional<ExpenseDTO> expense = expenseService.getExpenseById(id);
        return expense.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseDTO>> getAllExpenses(Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getAllExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<ExpenseDTO>> searchExpenses(
            @RequestParam(required = false) String expenseNumber,
            @RequestParam(required = false) String expenseType,
            @RequestParam(required = false) String expenseCategory,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.searchExpenses(expenseNumber, expenseType, expenseCategory, 
                                                                 paymentStatus, approvalStatus, startDate, endDate, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-expense-number/{expenseNumber}")
    public ResponseEntity<ExpenseDTO> getExpenseByExpenseNumber(@PathVariable String expenseNumber) {
        Optional<ExpenseDTO> expense = expenseService.findByExpenseNumber(expenseNumber);
        return expense.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type/{expenseType}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByType(@PathVariable String expenseType, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByType(expenseType, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-category/{expenseCategory}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByCategory(@PathVariable String expenseCategory, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByCategory(expenseCategory, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-trip/{tripId}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByTrip(@PathVariable Long tripId, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByTrip(tripId, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-truck/{truckId}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByTruck(@PathVariable Long truckId, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByTruck(truckId, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/by-driver/{driverId}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByDriver(@PathVariable Long driverId, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByDriver(driverId, pageable);
        return ResponseEntity.ok(expenses);
    }

    // ===============================================
    // APPROVAL WORKFLOW MANAGEMENT
    // ===============================================

    @GetMapping("/by-approval-status/{approvalStatus}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByApprovalStatus(@PathVariable String approvalStatus, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByApprovalStatus(approvalStatus, pageable);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ExpenseDTO> approveExpense(
            @PathVariable Long id,
            @RequestParam String approvedBy,
            @RequestParam(required = false) String approvalRemarks) {
        ExpenseDTO updatedExpense = expenseService.approveExpense(id, approvedBy, approvalRemarks);
        return ResponseEntity.ok(updatedExpense);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ExpenseDTO> rejectExpense(
            @PathVariable Long id,
            @RequestParam String rejectedBy,
            @RequestParam String rejectionReason) {
        ExpenseDTO updatedExpense = expenseService.rejectExpense(id, rejectedBy, rejectionReason);
        return ResponseEntity.ok(updatedExpense);
    }

    @PutMapping("/{id}/submit-for-approval")
    public ResponseEntity<ExpenseDTO> submitForApproval(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks) {
        ExpenseDTO updatedExpense = expenseService.submitForApproval(id, remarks);
        return ResponseEntity.ok(updatedExpense);
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<Page<ExpenseDTO>> getPendingApprovalExpenses(Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getPendingApprovalExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================

    @GetMapping("/by-payment-status/{paymentStatus}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByPaymentStatus(@PathVariable String paymentStatus, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByPaymentStatus(paymentStatus, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/overdue-payments")
    public ResponseEntity<Page<ExpenseDTO>> getOverdueExpenses(Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getOverdueExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<ExpenseDTO> recordPayment(
            @PathVariable Long id,
            @RequestParam String paymentAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String remarks) {
        ExpenseDTO updatedExpense = expenseService.recordPayment(id, paymentAmount, paymentDate, 
                                                                paymentMethod, referenceNumber, remarks);
        return ResponseEntity.ok(updatedExpense);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<ExpenseDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus,
            @RequestParam(required = false) String remarks) {
        ExpenseDTO updatedExpense = expenseService.updatePaymentStatus(id, paymentStatus, remarks);
        return ResponseEntity.ok(updatedExpense);
    }

    @GetMapping("/total-pending-amount")
    public ResponseEntity<String> getTotalPendingAmount() {
        String totalAmount = expenseService.getTotalPendingAmount();
        return ResponseEntity.ok(totalAmount);
    }

    // ===============================================
    // REIMBURSEMENT MANAGEMENT
    // ===============================================

    @GetMapping("/by-reimbursement-status/{reimbursementStatus}")
    public ResponseEntity<Page<ExpenseDTO>> getExpensesByReimbursementStatus(@PathVariable String reimbursementStatus, Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getExpensesByReimbursementStatus(reimbursementStatus, pageable);
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}/reimbursement")
    public ResponseEntity<ExpenseDTO> processReimbursement(
            @PathVariable Long id,
            @RequestParam String reimbursementAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reimbursementDate,
            @RequestParam(required = false) String remarks) {
        ExpenseDTO updatedExpense = expenseService.processReimbursement(id, reimbursementAmount, reimbursementDate, remarks);
        return ResponseEntity.ok(updatedExpense);
    }

    @GetMapping("/total-reimbursement-amount")
    public ResponseEntity<String> getTotalReimbursementAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String totalAmount = expenseService.getTotalReimbursementAmount(startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }

    // ===============================================
    // RECURRING EXPENSES
    // ===============================================

    @GetMapping("/recurring")
    public ResponseEntity<Page<ExpenseDTO>> getRecurringExpenses(Pageable pageable) {
        Page<ExpenseDTO> expenses = expenseService.getRecurringExpenses(pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/recurring-due")
    public ResponseEntity<List<ExpenseDTO>> getRecurringExpensesDue() {
        List<ExpenseDTO> expenses = expenseService.getRecurringExpensesDue();
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}/recurring")
    public ResponseEntity<ExpenseDTO> updateRecurringSettings(
            @PathVariable Long id,
            @RequestParam Boolean isRecurring,
            @RequestParam(required = false) String recurringFrequency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextRecurringDate) {
        ExpenseDTO updatedExpense = expenseService.updateRecurringSettings(id, isRecurring, recurringFrequency, nextRecurringDate);
        return ResponseEntity.ok(updatedExpense);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getExpenseStatistics() {
        Object[] statistics = expenseService.getExpenseStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/by-type-report")
    public ResponseEntity<List<Object[]>> getExpenseByTypeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = expenseService.getExpenseByTypeReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/by-category-report")
    public ResponseEntity<List<Object[]>> getExpenseByCategoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = expenseService.getExpenseByCategoryReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/truck-wise-report")
    public ResponseEntity<List<Object[]>> getTruckWiseExpenseReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = expenseService.getTruckWiseExpenseReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/driver-wise-report")
    public ResponseEntity<List<Object[]>> getDriverWiseExpenseReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = expenseService.getDriverWiseExpenseReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/vendor-wise-report")
    public ResponseEntity<List<Object[]>> getVendorWiseExpenseReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = expenseService.getVendorWiseExpenseReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<List<Object[]>> getMonthlyExpenseSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = expenseService.getMonthlyExpenseSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/approval-summary")
    public ResponseEntity<List<Object[]>> getApprovalSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = expenseService.getApprovalSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/payment-summary")
    public ResponseEntity<List<Object[]>> getPaymentSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = expenseService.getPaymentSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/total-expenses")
    public ResponseEntity<String> getTotalExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String totalExpenses = expenseService.getTotalExpenses(startDate, endDate);
        return ResponseEntity.ok(totalExpenses);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<ExpenseDTO>> generateExpenseReport(
            @RequestParam(required = false) String expenseType,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ExpenseDTO> expenses = expenseService.generateExpenseReport(expenseType, paymentStatus, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-expense-number/{expenseNumber}")
    public ResponseEntity<Boolean> validateExpenseNumber(
            @PathVariable String expenseNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = expenseService.isExpenseNumberUnique(expenseNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-expense-number")
    public ResponseEntity<String> generateExpenseNumber() {
        String expenseNumber = expenseService.generateExpenseNumber();
        return ResponseEntity.ok(expenseNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getExpenseCount(
            @RequestParam(required = false) String expenseType,
            @RequestParam(required = false) String paymentStatus) {
        long count = expenseService.getExpenseCount(expenseType, paymentStatus);
        return ResponseEntity.ok(count);
    }
}

