package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.IncomeDTO;
import com.shivshakti.stms.entity.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Income management
 * Defines business operations for income-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface IncomeService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    IncomeDTO createIncome(IncomeDTO incomeDTO);
    IncomeDTO updateIncome(Long id, IncomeDTO incomeDTO);
    Optional<IncomeDTO> getIncomeById(Long id);
    Page<IncomeDTO> getAllIncomes(Pageable pageable);
    void deleteIncome(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    Page<IncomeDTO> searchIncomes(String incomeNumber, String incomeType, String incomeCategory,
                                 String paymentStatus, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    Optional<IncomeDTO> findByIncomeNumber(String incomeNumber);
    Page<IncomeDTO> getIncomesByType(String incomeType, Pageable pageable);
    Page<IncomeDTO> getIncomesByCategory(String incomeCategory, Pageable pageable);
    Page<IncomeDTO> getIncomesByTrip(Long tripId, Pageable pageable);
    Page<IncomeDTO> getIncomesByClient(Long clientId, Pageable pageable);
    Page<IncomeDTO> getIncomesByBuilty(Long builtyId, Pageable pageable);

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================
    
    IncomeDTO updatePaymentStatus(Long id, String paymentStatus, LocalDate paymentDate, String remarks);
    IncomeDTO recordPayment(Long id, BigDecimal receivedAmount, LocalDate paymentDate, 
                           String paymentMethod, String referenceNumber);
    IncomeDTO markAsReceived(Long id, LocalDate paymentDate, String paymentMethod, String referenceNumber);
    Page<IncomeDTO> getPendingPayments(Pageable pageable);
    Page<IncomeDTO> getOverdueIncomes(Pageable pageable);
    Page<IncomeDTO> getPartiallyReceivedIncomes(Pageable pageable);
    BigDecimal calculateTotalPendingAmount();
    BigDecimal calculateTotalReceivedAmount(LocalDate startDate, LocalDate endDate);
    List<Object[]> getPaymentSummary(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // RECURRING INCOMES
    // ===============================================
    
    IncomeDTO createRecurringIncome(IncomeDTO incomeDTO, String frequency, LocalDate nextDate);
    List<IncomeDTO> generateRecurringIncomes(LocalDate forDate);
    Page<IncomeDTO> getRecurringIncomes(Pageable pageable);
    IncomeDTO updateRecurringSchedule(Long id, String frequency, LocalDate nextDate);

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================
    
    Object[] getIncomeStatistics();
    List<Object[]> getMonthlyIncomeSummary(LocalDate startDate, LocalDate endDate);
    List<Object[]> getIncomeByTypeReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getIncomeByCategoryReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getClientWiseIncomeReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getTripWiseIncomeReport(LocalDate startDate, LocalDate endDate);
    BigDecimal calculateTotalIncome(LocalDate startDate, LocalDate endDate);
    List<IncomeDTO> generateIncomeReport(LocalDate startDate, LocalDate endDate, String incomeType, String status);
    List<Object[]> getCashFlowReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getAgingReport();

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    void validateIncomeForCreation(IncomeDTO incomeDTO);
    void validateIncomeForUpdate(Long id, IncomeDTO incomeDTO);
    boolean isIncomeNumberUnique(String incomeNumber, Long excludeId);
    boolean canDeleteIncome(Long id);
    boolean validatePaymentAmount(Long incomeId, BigDecimal paymentAmount);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    IncomeDTO convertToDTO(Income income);
    Income convertToEntity(IncomeDTO incomeDTO);
    String generateIncomeNumber();
    LocalDate calculateNextRecurringDate(LocalDate currentDate, String frequency);
    long getIncomeCount(String incomeType, String status);
}

