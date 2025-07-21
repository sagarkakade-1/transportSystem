package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.ExpenseDTO;
import com.shivshakti.stms.entity.Expense;
import com.shivshakti.stms.entity.Trip;
import com.shivshakti.stms.entity.Truck;
import com.shivshakti.stms.entity.Driver;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.exception.DuplicateResourceException;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.repository.ExpenseRepository;
import com.shivshakti.stms.repository.TripRepository;
import com.shivshakti.stms.repository.TruckRepository;
import com.shivshakti.stms.repository.DriverRepository;
import com.shivshakti.stms.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService interface
 * Provides comprehensive expense management functionality with business logic
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseServiceImpl.class);
    
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    
    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, TripRepository tripRepository,
                             TruckRepository truckRepository, DriverRepository driverRepository) {
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
        this.truckRepository = truckRepository;
        this.driverRepository = driverRepository;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    @Override
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        logger.info("Creating new expense: {}", expenseDTO.getExpenseNumber());
        
        validateExpenseForCreation(expenseDTO);
        
        if (!StringUtils.hasText(expenseDTO.getExpenseNumber())) {
            expenseDTO.setExpenseNumber(generateExpenseNumber());
        }
        
        Expense expense = convertToEntity(expenseDTO);
        Expense savedExpense = expenseRepository.save(expense);
        
        logger.info("Successfully created expense with ID: {}", savedExpense.getId());
        return convertToDTO(savedExpense);
    }
    
    @Override
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        logger.info("Updating expense with ID: {}", id);
        
        validateExpenseForUpdate(id, expenseDTO);
        
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        if ("APPROVED".equals(existingExpense.getApprovalStatus()) && "PAID".equals(existingExpense.getPaymentStatus())) {
            throw new BusinessValidationException("Cannot update approved and paid expense");
        }
        
        updateExpenseFields(existingExpense, expenseDTO);
        Expense updatedExpense = expenseRepository.save(existingExpense);
        
        logger.info("Successfully updated expense with ID: {}", id);
        return convertToDTO(updatedExpense);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseDTO> getExpenseById(Long id) {
        return expenseRepository.findById(id).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getAllExpenses(Pageable pageable) {
        return expenseRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    @Override
    public void deleteExpense(Long id) {
        if (!canDeleteExpense(id)) {
            throw new BusinessValidationException("Cannot delete approved or paid expense");
        }
        expenseRepository.deleteById(id);
        logger.info("Successfully deleted expense with ID: {}", id);
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> searchExpenses(String expenseNumber, String expenseType, String expenseCategory,
                                          String paymentStatus, String approvalStatus, LocalDate startDate, 
                                          LocalDate endDate, Pageable pageable) {
        return expenseRepository.searchExpenses(expenseNumber, expenseType, expenseCategory, 
                                               paymentStatus, approvalStatus, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ExpenseDTO> findByExpenseNumber(String expenseNumber) {
        return expenseRepository.findByExpenseNumber(expenseNumber).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByType(String expenseType, Pageable pageable) {
        return expenseRepository.findByExpenseType(expenseType, pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByCategory(String expenseCategory, Pageable pageable) {
        return expenseRepository.findByExpenseCategory(expenseCategory, pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByTrip(Long tripId, Pageable pageable) {
        return expenseRepository.findByTripId(tripId, pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByTruck(Long truckId, Pageable pageable) {
        return expenseRepository.findByTruckId(truckId, pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByDriver(Long driverId, Pageable pageable) {
        return expenseRepository.findByDriverId(driverId, pageable).map(this::convertToDTO);
    }

    // ===============================================
    // APPROVAL WORKFLOW
    // ===============================================
    
    @Override
    public ExpenseDTO approveExpense(Long id, Long approverId, String remarks) {
        logger.info("Approving expense ID: {} by approver: {}", id, approverId);
        
        if (!canApproveExpense(id, approverId)) {
            throw new BusinessValidationException("Cannot approve this expense");
        }
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setApprovalStatus("APPROVED");
        expense.setApprovedBy(approverId);
        expense.setApprovalDate(LocalDate.now());
        expense.setApprovalRemarks(remarks);
        
        addToRemarks(expense, "Expense approved on " + LocalDate.now() + " - " + remarks);
        
        Expense updatedExpense = expenseRepository.save(expense);
        logger.info("Successfully approved expense ID: {}", id);
        return convertToDTO(updatedExpense);
    }
    
    @Override
    public ExpenseDTO rejectExpense(Long id, Long approverId, String remarks) {
        logger.info("Rejecting expense ID: {} by approver: {}", id, approverId);
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setApprovalStatus("REJECTED");
        expense.setApprovedBy(approverId);
        expense.setApprovalDate(LocalDate.now());
        expense.setApprovalRemarks(remarks);
        
        addToRemarks(expense, "Expense rejected on " + LocalDate.now() + " - " + remarks);
        
        Expense updatedExpense = expenseRepository.save(expense);
        logger.info("Successfully rejected expense ID: {}", id);
        return convertToDTO(updatedExpense);
    }
    
    @Override
    public ExpenseDTO submitForReview(Long id, String remarks) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setApprovalStatus("UNDER_REVIEW");
        addToRemarks(expense, "Submitted for review on " + LocalDate.now() + " - " + remarks);
        
        return convertToDTO(expenseRepository.save(expense));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getPendingApprovals(Pageable pageable) {
        return expenseRepository.findByApprovalStatus("PENDING", pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getApprovedExpenses(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return expenseRepository.findApprovedExpenses(startDate, endDate, pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getRejectedExpenses(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return expenseRepository.findRejectedExpenses(startDate, endDate, pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getApprovalSummary(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getApprovalSummary(startDate, endDate);
    }

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================
    
    @Override
    public ExpenseDTO updatePaymentStatus(Long id, String paymentStatus, LocalDate paymentDate, String remarks) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setPaymentStatus(paymentStatus);
        expense.setPaymentDate(paymentDate);
        addToRemarks(expense, "Payment status updated to " + paymentStatus + " on " + LocalDate.now() + " - " + remarks);
        
        return convertToDTO(expenseRepository.save(expense));
    }
    
    @Override
    public ExpenseDTO markAsPaid(Long id, LocalDate paymentDate, String paymentMethod, String referenceNumber) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setPaymentStatus("PAID");
        expense.setPaymentDate(paymentDate);
        expense.setPaymentMethod(paymentMethod);
        expense.setReferenceNumber(referenceNumber);
        
        addToRemarks(expense, "Payment completed on " + paymentDate + " via " + paymentMethod + " - Ref: " + referenceNumber);
        
        return convertToDTO(expenseRepository.save(expense));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getPendingPayments(Pageable pageable) {
        return expenseRepository.findByPaymentStatus("PENDING", pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getOverdueExpenses(Pageable pageable) {
        return expenseRepository.findOverdueExpenses(pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalPendingAmount() {
        return expenseRepository.calculateTotalPendingAmount();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPaymentSummary(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getPaymentSummary(startDate, endDate);
    }

    // ===============================================
    // REIMBURSEMENT MANAGEMENT
    // ===============================================
    
    @Override
    public ExpenseDTO processReimbursement(Long id, BigDecimal reimbursementAmount, String remarks) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setReimbursementStatus("PROCESSED");
        expense.setReimbursementAmount(reimbursementAmount);
        addToRemarks(expense, "Reimbursement processed: " + reimbursementAmount + " on " + LocalDate.now() + " - " + remarks);
        
        return convertToDTO(expenseRepository.save(expense));
    }
    
    @Override
    public ExpenseDTO rejectReimbursement(Long id, String remarks) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setReimbursementStatus("REJECTED");
        addToRemarks(expense, "Reimbursement rejected on " + LocalDate.now() + " - " + remarks);
        
        return convertToDTO(expenseRepository.save(expense));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getPendingReimbursements(Pageable pageable) {
        return expenseRepository.findByReimbursementStatus("PENDING", pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getProcessedReimbursements(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return expenseRepository.findProcessedReimbursements(startDate, endDate, pageable).map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalReimbursementAmount(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.calculateTotalReimbursementAmount(startDate, endDate);
    }

    // ===============================================
    // RECURRING EXPENSES
    // ===============================================
    
    @Override
    public ExpenseDTO createRecurringExpense(ExpenseDTO expenseDTO, String frequency, LocalDate nextDate) {
        expenseDTO.setIsRecurring(true);
        expenseDTO.setRecurringFrequency(frequency);
        expenseDTO.setNextRecurringDate(nextDate);
        return createExpense(expenseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> generateRecurringExpenses(LocalDate forDate) {
        return expenseRepository.findRecurringExpensesDue(forDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getRecurringExpenses(Pageable pageable) {
        return expenseRepository.findByIsRecurring(true, pageable).map(this::convertToDTO);
    }
    
    @Override
    public ExpenseDTO updateRecurringSchedule(Long id, String frequency, LocalDate nextDate) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        
        expense.setRecurringFrequency(frequency);
        expense.setNextRecurringDate(nextDate);
        
        return convertToDTO(expenseRepository.save(expense));
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getExpenseStatistics() {
        return expenseRepository.getExpenseStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyExpenseSummary(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getMonthlyExpenseSummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getExpenseByTypeReport(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getExpenseByTypeReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getExpenseByCategoryReport(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getExpenseByCategoryReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTruckWiseExpenseReport(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getTruckWiseExpenseReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDriverWiseExpenseReport(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getDriverWiseExpenseReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getVendorWiseExpenseReport(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.getVendorWiseExpenseReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalExpenses(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.calculateTotalExpenses(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> generateExpenseReport(LocalDate startDate, LocalDate endDate, String expenseType, String status) {
        return expenseRepository.findExpensesForReport(startDate, endDate, expenseType, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    @Override
    public void validateExpenseForCreation(ExpenseDTO expenseDTO) {
        if (StringUtils.hasText(expenseDTO.getExpenseNumber()) && 
            !isExpenseNumberUnique(expenseDTO.getExpenseNumber(), null)) {
            throw new DuplicateResourceException("Expense", "expense number", expenseDTO.getExpenseNumber());
        }
        
        if (expenseDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Expense amount must be greater than zero");
        }
        
        if (expenseDTO.getExpenseDate().isAfter(LocalDate.now())) {
            throw new BusinessValidationException("Expense date cannot be in the future");
        }
    }
    
    @Override
    public void validateExpenseForUpdate(Long id, ExpenseDTO expenseDTO) {
        if (StringUtils.hasText(expenseDTO.getExpenseNumber()) && 
            !isExpenseNumberUnique(expenseDTO.getExpenseNumber(), id)) {
            throw new DuplicateResourceException("Expense", "expense number", expenseDTO.getExpenseNumber());
        }
        validateExpenseForCreation(expenseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isExpenseNumberUnique(String expenseNumber, Long excludeId) {
        if (excludeId != null) {
            return !expenseRepository.existsByExpenseNumberAndIdNot(expenseNumber, excludeId);
        }
        return expenseRepository.findByExpenseNumber(expenseNumber).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canDeleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        return expense != null && !"APPROVED".equals(expense.getApprovalStatus()) && !"PAID".equals(expense.getPaymentStatus());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canApproveExpense(Long id, Long approverId) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        return expense != null && "PENDING".equals(expense.getApprovalStatus()) && approverId != null;
    }

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    @Override
    public ExpenseDTO convertToDTO(Expense expense) {
        if (expense == null) return null;
        
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setExpenseNumber(expense.getExpenseNumber());
        dto.setExpenseType(expense.getExpenseType());
        dto.setExpenseCategory(expense.getExpenseCategory());
        dto.setAmount(expense.getAmount());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setDescription(expense.getDescription());
        dto.setTripId(expense.getTrip() != null ? expense.getTrip().getId() : null);
        dto.setTruckId(expense.getTruck() != null ? expense.getTruck().getId() : null);
        dto.setDriverId(expense.getDriver() != null ? expense.getDriver().getId() : null);
        dto.setVendorName(expense.getVendorName());
        dto.setInvoiceNumber(expense.getInvoiceNumber());
        dto.setInvoiceDate(expense.getInvoiceDate());
        dto.setGstAmount(expense.getGstAmount());
        dto.setDiscountAmount(expense.getDiscountAmount());
        dto.setPaymentMethod(expense.getPaymentMethod());
        dto.setPaymentStatus(expense.getPaymentStatus());
        dto.setPaymentDate(expense.getPaymentDate());
        dto.setReferenceNumber(expense.getReferenceNumber());
        dto.setApprovalStatus(expense.getApprovalStatus());
        dto.setApprovedBy(expense.getApprovedBy());
        dto.setApprovalDate(expense.getApprovalDate());
        dto.setApprovalRemarks(expense.getApprovalRemarks());
        dto.setReimbursementStatus(expense.getReimbursementStatus());
        dto.setReimbursementAmount(expense.getReimbursementAmount());
        dto.setRemarks(expense.getRemarks());
        dto.setReceiptPath(expense.getReceiptPath());
        dto.setIsRecurring(expense.getIsRecurring());
        dto.setRecurringFrequency(expense.getRecurringFrequency());
        dto.setNextRecurringDate(expense.getNextRecurringDate());
        
        // Set related entity information
        if (expense.getTrip() != null) {
            dto.setTripNumber(expense.getTrip().getTripNumber());
        }
        if (expense.getTruck() != null) {
            dto.setTruckNumber(expense.getTruck().getTruckNumber());
        }
        if (expense.getDriver() != null) {
            dto.setDriverName(expense.getDriver().getName());
        }
        
        // Calculate derived fields
        dto.setTotalAmount(dto.calculateTotalAmount());
        dto.setNetAmount(dto.calculateNetAmount());
        
        return dto;
    }
    
    @Override
    public Expense convertToEntity(ExpenseDTO expenseDTO) {
        if (expenseDTO == null) return null;
        
        Expense expense = new Expense();
        expense.setId(expenseDTO.getId());
        expense.setExpenseNumber(expenseDTO.getExpenseNumber());
        expense.setExpenseType(expenseDTO.getExpenseType());
        expense.setExpenseCategory(expenseDTO.getExpenseCategory());
        expense.setAmount(expenseDTO.getAmount());
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setDescription(expenseDTO.getDescription());
        expense.setVendorName(expenseDTO.getVendorName());
        expense.setInvoiceNumber(expenseDTO.getInvoiceNumber());
        expense.setInvoiceDate(expenseDTO.getInvoiceDate());
        expense.setGstAmount(expenseDTO.getGstAmount());
        expense.setDiscountAmount(expenseDTO.getDiscountAmount());
        expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        expense.setPaymentStatus(expenseDTO.getPaymentStatus());
        expense.setPaymentDate(expenseDTO.getPaymentDate());
        expense.setReferenceNumber(expenseDTO.getReferenceNumber());
        expense.setApprovalStatus(expenseDTO.getApprovalStatus());
        expense.setApprovedBy(expenseDTO.getApprovedBy());
        expense.setApprovalDate(expenseDTO.getApprovalDate());
        expense.setApprovalRemarks(expenseDTO.getApprovalRemarks());
        expense.setReimbursementStatus(expenseDTO.getReimbursementStatus());
        expense.setReimbursementAmount(expenseDTO.getReimbursementAmount());
        expense.setRemarks(expenseDTO.getRemarks());
        expense.setReceiptPath(expenseDTO.getReceiptPath());
        expense.setIsRecurring(expenseDTO.getIsRecurring());
        expense.setRecurringFrequency(expenseDTO.getRecurringFrequency());
        expense.setNextRecurringDate(expenseDTO.getNextRecurringDate());
        
        // Set related entities
        if (expenseDTO.getTripId() != null) {
            Trip trip = tripRepository.findById(expenseDTO.getTripId()).orElse(null);
            expense.setTrip(trip);
        }
        if (expenseDTO.getTruckId() != null) {
            Truck truck = truckRepository.findById(expenseDTO.getTruckId()).orElse(null);
            expense.setTruck(truck);
        }
        if (expenseDTO.getDriverId() != null) {
            Driver driver = driverRepository.findById(expenseDTO.getDriverId()).orElse(null);
            expense.setDriver(driver);
        }
        
        return expense;
    }
    
    @Override
    public String generateExpenseNumber() {
        String prefix = "EX";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String lastExpenseNumber = expenseRepository.findLastExpenseNumberForDate(datePart);
        
        int sequence = 1;
        if (lastExpenseNumber != null && lastExpenseNumber.length() > 10) {
            try {
                sequence = Integer.parseInt(lastExpenseNumber.substring(10)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Could not parse sequence from expense number: {}", lastExpenseNumber);
            }
        }
        
        return String.format("%s%s%04d", prefix, datePart, sequence);
    }
    
    @Override
    public LocalDate calculateNextRecurringDate(LocalDate currentDate, String frequency) {
        switch (frequency.toUpperCase()) {
            case "MONTHLY": return currentDate.plusMonths(1);
            case "QUARTERLY": return currentDate.plusMonths(3);
            case "YEARLY": return currentDate.plusYears(1);
            default: return currentDate.plusMonths(1);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getExpenseCount(String expenseType, String status) {
        return expenseRepository.countByExpenseTypeAndPaymentStatus(expenseType, status);
    }
    
    // ===============================================
    // PRIVATE HELPER METHODS
    // ===============================================
    
    private void updateExpenseFields(Expense existingExpense, ExpenseDTO expenseDTO) {
        existingExpense.setExpenseNumber(expenseDTO.getExpenseNumber());
        existingExpense.setExpenseType(expenseDTO.getExpenseType());
        existingExpense.setExpenseCategory(expenseDTO.getExpenseCategory());
        existingExpense.setAmount(expenseDTO.getAmount());
        existingExpense.setExpenseDate(expenseDTO.getExpenseDate());
        existingExpense.setDescription(expenseDTO.getDescription());
        existingExpense.setVendorName(expenseDTO.getVendorName());
        existingExpense.setInvoiceNumber(expenseDTO.getInvoiceNumber());
        existingExpense.setInvoiceDate(expenseDTO.getInvoiceDate());
        existingExpense.setGstAmount(expenseDTO.getGstAmount());
        existingExpense.setDiscountAmount(expenseDTO.getDiscountAmount());
        existingExpense.setPaymentMethod(expenseDTO.getPaymentMethod());
        existingExpense.setReferenceNumber(expenseDTO.getReferenceNumber());
        existingExpense.setReceiptPath(expenseDTO.getReceiptPath());
    }
    
    private void addToRemarks(Expense expense, String newRemark) {
        String currentRemarks = expense.getRemarks() != null ? expense.getRemarks() : "";
        expense.setRemarks(currentRemarks + "\n" + newRemark);
    }
}

