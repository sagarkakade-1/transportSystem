package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.IncomeDTO;
import com.shivshakti.stms.entity.Income;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.repository.IncomeRepository;
import com.shivshakti.stms.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for Income management
 * Provides comprehensive business logic for income operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeServiceImpl(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public IncomeDTO createIncome(IncomeDTO incomeDTO) {
        validateIncomeData(incomeDTO);
        
        if (incomeDTO.getIncomeNumber() == null || incomeDTO.getIncomeNumber().isEmpty()) {
            incomeDTO.setIncomeNumber(generateIncomeNumber());
        }
        
        Income income = convertToEntity(incomeDTO);
        income.setPaymentStatus("PENDING");
        income.setCreatedDate(LocalDate.now());
        
        // Calculate net amount
        calculateNetAmount(income);
        
        Income savedIncome = incomeRepository.save(income);
        return convertToDTO(savedIncome);
    }

    @Override
    public IncomeDTO updateIncome(Long id, IncomeDTO incomeDTO) {
        Income existingIncome = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        
        validateIncomeData(incomeDTO);
        updateIncomeFields(existingIncome, incomeDTO);
        existingIncome.setModifiedDate(LocalDate.now());
        
        // Recalculate net amount
        calculateNetAmount(existingIncome);
        
        Income updatedIncome = incomeRepository.save(existingIncome);
        return convertToDTO(updatedIncome);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IncomeDTO> getIncomeById(Long id) {
        return incomeRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getAllIncomes(Pageable pageable) {
        return incomeRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public void deleteIncome(Long id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        
        if ("RECEIVED".equals(income.getPaymentStatus())) {
            throw new BusinessValidationException("Cannot delete received income");
        }
        
        incomeRepository.delete(income);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> searchIncomes(String incomeNumber, String incomeType, String incomeCategory,
                                        String paymentStatus, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return incomeRepository.searchIncomes(incomeNumber, incomeType, incomeCategory, 
                                             paymentStatus, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IncomeDTO> findByIncomeNumber(String incomeNumber) {
        return incomeRepository.findByIncomeNumber(incomeNumber)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getIncomesByType(String incomeType, Pageable pageable) {
        return incomeRepository.findByIncomeType(incomeType, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getIncomesByCategory(String incomeCategory, Pageable pageable) {
        return incomeRepository.findByIncomeCategory(incomeCategory, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getIncomesByTrip(Long tripId, Pageable pageable) {
        return incomeRepository.findByTripId(tripId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getIncomesByClient(Long clientId, Pageable pageable) {
        return incomeRepository.findByClientId(clientId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getIncomesByBuilty(Long builtyId, Pageable pageable) {
        return incomeRepository.findByBuiltyId(builtyId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getIncomesByPaymentStatus(String paymentStatus, Pageable pageable) {
        return incomeRepository.findByPaymentStatus(paymentStatus, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getOverdueIncomes(Pageable pageable) {
        return incomeRepository.findOverdueIncomes(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getPartiallyReceivedIncomes(Pageable pageable) {
        return incomeRepository.findPartiallyReceivedIncomes(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public IncomeDTO recordPayment(Long id, String receivedAmount, LocalDate paymentDate,
                                  String paymentMethod, String referenceNumber, String remarks) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        
        BigDecimal receivedAmt = new BigDecimal(receivedAmount);
        BigDecimal currentReceived = income.getReceivedAmount() != null ? income.getReceivedAmount() : BigDecimal.ZERO;
        BigDecimal totalReceived = currentReceived.add(receivedAmt);
        BigDecimal totalAmount = income.getAmount().add(income.getGstAmount()).subtract(income.getTdsAmount());
        
        income.setReceivedAmount(totalReceived);
        income.setPaymentDate(paymentDate);
        income.setPaymentMethod(paymentMethod);
        income.setReferenceNumber(referenceNumber);
        
        // Update payment status
        if (totalReceived.compareTo(totalAmount) >= 0) {
            income.setPaymentStatus("RECEIVED");
        } else if (totalReceived.compareTo(BigDecimal.ZERO) > 0) {
            income.setPaymentStatus("PARTIALLY_RECEIVED");
        }
        
        if (remarks != null) {
            income.setRemarks(remarks);
        }
        income.setModifiedDate(LocalDate.now());
        
        Income updatedIncome = incomeRepository.save(income);
        return convertToDTO(updatedIncome);
    }

    @Override
    public IncomeDTO updatePaymentStatus(Long id, String paymentStatus, String remarks) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        
        income.setPaymentStatus(paymentStatus);
        if (remarks != null) {
            income.setRemarks(remarks);
        }
        income.setModifiedDate(LocalDate.now());
        
        Income updatedIncome = incomeRepository.save(income);
        return convertToDTO(updatedIncome);
    }

    @Override
    @Transactional(readOnly = true)
    public String getTotalPendingAmount() {
        BigDecimal total = incomeRepository.calculateTotalPendingAmount();
        return total != null ? total.toString() : "0.00";
    }

    @Override
    @Transactional(readOnly = true)
    public String getTotalReceivedAmount(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = incomeRepository.calculateTotalReceivedAmount(startDate, endDate);
        return total != null ? total.toString() : "0.00";
    }

    @Override
    public IncomeDTO updateTaxDetails(Long id, String gstAmount, String tdsAmount, String remarks) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        
        if (gstAmount != null) {
            income.setGstAmount(new BigDecimal(gstAmount));
        }
        if (tdsAmount != null) {
            income.setTdsAmount(new BigDecimal(tdsAmount));
        }
        if (remarks != null) {
            income.setRemarks(remarks);
        }
        income.setModifiedDate(LocalDate.now());
        
        // Recalculate net amount
        calculateNetAmount(income);
        
        Income updatedIncome = incomeRepository.save(income);
        return convertToDTO(updatedIncome);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IncomeDTO> getRecurringIncomes(Pageable pageable) {
        return incomeRepository.findByIsRecurring(true, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeDTO> getRecurringIncomesDue() {
        return incomeRepository.findRecurringIncomesDue(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IncomeDTO updateRecurringSettings(Long id, Boolean isRecurring, String recurringFrequency, LocalDate nextRecurringDate) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income", id));
        
        income.setIsRecurring(isRecurring);
        income.setRecurringFrequency(recurringFrequency);
        income.setNextRecurringDate(nextRecurringDate);
        income.setModifiedDate(LocalDate.now());
        
        Income updatedIncome = incomeRepository.save(income);
        return convertToDTO(updatedIncome);
    }

    @Override
    @Transactional(readOnly = true)
    public Object[] getIncomeStatistics() {
        return incomeRepository.getIncomeStatistics();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getIncomeByTypeReport(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.getIncomeByTypeReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getIncomeByCategoryReport(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.getIncomeByCategoryReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientWiseIncomeReport(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.getClientWiseIncomeReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTripWiseIncomeReport(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.getTripWiseIncomeReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyIncomeSummary(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.getMonthlyIncomeSummary(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPaymentSummary(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.getPaymentSummary(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCashFlowReport(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.getCashFlowReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAgingReport() {
        return incomeRepository.getAgingReport();
    }

    @Override
    @Transactional(readOnly = true)
    public String getTotalIncome(LocalDate startDate, LocalDate endDate) {
        BigDecimal total = incomeRepository.calculateTotalIncome(startDate, endDate);
        return total != null ? total.toString() : "0.00";
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeDTO> generateIncomeReport(String incomeType, String paymentStatus, 
                                               LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findIncomesForReport(startDate, endDate, incomeType, paymentStatus).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIncomeNumberUnique(String incomeNumber, Long excludeId) {
        if (excludeId != null) {
            return !incomeRepository.existsByIncomeNumberAndIdNot(incomeNumber, excludeId);
        }
        return incomeRepository.findByIncomeNumber(incomeNumber).isEmpty();
    }

    @Override
    public String generateIncomeNumber() {
        String datePrefix = "IN" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String lastNumber = incomeRepository.findLastIncomeNumberForDate(datePrefix + "%");
        
        int nextNumber = 1;
        if (lastNumber != null) {
            String numberPart = lastNumber.substring(datePrefix.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        
        return datePrefix + String.format("%04d", nextNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public long getIncomeCount(String incomeType, String paymentStatus) {
        return incomeRepository.countByIncomeTypeAndPaymentStatus(incomeType, paymentStatus);
    }

    // Private helper methods
    private void validateIncomeData(IncomeDTO incomeDTO) {
        if (incomeDTO.getIncomeType() == null || incomeDTO.getIncomeType().trim().isEmpty()) {
            throw new BusinessValidationException("Income type is required");
        }
        if (incomeDTO.getAmount() == null || new BigDecimal(incomeDTO.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Amount must be greater than zero");
        }
        if (incomeDTO.getIncomeDate() == null) {
            throw new BusinessValidationException("Income date is required");
        }
    }

    private void calculateNetAmount(Income income) {
        BigDecimal amount = income.getAmount() != null ? income.getAmount() : BigDecimal.ZERO;
        BigDecimal gstAmount = income.getGstAmount() != null ? income.getGstAmount() : BigDecimal.ZERO;
        BigDecimal tdsAmount = income.getTdsAmount() != null ? income.getTdsAmount() : BigDecimal.ZERO;
        
        BigDecimal netAmount = amount.add(gstAmount).subtract(tdsAmount);
        income.setNetAmount(netAmount);
    }

    private Income convertToEntity(IncomeDTO dto) {
        Income income = new Income();
        income.setIncomeNumber(dto.getIncomeNumber());
        income.setIncomeType(dto.getIncomeType());
        income.setIncomeCategory(dto.getIncomeCategory());
        income.setAmount(new BigDecimal(dto.getAmount()));
        income.setGstAmount(dto.getGstAmount() != null ? new BigDecimal(dto.getGstAmount()) : BigDecimal.ZERO);
        income.setTdsAmount(dto.getTdsAmount() != null ? new BigDecimal(dto.getTdsAmount()) : BigDecimal.ZERO);
        income.setIncomeDate(dto.getIncomeDate());
        income.setExpectedDate(dto.getExpectedDate());
        income.setDescription(dto.getDescription());
        income.setRemarks(dto.getRemarks());
        income.setIsRecurring(dto.getIsRecurring() != null ? dto.getIsRecurring() : false);
        income.setRecurringFrequency(dto.getRecurringFrequency());
        income.setNextRecurringDate(dto.getNextRecurringDate());
        // Set relationships based on IDs
        return income;
    }

    private IncomeDTO convertToDTO(Income income) {
        IncomeDTO dto = new IncomeDTO();
        dto.setId(income.getId());
        dto.setIncomeNumber(income.getIncomeNumber());
        dto.setIncomeType(income.getIncomeType());
        dto.setIncomeCategory(income.getIncomeCategory());
        dto.setAmount(income.getAmount().toString());
        dto.setGstAmount(income.getGstAmount() != null ? income.getGstAmount().toString() : null);
        dto.setTdsAmount(income.getTdsAmount() != null ? income.getTdsAmount().toString() : null);
        dto.setNetAmount(income.getNetAmount() != null ? income.getNetAmount().toString() : null);
        dto.setReceivedAmount(income.getReceivedAmount() != null ? income.getReceivedAmount().toString() : null);
        dto.setIncomeDate(income.getIncomeDate());
        dto.setExpectedDate(income.getExpectedDate());
        dto.setPaymentDate(income.getPaymentDate());
        dto.setPaymentStatus(income.getPaymentStatus());
        dto.setPaymentMethod(income.getPaymentMethod());
        dto.setReferenceNumber(income.getReferenceNumber());
        dto.setDescription(income.getDescription());
        dto.setRemarks(income.getRemarks());
        dto.setIsRecurring(income.getIsRecurring());
        dto.setRecurringFrequency(income.getRecurringFrequency());
        dto.setNextRecurringDate(income.getNextRecurringDate());
        dto.setCreatedDate(income.getCreatedDate());
        dto.setModifiedDate(income.getModifiedDate());
        // Set relationship IDs
        if (income.getTrip() != null) {
            dto.setTripId(income.getTrip().getId());
        }
        if (income.getClient() != null) {
            dto.setClientId(income.getClient().getId());
        }
        if (income.getBuilty() != null) {
            dto.setBuiltyId(income.getBuilty().getId());
        }
        return dto;
    }

    private void updateIncomeFields(Income income, IncomeDTO dto) {
        if (dto.getIncomeType() != null) {
            income.setIncomeType(dto.getIncomeType());
        }
        if (dto.getIncomeCategory() != null) {
            income.setIncomeCategory(dto.getIncomeCategory());
        }
        if (dto.getAmount() != null) {
            income.setAmount(new BigDecimal(dto.getAmount()));
        }
        if (dto.getGstAmount() != null) {
            income.setGstAmount(new BigDecimal(dto.getGstAmount()));
        }
        if (dto.getTdsAmount() != null) {
            income.setTdsAmount(new BigDecimal(dto.getTdsAmount()));
        }
        if (dto.getIncomeDate() != null) {
            income.setIncomeDate(dto.getIncomeDate());
        }
        if (dto.getExpectedDate() != null) {
            income.setExpectedDate(dto.getExpectedDate());
        }
        if (dto.getDescription() != null) {
            income.setDescription(dto.getDescription());
        }
        if (dto.getRemarks() != null) {
            income.setRemarks(dto.getRemarks());
        }
    }
}

