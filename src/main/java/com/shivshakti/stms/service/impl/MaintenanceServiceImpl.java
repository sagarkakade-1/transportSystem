package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.MaintenanceDTO;
import com.shivshakti.stms.entity.Maintenance;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.repository.MaintenanceRepository;
import com.shivshakti.stms.service.MaintenanceService;
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
 * Service implementation for Maintenance management
 * Provides comprehensive business logic for maintenance operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    @Autowired
    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public MaintenanceDTO createMaintenance(MaintenanceDTO maintenanceDTO) {
        validateMaintenanceData(maintenanceDTO);
        
        if (maintenanceDTO.getMaintenanceNumber() == null || maintenanceDTO.getMaintenanceNumber().isEmpty()) {
            maintenanceDTO.setMaintenanceNumber(generateMaintenanceNumber());
        }
        
        Maintenance maintenance = convertToEntity(maintenanceDTO);
        maintenance.setStatus("SCHEDULED");
        maintenance.setCreatedDate(LocalDate.now());
        
        // Calculate total cost
        calculateTotalCost(maintenance);
        
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(savedMaintenance);
    }

    @Override
    public MaintenanceDTO updateMaintenance(Long id, MaintenanceDTO maintenanceDTO) {
        Maintenance existingMaintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        validateMaintenanceData(maintenanceDTO);
        updateMaintenanceFields(existingMaintenance, maintenanceDTO);
        existingMaintenance.setModifiedDate(LocalDate.now());
        
        // Recalculate total cost
        calculateTotalCost(existingMaintenance);
        
        Maintenance updatedMaintenance = maintenanceRepository.save(existingMaintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MaintenanceDTO> getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getAllMaintenances(Pageable pageable) {
        return maintenanceRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public void deleteMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        if ("IN_PROGRESS".equals(maintenance.getStatus())) {
            throw new BusinessValidationException("Cannot delete maintenance in progress");
        }
        
        maintenanceRepository.delete(maintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> searchMaintenances(String maintenanceNumber, Long truckId, String maintenanceType,
                                                  String serviceCategory, String status, String priority,
                                                  LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return maintenanceRepository.searchMaintenances(maintenanceNumber, truckId, maintenanceType, 
                                                       serviceCategory, status, priority, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MaintenanceDTO> findByMaintenanceNumber(String maintenanceNumber) {
        return maintenanceRepository.findByMaintenanceNumber(maintenanceNumber)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getMaintenancesByTruck(Long truckId, Pageable pageable) {
        return maintenanceRepository.findByTruckId(truckId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getMaintenancesByType(String maintenanceType, Pageable pageable) {
        return maintenanceRepository.findByMaintenanceType(maintenanceType, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getMaintenancesByCategory(String serviceCategory, Pageable pageable) {
        return maintenanceRepository.findByServiceCategory(serviceCategory, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getMaintenancesByStatus(String status, Pageable pageable) {
        return maintenanceRepository.findByStatus(status, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getMaintenancesByPriority(String priority, Pageable pageable) {
        return maintenanceRepository.findByPriority(priority, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getScheduledMaintenances(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return maintenanceRepository.findScheduledMaintenances(fromDate, toDate, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getOverdueMaintenances(Pageable pageable) {
        return maintenanceRepository.findOverdueMaintenances(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getUpcomingMaintenances(int days, Pageable pageable) {
        LocalDate alertDate = LocalDate.now().plusDays(days);
        return maintenanceRepository.findUpcomingMaintenances(alertDate, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getMaintenancesDueForAlert(int alertDays) {
        LocalDate alertDate = LocalDate.now().plusDays(alertDays);
        return maintenanceRepository.findMaintenancesDueForAlert(alertDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceDTO scheduleMaintenance(Long id, LocalDate scheduledDate, String serviceProvider, String remarks) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        maintenance.setScheduledDate(scheduledDate);
        maintenance.setServiceProvider(serviceProvider);
        maintenance.setStatus("SCHEDULED");
        if (remarks != null) {
            maintenance.setRemarks(remarks);
        }
        maintenance.setModifiedDate(LocalDate.now());
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    public MaintenanceDTO rescheduleMaintenance(Long id, LocalDate newScheduledDate, String rescheduleReason) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        if ("COMPLETED".equals(maintenance.getStatus())) {
            throw new BusinessValidationException("Cannot reschedule completed maintenance");
        }
        
        maintenance.setScheduledDate(newScheduledDate);
        maintenance.setRescheduleReason(rescheduleReason);
        maintenance.setModifiedDate(LocalDate.now());
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    public MaintenanceDTO startMaintenance(Long id, LocalDate actualStartDate, String remarks) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        if (!"SCHEDULED".equals(maintenance.getStatus())) {
            throw new BusinessValidationException("Only scheduled maintenance can be started");
        }
        
        maintenance.setStatus("IN_PROGRESS");
        maintenance.setActualStartDate(actualStartDate != null ? actualStartDate : LocalDate.now());
        if (remarks != null) {
            maintenance.setRemarks(remarks);
        }
        maintenance.setModifiedDate(LocalDate.now());
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    public MaintenanceDTO completeMaintenance(Long id, LocalDate completedDate, String laborCost, String partsCost,
                                             String otherCharges, String workDescription, String remarks) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        if (!"IN_PROGRESS".equals(maintenance.getStatus())) {
            throw new BusinessValidationException("Only in-progress maintenance can be completed");
        }
        
        maintenance.setStatus("COMPLETED");
        maintenance.setCompletedDate(completedDate != null ? completedDate : LocalDate.now());
        
        if (laborCost != null) {
            maintenance.setLaborCost(new BigDecimal(laborCost));
        }
        if (partsCost != null) {
            maintenance.setPartsCost(new BigDecimal(partsCost));
        }
        if (otherCharges != null) {
            maintenance.setOtherCharges(new BigDecimal(otherCharges));
        }
        if (workDescription != null) {
            maintenance.setWorkDescription(workDescription);
        }
        if (remarks != null) {
            maintenance.setRemarks(remarks);
        }
        maintenance.setModifiedDate(LocalDate.now());
        
        // Recalculate total cost
        calculateTotalCost(maintenance);
        
        // Set next service date for recurring maintenance
        if (maintenance.getIsRecurring() != null && maintenance.getIsRecurring()) {
            setNextServiceDate(maintenance);
        }
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    public MaintenanceDTO cancelMaintenance(Long id, String cancellationReason) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        if ("COMPLETED".equals(maintenance.getStatus())) {
            throw new BusinessValidationException("Cannot cancel completed maintenance");
        }
        
        maintenance.setStatus("CANCELLED");
        maintenance.setCancellationReason(cancellationReason);
        maintenance.setModifiedDate(LocalDate.now());
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    public MaintenanceDTO updateMaintenanceStatus(Long id, String status, String remarks) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        validateStatusTransition(maintenance.getStatus(), status);
        
        maintenance.setStatus(status);
        if (remarks != null) {
            maintenance.setRemarks(remarks);
        }
        maintenance.setModifiedDate(LocalDate.now());
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getRecurringMaintenances(Pageable pageable) {
        return maintenanceRepository.findByIsRecurring(true, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getRecurringMaintenancesDue() {
        return maintenanceRepository.findRecurringMaintenancesDue(LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceDTO updateRecurringSettings(Long id, Boolean isRecurring, String recurringFrequency, LocalDate nextServiceDate) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        maintenance.setIsRecurring(isRecurring);
        maintenance.setRecurringFrequency(recurringFrequency);
        maintenance.setNextServiceDate(nextServiceDate);
        maintenance.setModifiedDate(LocalDate.now());
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    public MaintenanceDTO updateMaintenanceCosts(Long id, String laborCost, String partsCost, 
                                                String otherCharges, String gstAmount, String remarks) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        
        if (laborCost != null) {
            maintenance.setLaborCost(new BigDecimal(laborCost));
        }
        if (partsCost != null) {
            maintenance.setPartsCost(new BigDecimal(partsCost));
        }
        if (otherCharges != null) {
            maintenance.setOtherCharges(new BigDecimal(otherCharges));
        }
        if (gstAmount != null) {
            maintenance.setGstAmount(new BigDecimal(gstAmount));
        }
        if (remarks != null) {
            maintenance.setRemarks(remarks);
        }
        maintenance.setModifiedDate(LocalDate.now());
        
        // Recalculate total cost
        calculateTotalCost(maintenance);
        
        Maintenance updatedMaintenance = maintenanceRepository.save(maintenance);
        return convertToDTO(updatedMaintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public String getTotalMaintenanceCost(Long truckId, LocalDate startDate, LocalDate endDate) {
        BigDecimal total = maintenanceRepository.calculateTotalMaintenanceCost(truckId, startDate, endDate);
        return total != null ? total.toString() : "0.00";
    }

    @Override
    @Transactional(readOnly = true)
    public Object[] getMaintenanceStatistics() {
        return maintenanceRepository.getMaintenanceStatistics();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMaintenanceByTypeReport(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.getMaintenanceByTypeReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMaintenanceByCategoryReport(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.getMaintenanceByCategoryReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTruckWiseMaintenanceCost(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.getTruckWiseMaintenanceCost(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyMaintenanceSummary(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.getMonthlyMaintenanceSummary(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMaintenanceCostSummary(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.getMaintenanceCostSummary(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTruckMaintenanceHistory(Long truckId) {
        return maintenanceRepository.getTruckMaintenanceHistory(truckId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getServiceProviderPerformance(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.getServiceProviderPerformance(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMaintenanceEfficiencyReport(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.getMaintenanceEfficiencyReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> generateMaintenanceReport(String maintenanceType, String status, 
                                                         LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findMaintenancesForReport(startDate, endDate, maintenanceType, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMaintenanceNumberUnique(String maintenanceNumber, Long excludeId) {
        if (excludeId != null) {
            return !maintenanceRepository.existsByMaintenanceNumberAndIdNot(maintenanceNumber, excludeId);
        }
        return maintenanceRepository.findByMaintenanceNumber(maintenanceNumber).isEmpty();
    }

    @Override
    public String generateMaintenanceNumber() {
        String datePrefix = "MT" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String lastNumber = maintenanceRepository.findLastMaintenanceNumberForDate(datePrefix + "%");
        
        int nextNumber = 1;
        if (lastNumber != null) {
            String numberPart = lastNumber.substring(datePrefix.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        
        return datePrefix + String.format("%04d", nextNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public long getMaintenanceCount(String maintenanceType, String status) {
        return maintenanceRepository.countByMaintenanceTypeAndStatus(maintenanceType, status);
    }

    // Private helper methods
    private void validateMaintenanceData(MaintenanceDTO maintenanceDTO) {
        if (maintenanceDTO.getMaintenanceType() == null || maintenanceDTO.getMaintenanceType().trim().isEmpty()) {
            throw new BusinessValidationException("Maintenance type is required");
        }
        if (maintenanceDTO.getTruckId() == null) {
            throw new BusinessValidationException("Truck is required");
        }
        if (maintenanceDTO.getScheduledDate() == null) {
            throw new BusinessValidationException("Scheduled date is required");
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        if ("COMPLETED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new BusinessValidationException("Cannot change status of completed or cancelled maintenance");
        }
    }

    private void calculateTotalCost(Maintenance maintenance) {
        BigDecimal laborCost = maintenance.getLaborCost() != null ? maintenance.getLaborCost() : BigDecimal.ZERO;
        BigDecimal partsCost = maintenance.getPartsCost() != null ? maintenance.getPartsCost() : BigDecimal.ZERO;
        BigDecimal otherCharges = maintenance.getOtherCharges() != null ? maintenance.getOtherCharges() : BigDecimal.ZERO;
        BigDecimal gstAmount = maintenance.getGstAmount() != null ? maintenance.getGstAmount() : BigDecimal.ZERO;
        
        BigDecimal totalCost = laborCost.add(partsCost).add(otherCharges).add(gstAmount);
        maintenance.setTotalCost(totalCost);
    }

    private void setNextServiceDate(Maintenance maintenance) {
        if (maintenance.getRecurringFrequency() != null && maintenance.getCompletedDate() != null) {
            LocalDate nextDate = maintenance.getCompletedDate();
            switch (maintenance.getRecurringFrequency().toUpperCase()) {
                case "MONTHLY":
                    nextDate = nextDate.plusMonths(1);
                    break;
                case "QUARTERLY":
                    nextDate = nextDate.plusMonths(3);
                    break;
                case "HALF_YEARLY":
                    nextDate = nextDate.plusMonths(6);
                    break;
                case "YEARLY":
                    nextDate = nextDate.plusYears(1);
                    break;
                default:
                    nextDate = nextDate.plusMonths(3); // Default to quarterly
            }
            maintenance.setNextServiceDate(nextDate);
        }
    }

    private Maintenance convertToEntity(MaintenanceDTO dto) {
        Maintenance maintenance = new Maintenance();
        maintenance.setMaintenanceNumber(dto.getMaintenanceNumber());
        maintenance.setMaintenanceType(dto.getMaintenanceType());
        maintenance.setServiceCategory(dto.getServiceCategory());
        maintenance.setScheduledDate(dto.getScheduledDate());
        maintenance.setPriority(dto.getPriority());
        maintenance.setServiceProvider(dto.getServiceProvider());
        maintenance.setDescription(dto.getDescription());
        maintenance.setRemarks(dto.getRemarks());
        maintenance.setIsRecurring(dto.getIsRecurring() != null ? dto.getIsRecurring() : false);
        maintenance.setRecurringFrequency(dto.getRecurringFrequency());
        maintenance.setNextServiceDate(dto.getNextServiceDate());
        maintenance.setLaborCost(dto.getLaborCost() != null ? new BigDecimal(dto.getLaborCost()) : BigDecimal.ZERO);
        maintenance.setPartsCost(dto.getPartsCost() != null ? new BigDecimal(dto.getPartsCost()) : BigDecimal.ZERO);
        maintenance.setOtherCharges(dto.getOtherCharges() != null ? new BigDecimal(dto.getOtherCharges()) : BigDecimal.ZERO);
        maintenance.setGstAmount(dto.getGstAmount() != null ? new BigDecimal(dto.getGstAmount()) : BigDecimal.ZERO);
        // Set relationships based on IDs
        return maintenance;
    }

    private MaintenanceDTO convertToDTO(Maintenance maintenance) {
        MaintenanceDTO dto = new MaintenanceDTO();
        dto.setId(maintenance.getId());
        dto.setMaintenanceNumber(maintenance.getMaintenanceNumber());
        dto.setMaintenanceType(maintenance.getMaintenanceType());
        dto.setServiceCategory(maintenance.getServiceCategory());
        dto.setScheduledDate(maintenance.getScheduledDate());
        dto.setActualStartDate(maintenance.getActualStartDate());
        dto.setCompletedDate(maintenance.getCompletedDate());
        dto.setPriority(maintenance.getPriority());
        dto.setStatus(maintenance.getStatus());
        dto.setServiceProvider(maintenance.getServiceProvider());
        dto.setDescription(maintenance.getDescription());
        dto.setWorkDescription(maintenance.getWorkDescription());
        dto.setRemarks(maintenance.getRemarks());
        dto.setIsRecurring(maintenance.getIsRecurring());
        dto.setRecurringFrequency(maintenance.getRecurringFrequency());
        dto.setNextServiceDate(maintenance.getNextServiceDate());
        dto.setLaborCost(maintenance.getLaborCost() != null ? maintenance.getLaborCost().toString() : null);
        dto.setPartsCost(maintenance.getPartsCost() != null ? maintenance.getPartsCost().toString() : null);
        dto.setOtherCharges(maintenance.getOtherCharges() != null ? maintenance.getOtherCharges().toString() : null);
        dto.setGstAmount(maintenance.getGstAmount() != null ? maintenance.getGstAmount().toString() : null);
        dto.setTotalCost(maintenance.getTotalCost() != null ? maintenance.getTotalCost().toString() : null);
        dto.setCancellationReason(maintenance.getCancellationReason());
        dto.setRescheduleReason(maintenance.getRescheduleReason());
        dto.setCreatedDate(maintenance.getCreatedDate());
        dto.setModifiedDate(maintenance.getModifiedDate());
        // Set relationship IDs
        if (maintenance.getTruck() != null) {
            dto.setTruckId(maintenance.getTruck().getId());
        }
        return dto;
    }

    private void updateMaintenanceFields(Maintenance maintenance, MaintenanceDTO dto) {
        if (dto.getMaintenanceType() != null) {
            maintenance.setMaintenanceType(dto.getMaintenanceType());
        }
        if (dto.getServiceCategory() != null) {
            maintenance.setServiceCategory(dto.getServiceCategory());
        }
        if (dto.getScheduledDate() != null) {
            maintenance.setScheduledDate(dto.getScheduledDate());
        }
        if (dto.getPriority() != null) {
            maintenance.setPriority(dto.getPriority());
        }
        if (dto.getServiceProvider() != null) {
            maintenance.setServiceProvider(dto.getServiceProvider());
        }
        if (dto.getDescription() != null) {
            maintenance.setDescription(dto.getDescription());
        }
        if (dto.getRemarks() != null) {
            maintenance.setRemarks(dto.getRemarks());
        }
    }
}

