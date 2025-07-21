package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Maintenance;
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
 * Repository interface for Maintenance entity
 * Provides data access operations for maintenance management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    // Basic queries
    Optional<Maintenance> findByMaintenanceNumber(String maintenanceNumber);
    Page<Maintenance> findByTruckId(Long truckId, Pageable pageable);
    Page<Maintenance> findByMaintenanceType(String maintenanceType, Pageable pageable);
    Page<Maintenance> findByServiceCategory(String serviceCategory, Pageable pageable);
    Page<Maintenance> findByStatus(String status, Pageable pageable);
    Page<Maintenance> findByPriority(String priority, Pageable pageable);
    Page<Maintenance> findByIsRecurring(Boolean isRecurring, Pageable pageable);
    
    // Search queries
    @Query("SELECT m FROM Maintenance m WHERE " +
           "(:maintenanceNumber IS NULL OR m.maintenanceNumber LIKE %:maintenanceNumber%) AND " +
           "(:truckId IS NULL OR m.truck.id = :truckId) AND " +
           "(:maintenanceType IS NULL OR m.maintenanceType = :maintenanceType) AND " +
           "(:serviceCategory IS NULL OR m.serviceCategory = :serviceCategory) AND " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:priority IS NULL OR m.priority = :priority) AND " +
           "(:startDate IS NULL OR m.scheduledDate >= :startDate) AND " +
           "(:endDate IS NULL OR m.scheduledDate <= :endDate)")
    Page<Maintenance> searchMaintenances(@Param("maintenanceNumber") String maintenanceNumber,
                                        @Param("truckId") Long truckId,
                                        @Param("maintenanceType") String maintenanceType,
                                        @Param("serviceCategory") String serviceCategory,
                                        @Param("status") String status,
                                        @Param("priority") String priority,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        Pageable pageable);
    
    // Scheduling queries
    @Query("SELECT m FROM Maintenance m WHERE m.scheduledDate BETWEEN :fromDate AND :toDate AND m.status = 'SCHEDULED'")
    Page<Maintenance> findScheduledMaintenances(@Param("fromDate") LocalDate fromDate,
                                               @Param("toDate") LocalDate toDate,
                                               Pageable pageable);
    
    @Query("SELECT m FROM Maintenance m WHERE m.status = 'OVERDUE'")
    Page<Maintenance> findOverdueMaintenances(Pageable pageable);
    
    @Query("SELECT m FROM Maintenance m WHERE m.scheduledDate <= :date AND m.status = 'SCHEDULED'")
    Page<Maintenance> findUpcomingMaintenances(@Param("date") LocalDate date, Pageable pageable);
    
    @Query("SELECT m FROM Maintenance m WHERE m.scheduledDate BETWEEN CURRENT_DATE AND :alertDate AND m.status = 'SCHEDULED'")
    List<Maintenance> findMaintenancesDueForAlert(@Param("alertDate") LocalDate alertDate);
    
    // Recurring maintenance queries
    @Query("SELECT m FROM Maintenance m WHERE m.isRecurring = true AND m.nextServiceDate <= :date")
    List<Maintenance> findRecurringMaintenancesDue(@Param("date") LocalDate date);
    
    // Cost queries
    @Query("SELECT SUM(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount) FROM Maintenance m WHERE " +
           "m.truck.id = :truckId AND m.completedDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalMaintenanceCost(@Param("truckId") Long truckId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m.status, COUNT(m), SUM(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount) " +
           "FROM Maintenance m WHERE m.scheduledDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.status")
    List<Object[]> getMaintenanceCostSummary(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.truckNumber, COUNT(m), SUM(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount) " +
           "FROM Maintenance m JOIN m.truck t WHERE m.completedDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.id, t.truckNumber")
    List<Object[]> getTruckWiseMaintenanceCost(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    // Reporting queries
    @Query("SELECT m.maintenanceType, COUNT(m), SUM(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount) " +
           "FROM Maintenance m WHERE m.scheduledDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.maintenanceType")
    List<Object[]> getMaintenanceByTypeReport(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m.serviceCategory, COUNT(m), SUM(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount) " +
           "FROM Maintenance m WHERE m.scheduledDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.serviceCategory")
    List<Object[]> getMaintenanceByCategoryReport(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT MONTH(m.scheduledDate), YEAR(m.scheduledDate), COUNT(m), SUM(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount) " +
           "FROM Maintenance m WHERE m.scheduledDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(m.scheduledDate), MONTH(m.scheduledDate) " +
           "ORDER BY YEAR(m.scheduledDate), MONTH(m.scheduledDate)")
    List<Object[]> getMonthlyMaintenanceSummary(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m.maintenanceNumber, m.maintenanceType, m.serviceCategory, m.scheduledDate, " +
           "m.completedDate, m.laborCost + m.partsCost + m.otherCharges + m.gstAmount " +
           "FROM Maintenance m WHERE m.truck.id = :truckId ORDER BY m.scheduledDate DESC")
    List<Object[]> getTruckMaintenanceHistory(@Param("truckId") Long truckId);
    
    @Query("SELECT m.serviceProvider, COUNT(m), AVG(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount), " +
           "AVG(DATEDIFF(m.completedDate, m.scheduledDate)) " +
           "FROM Maintenance m WHERE m.serviceProvider IS NOT NULL AND m.completedDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.serviceProvider")
    List<Object[]> getServiceProviderPerformance(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM Maintenance m WHERE " +
           "(:startDate IS NULL OR m.scheduledDate >= :startDate) AND " +
           "(:endDate IS NULL OR m.scheduledDate <= :endDate) AND " +
           "(:maintenanceType IS NULL OR m.maintenanceType = :maintenanceType) AND " +
           "(:status IS NULL OR m.status = :status)")
    List<Maintenance> findMaintenancesForReport(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("maintenanceType") String maintenanceType,
                                               @Param("status") String status);
    
    @Query("SELECT COUNT(m), " +
           "AVG(DATEDIFF(m.completedDate, m.scheduledDate)), " +
           "COUNT(CASE WHEN m.completedDate <= m.scheduledDate THEN 1 END), " +
           "COUNT(CASE WHEN m.completedDate > m.scheduledDate THEN 1 END) " +
           "FROM Maintenance m WHERE m.completedDate BETWEEN :startDate AND :endDate")
    List<Object[]> getMaintenanceEfficiencyReport(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
    
    // Statistics queries
    @Query("SELECT COUNT(m), " +
           "COUNT(CASE WHEN m.status = 'SCHEDULED' THEN 1 END), " +
           "COUNT(CASE WHEN m.status = 'IN_PROGRESS' THEN 1 END), " +
           "COUNT(CASE WHEN m.status = 'COMPLETED' THEN 1 END), " +
           "COUNT(CASE WHEN m.status = 'OVERDUE' THEN 1 END), " +
           "SUM(m.laborCost + m.partsCost + m.otherCharges + m.gstAmount) " +
           "FROM Maintenance m")
    Object[] getMaintenanceStatistics();
    
    // Utility queries
    boolean existsByMaintenanceNumberAndIdNot(String maintenanceNumber, Long id);
    long countByMaintenanceTypeAndStatus(String maintenanceType, String status);
    
    @Query("SELECT m.maintenanceNumber FROM Maintenance m WHERE m.maintenanceNumber LIKE :pattern ORDER BY m.maintenanceNumber DESC")
    String findLastMaintenanceNumberForDate(@Param("pattern") String pattern);
}

