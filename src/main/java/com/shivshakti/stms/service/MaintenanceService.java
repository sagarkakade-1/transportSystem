package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.MaintenanceDTO;
import com.shivshakti.stms.entity.Maintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Maintenance management
 * Defines business operations for maintenance-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface MaintenanceService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    MaintenanceDTO createMaintenance(MaintenanceDTO maintenanceDTO);
    MaintenanceDTO updateMaintenance(Long id, MaintenanceDTO maintenanceDTO);
    Optional<MaintenanceDTO> getMaintenanceById(Long id);
    Page<MaintenanceDTO> getAllMaintenances(Pageable pageable);
    void deleteMaintenance(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    Page<MaintenanceDTO> searchMaintenances(String maintenanceNumber, Long truckId, String maintenanceType,
                                           String serviceCategory, String status, String priority,
                                           LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    Optional<MaintenanceDTO> findByMaintenanceNumber(String maintenanceNumber);
    Page<MaintenanceDTO> getMaintenancesByTruck(Long truckId, Pageable pageable);
    Page<MaintenanceDTO> getMaintenancesByType(String maintenanceType, Pageable pageable);
    Page<MaintenanceDTO> getMaintenancesByCategory(String serviceCategory, Pageable pageable);
    Page<MaintenanceDTO> getMaintenancesByStatus(String status, Pageable pageable);
    Page<MaintenanceDTO> getMaintenancesByPriority(String priority, Pageable pageable);

    // ===============================================
    // MAINTENANCE LIFECYCLE
    // ===============================================
    
    MaintenanceDTO scheduleMaintenance(MaintenanceDTO maintenanceDTO);
    MaintenanceDTO startMaintenance(Long id, String serviceProvider, String serviceLocation);
    MaintenanceDTO completeMaintenance(Long id, LocalDate completedDate, String workPerformed, 
                                      String partsReplaced, BigDecimal laborCost, BigDecimal partsCost);
    MaintenanceDTO cancelMaintenance(Long id, String reason);
    MaintenanceDTO markAsOverdue(Long id);

    // ===============================================
    // SCHEDULING AND ALERTS
    // ===============================================
    
    Page<MaintenanceDTO> getScheduledMaintenances(LocalDate fromDate, LocalDate toDate, Pageable pageable);
    Page<MaintenanceDTO> getOverdueMaintenances(Pageable pageable);
    Page<MaintenanceDTO> getUpcomingMaintenances(int days, Pageable pageable);
    List<MaintenanceDTO> generateMaintenanceAlerts(int alertDays);
    MaintenanceDTO rescheduleMaintenanceByDate(Long id, LocalDate newDate, String reason);
    MaintenanceDTO rescheduleMaintenanceByOdometer(Long id, BigDecimal newOdometer, String reason);

    // ===============================================
    // RECURRING MAINTENANCE
    // ===============================================
    
    MaintenanceDTO createRecurringMaintenance(MaintenanceDTO maintenanceDTO, Integer intervalDays, BigDecimal intervalKm);
    List<MaintenanceDTO> generateRecurringMaintenances(LocalDate forDate);
    Page<MaintenanceDTO> getRecurringMaintenances(Pageable pageable);
    MaintenanceDTO updateMaintenanceSchedule(Long id, Integer intervalDays, BigDecimal intervalKm);

    // ===============================================
    // COST MANAGEMENT
    // ===============================================
    
    MaintenanceDTO updateMaintenanceCosts(Long id, BigDecimal laborCost, BigDecimal partsCost, 
                                         BigDecimal otherCharges, BigDecimal gstAmount);
    BigDecimal calculateTotalMaintenanceCost(Long truckId, LocalDate startDate, LocalDate endDate);
    List<Object[]> getMaintenanceCostSummary(LocalDate startDate, LocalDate endDate);
    List<Object[]> getTruckWiseMaintenanceCost(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================
    
    Object[] getMaintenanceStatistics();
    List<Object[]> getMonthlyMaintenanceSummary(LocalDate startDate, LocalDate endDate);
    List<Object[]> getMaintenanceByTypeReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getMaintenanceByCategoryReport(LocalDate startDate, LocalDate endDate);
    List<Object[]> getTruckMaintenanceHistory(Long truckId);
    List<Object[]> getServiceProviderPerformance(LocalDate startDate, LocalDate endDate);
    List<MaintenanceDTO> generateMaintenanceReport(LocalDate startDate, LocalDate endDate, 
                                                   String maintenanceType, String status);
    List<Object[]> getMaintenanceEfficiencyReport(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    void validateMaintenanceForCreation(MaintenanceDTO maintenanceDTO);
    void validateMaintenanceForUpdate(Long id, MaintenanceDTO maintenanceDTO);
    boolean isMaintenanceNumberUnique(String maintenanceNumber, Long excludeId);
    boolean canDeleteMaintenance(Long id);
    boolean canCompleteMaintenance(Long id);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    MaintenanceDTO convertToDTO(Maintenance maintenance);
    Maintenance convertToEntity(MaintenanceDTO maintenanceDTO);
    String generateMaintenanceNumber();
    LocalDate calculateNextServiceDate(LocalDate currentDate, Integer intervalDays);
    BigDecimal calculateNextServiceOdometer(BigDecimal currentOdometer, BigDecimal intervalKm);
    long getMaintenanceCount(String maintenanceType, String status);
}

