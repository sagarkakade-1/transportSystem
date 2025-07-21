package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.IncomeDTO;
import com.shivshakti.stms.service.IncomeService;
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
 * REST Controller for Income management
 * Provides comprehensive API endpoints for income operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/incomes")
@CrossOrigin(origins = "*")
public class IncomeController {

    private final IncomeService incomeService;

    @Autowired
    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<IncomeDTO> createIncome(@Valid @RequestBody IncomeDTO incomeDTO) {
        IncomeDTO createdIncome = incomeService.createIncome(incomeDTO);
        return new ResponseEntity<>(createdIncome, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeDTO> updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeDTO incomeDTO) {
        IncomeDTO updatedIncome = incomeService.updateIncome(id, incomeDTO);
        return ResponseEntity.ok(updatedIncome);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeDTO> getIncomeById(@PathVariable Long id) {
        Optional<IncomeDTO> income = incomeService.getIncomeById(id);
        return income.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<IncomeDTO>> getAllIncomes(Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getAllIncomes(pageable);
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<IncomeDTO>> searchIncomes(
            @RequestParam(required = false) String incomeNumber,
            @RequestParam(required = false) String incomeType,
            @RequestParam(required = false) String incomeCategory,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.searchIncomes(incomeNumber, incomeType, incomeCategory, 
                                                             paymentStatus, startDate, endDate, pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/by-income-number/{incomeNumber}")
    public ResponseEntity<IncomeDTO> getIncomeByIncomeNumber(@PathVariable String incomeNumber) {
        Optional<IncomeDTO> income = incomeService.findByIncomeNumber(incomeNumber);
        return income.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type/{incomeType}")
    public ResponseEntity<Page<IncomeDTO>> getIncomesByType(@PathVariable String incomeType, Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getIncomesByType(incomeType, pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/by-category/{incomeCategory}")
    public ResponseEntity<Page<IncomeDTO>> getIncomesByCategory(@PathVariable String incomeCategory, Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getIncomesByCategory(incomeCategory, pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/by-trip/{tripId}")
    public ResponseEntity<Page<IncomeDTO>> getIncomesByTrip(@PathVariable Long tripId, Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getIncomesByTrip(tripId, pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Page<IncomeDTO>> getIncomesByClient(@PathVariable Long clientId, Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getIncomesByClient(clientId, pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/by-builty/{builtyId}")
    public ResponseEntity<Page<IncomeDTO>> getIncomesByBuilty(@PathVariable Long builtyId, Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getIncomesByBuilty(builtyId, pageable);
        return ResponseEntity.ok(incomes);
    }

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================

    @GetMapping("/by-payment-status/{paymentStatus}")
    public ResponseEntity<Page<IncomeDTO>> getIncomesByPaymentStatus(@PathVariable String paymentStatus, Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getIncomesByPaymentStatus(paymentStatus, pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<IncomeDTO>> getOverdueIncomes(Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getOverdueIncomes(pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/partially-received")
    public ResponseEntity<Page<IncomeDTO>> getPartiallyReceivedIncomes(Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getPartiallyReceivedIncomes(pageable);
        return ResponseEntity.ok(incomes);
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<IncomeDTO> recordPayment(
            @PathVariable Long id,
            @RequestParam String receivedAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String remarks) {
        IncomeDTO updatedIncome = incomeService.recordPayment(id, receivedAmount, paymentDate, 
                                                             paymentMethod, referenceNumber, remarks);
        return ResponseEntity.ok(updatedIncome);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<IncomeDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus,
            @RequestParam(required = false) String remarks) {
        IncomeDTO updatedIncome = incomeService.updatePaymentStatus(id, paymentStatus, remarks);
        return ResponseEntity.ok(updatedIncome);
    }

    @GetMapping("/total-pending-amount")
    public ResponseEntity<String> getTotalPendingAmount() {
        String totalAmount = incomeService.getTotalPendingAmount();
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/total-received-amount")
    public ResponseEntity<String> getTotalReceivedAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String totalAmount = incomeService.getTotalReceivedAmount(startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }

    // ===============================================
    // TAX MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/tax-details")
    public ResponseEntity<IncomeDTO> updateTaxDetails(
            @PathVariable Long id,
            @RequestParam(required = false) String gstAmount,
            @RequestParam(required = false) String tdsAmount,
            @RequestParam(required = false) String remarks) {
        IncomeDTO updatedIncome = incomeService.updateTaxDetails(id, gstAmount, tdsAmount, remarks);
        return ResponseEntity.ok(updatedIncome);
    }

    // ===============================================
    // RECURRING INCOME
    // ===============================================

    @GetMapping("/recurring")
    public ResponseEntity<Page<IncomeDTO>> getRecurringIncomes(Pageable pageable) {
        Page<IncomeDTO> incomes = incomeService.getRecurringIncomes(pageable);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/recurring-due")
    public ResponseEntity<List<IncomeDTO>> getRecurringIncomesDue() {
        List<IncomeDTO> incomes = incomeService.getRecurringIncomesDue();
        return ResponseEntity.ok(incomes);
    }

    @PutMapping("/{id}/recurring")
    public ResponseEntity<IncomeDTO> updateRecurringSettings(
            @PathVariable Long id,
            @RequestParam Boolean isRecurring,
            @RequestParam(required = false) String recurringFrequency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextRecurringDate) {
        IncomeDTO updatedIncome = incomeService.updateRecurringSettings(id, isRecurring, recurringFrequency, nextRecurringDate);
        return ResponseEntity.ok(updatedIncome);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getIncomeStatistics() {
        Object[] statistics = incomeService.getIncomeStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/by-type-report")
    public ResponseEntity<List<Object[]>> getIncomeByTypeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = incomeService.getIncomeByTypeReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/by-category-report")
    public ResponseEntity<List<Object[]>> getIncomeByCategoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = incomeService.getIncomeByCategoryReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/client-wise-report")
    public ResponseEntity<List<Object[]>> getClientWiseIncomeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = incomeService.getClientWiseIncomeReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/trip-wise-report")
    public ResponseEntity<List<Object[]>> getTripWiseIncomeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = incomeService.getTripWiseIncomeReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<List<Object[]>> getMonthlyIncomeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = incomeService.getMonthlyIncomeSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/payment-summary")
    public ResponseEntity<List<Object[]>> getPaymentSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = incomeService.getPaymentSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/cash-flow-report")
    public ResponseEntity<List<Object[]>> getCashFlowReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = incomeService.getCashFlowReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/aging-report")
    public ResponseEntity<List<Object[]>> getAgingReport() {
        List<Object[]> report = incomeService.getAgingReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/total-income")
    public ResponseEntity<String> getTotalIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String totalIncome = incomeService.getTotalIncome(startDate, endDate);
        return ResponseEntity.ok(totalIncome);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<IncomeDTO>> generateIncomeReport(
            @RequestParam(required = false) String incomeType,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<IncomeDTO> incomes = incomeService.generateIncomeReport(incomeType, paymentStatus, startDate, endDate);
        return ResponseEntity.ok(incomes);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-income-number/{incomeNumber}")
    public ResponseEntity<Boolean> validateIncomeNumber(
            @PathVariable String incomeNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = incomeService.isIncomeNumberUnique(incomeNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-income-number")
    public ResponseEntity<String> generateIncomeNumber() {
        String incomeNumber = incomeService.generateIncomeNumber();
        return ResponseEntity.ok(incomeNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getIncomeCount(
            @RequestParam(required = false) String incomeType,
            @RequestParam(required = false) String paymentStatus) {
        long count = incomeService.getIncomeCount(incomeType, paymentStatus);
        return ResponseEntity.ok(count);
    }
}

