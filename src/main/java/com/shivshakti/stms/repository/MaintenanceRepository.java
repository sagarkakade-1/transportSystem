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
 * Provides CRUD operations and custom queries for maintenance management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find maintenance by maintenance number
     */
    Optional<Maintenance> findByMaintenanceNumber(String maintenanceNumber);
    
    /**
     * Find maintenance records by truck
     */
    List<Maintenance> findByTruckId(Long truckId);
    
    /**
     * Find maintenance records by shop
     */
    List<Maintenance> findByShopId(Long shopId);
    
    /**
     * Find maintenance records by type
     */
    List<Maintenance> findByMaintenanceType(Maintenance.MaintenanceType maintenanceType);
    
    /**
     * Find maintenance records by date range
     */
    List<Maintenance> findByMaintenanceDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find maintenance records ordered by date
     */
    List<Maintenance> findAllByOrderByMaintenanceDateDesc();

    // ===============================================
    // MAINTENANCE TYPE QUERIES
    // ===============================================
    
    /**
     * Find service records
     */
    @Query("SELECT m FROM Maintenance m WHERE m.maintenanceType = 'SERVICE' ORDER BY m.maintenanceDate DESC")
    List<Maintenance> findServiceRecords();
    
    /**
     * Find repair records
     */
    @Query("SELECT m FROM Maintenance m WHERE m.maintenanceType = 'REPAIR' ORDER BY m.maintenanceDate DESC")
    List<Maintenance> findRepairRecords();
    
    /**
     * Find tyre change records
     */
    @Query("SELECT m FROM Maintenance m WHERE m.maintenanceType = 'TYRE_CHANGE' ORDER BY m.maintenanceDate DESC")
    List<Maintenance> findTyreChangeRecords();
    
    /**
     * Find oil change records
     */
    @Query("SELECT m FROM Maintenance m WHERE m.maintenanceType = 'OIL_CHANGE' ORDER BY m.maintenanceDate DESC")
    List<Maintenance> findOilChangeRecords();
    
    /**
     * Get maintenance type distribution
     */
    @Query("SELECT m.maintenanceType, COUNT(m), COALESCE(SUM(m.totalCost), 0) " +
           "FROM Maintenance m " +
           "WHERE m.maintenanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.maintenanceType " +
           "ORDER BY SUM(m.totalCost) DESC")
    List<Object[]> getMaintenanceTypeDistribution(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    // ===============================================
    // TRUCK-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get truck-wise maintenance summary
     */
    @Query("SELECT m.truck, COUNT(m), COALESCE(SUM(m.totalCost), 0), COALESCE(AVG(m.totalCost), 0) " +
           "FROM Maintenance m " +
           "WHERE m.maintenanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.truck " +
           "ORDER BY SUM(m.totalCost) DESC")
    List<Object[]> getTruckWiseMaintenanceSummary(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
    
    /**
     * Find trucks with highest maintenance costs
     */
    @Query("SELECT m.truck, COALESCE(SUM(m.totalCost), 0) as totalCost " +
           "FROM Maintenance m " +
           "WHERE m.maintenanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.truck " +
           "ORDER BY totalCost DESC")
    Page<Object[]> findTrucksWithHighestMaintenanceCosts(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate,
                                                        Pageable pageable);
    
    /**
     * Get truck maintenance frequency
     */
    @Query("SELECT m.truck, COUNT(m) as maintenanceCount " +
           "FROM Maintenance m " +
           "WHERE m.maintenanceDate >= :startDate " +
           "GROUP BY m.truck " +
           "ORDER BY maintenanceCount DESC")
    List<Object[]> getTruckMaintenanceFrequency(@Param("startDate") LocalDate startDate);
    
    /**
     * Find trucks requiring maintenance (based on mileage)
     */
    @Query("SELECT DISTINCT m.truck FROM Maintenance m " +
           "WHERE m.nextServiceMileage IS NOT NULL AND " +
           "m.truck.currentMileage >= m.nextServiceMileage")
    List<Object[]> findTrucksRequiringMaintenanceByMileage();

    // ===============================================
    // WARRANTY MANAGEMENT
    // ===============================================
    
    /**
     * Find maintenance records with active warranty
     */
    @Query("SELECT m FROM Maintenance m WHERE m.warrantyExpiryDate > CURRENT_DATE ORDER BY m.warrantyExpiryDate ASC")
    List<Maintenance> findMaintenanceWithActiveWarranty();
    
    /**
     * Find maintenance records with expired warranty
     */
    @Query("SELECT m FROM Maintenance m WHERE m.warrantyExpiryDate < CURRENT_DATE ORDER BY m.warrantyExpiryDate DESC")
    List<Maintenance> findMaintenanceWithExpiredWarranty();
    
    /**
     * Find maintenance records with warranty expiring soon
     */
    @Query("SELECT m FROM Maintenance m WHERE m.warrantyExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days ORDER BY m.warrantyExpiryDate ASC")
    List<Maintenance> findMaintenanceWithWarrantyExpiringSoon(@Param("days") int days);
    
    /**
     * Get warranty summary
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN m.warrantyExpiryDate > CURRENT_DATE THEN 1 END) as activeWarranties, " +
           "COUNT(CASE WHEN m.warrantyExpiryDate < CURRENT_DATE THEN 1 END) as expiredWarranties, " +
           "COUNT(CASE WHEN m.warrantyExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + 30 THEN 1 END) as expiringSoon " +
           "FROM Maintenance m " +
           "WHERE m.warrantyExpiryDate IS NOT NULL")
    Object[] getWarrantySummary();

    // ===============================================
    // SERVICE SCHEDULING
    // ===============================================
    
    /**
     * Find maintenance records with next service due
     */
    @Query("SELECT m FROM Maintenance m WHERE m.nextServiceDate <= CURRENT_DATE ORDER BY m.nextServiceDate ASC")
    List<Maintenance> findMaintenanceWithServiceDue();
    
    /**
     * Find maintenance records with service due soon
     */
    @Query("SELECT m FROM Maintenance m WHERE m.nextServiceDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days ORDER BY m.nextServiceDate ASC")
    List<Maintenance> findMaintenanceWithServiceDueSoon(@Param("days") int days);
    
    /**
     * Get service schedule summary
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN m.nextServiceDate <= CURRENT_DATE THEN 1 END) as serviceDue, " +
           "COUNT(CASE WHEN m.nextServiceDate BETWEEN CURRENT_DATE + 1 AND CURRENT_DATE + 7 THEN 1 END) as dueThisWeek, " +
           "COUNT(CASE WHEN m.nextServiceDate BETWEEN CURRENT_DATE + 8 AND CURRENT_DATE + 30 THEN 1 END) as dueThisMonth " +
           "FROM Maintenance m " +
           "WHERE m.nextServiceDate IS NOT NULL")
    Object[] getServiceScheduleSummary();

    // ===============================================
    // COST ANALYSIS
    // ===============================================
    
    /**
     * Find high cost maintenance records
     */
    @Query("SELECT m FROM Maintenance m WHERE m.totalCost > :threshold ORDER BY m.totalCost DESC")
    List<Maintenance> findHighCostMaintenance(@Param("threshold") BigDecimal threshold);
    
    /**
     * Calculate total maintenance cost for period
     */
    @Query("SELECT COALESCE(SUM(m.totalCost), 0) FROM Maintenance m WHERE m.maintenanceDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalMaintenanceCostForPeriod(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Get cost breakdown analysis
     */
    @Query("SELECT " +
           "COALESCE(SUM(m.totalCost), 0) as totalCost, " +
           "COALESCE(SUM(m.labourCost), 0) as totalLabourCost, " +
           "COALESCE(SUM(m.partsCost), 0) as totalPartsCost, " +
           "COALESCE(AVG(m.totalCost), 0) as averageCost " +
           "FROM Maintenance m " +
           "WHERE m.maintenanceDate BETWEEN :startDate AND :endDate")
    Object[] getCostBreakdownAnalysis(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Get maintenance cost trend
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM m.maintenanceDate) as year, " +
           "EXTRACT(MONTH FROM m.maintenanceDate) as month, " +
           "COUNT(m) as maintenanceCount, " +
           "COALESCE(SUM(m.totalCost), 0) as totalCost " +
           "FROM Maintenance m " +
           "WHERE m.maintenanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM m.maintenanceDate), EXTRACT(MONTH FROM m.maintenanceDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMaintenanceCostTrend(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // ===============================================
    // SHOP-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get shop-wise maintenance summary
     */
    @Query("SELECT m.shop, COUNT(m), COALESCE(SUM(m.totalCost), 0) " +
           "FROM Maintenance m " +
           "WHERE m.shop IS NOT NULL AND m.maintenanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.shop " +
           "ORDER BY SUM(m.totalCost) DESC")
    List<Object[]> getShopWiseMaintenanceSummary(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
    
    /**
     * Find preferred maintenance shops
     */
    @Query("SELECT m.shop, COUNT(m) as maintenanceCount, COALESCE(SUM(m.totalCost), 0) as totalCost " +
           "FROM Maintenance m " +
           "WHERE m.shop IS NOT NULL " +
           "GROUP BY m.shop " +
           "ORDER BY maintenanceCount DESC, totalCost DESC")
    List<Object[]> findPreferredMaintenanceShops();

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search maintenance records by multiple criteria
     */
    @Query("SELECT m FROM Maintenance m WHERE " +
           "(:maintenanceNumber IS NULL OR m.maintenanceNumber = :maintenanceNumber) AND " +
           "(:truckId IS NULL OR m.truck.id = :truckId) AND " +
           "(:shopId IS NULL OR m.shop.id = :shopId) AND " +
           "(:maintenanceType IS NULL OR m.maintenanceType = :maintenanceType) AND " +
           "(:startDate IS NULL OR m.maintenanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR m.maintenanceDate <= :endDate) AND " +
           "(:minCost IS NULL OR m.totalCost >= :minCost) AND " +
           "(:maxCost IS NULL OR m.totalCost <= :maxCost)")
    Page<Maintenance> searchMaintenance(@Param("maintenanceNumber") String maintenanceNumber,
                                       @Param("truckId") Long truckId,
                                       @Param("shopId") Long shopId,
                                       @Param("maintenanceType") Maintenance.MaintenanceType maintenanceType,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       @Param("minCost") BigDecimal minCost,
                                       @Param("maxCost") BigDecimal maxCost,
                                       Pageable pageable);
    
    /**
     * Find maintenance by cost range
     */
    List<Maintenance> findByTotalCostBetween(BigDecimal minCost, BigDecimal maxCost);
    
    /**
     * Find maintenance by description containing
     */
    List<Maintenance> findByDescriptionContainingIgnoreCase(String description);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly maintenance summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM m.maintenanceDate) as year, " +
           "EXTRACT(MONTH FROM m.maintenanceDate) as month, " +
           "COUNT(m) as maintenanceCount, " +
           "COALESCE(SUM(m.totalCost), 0) as totalCost, " +
           "COALESCE(AVG(m.totalCost), 0) as averageCost " +
           "FROM Maintenance m " +
           "WHERE m.maintenanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM m.maintenanceDate), EXTRACT(MONTH FROM m.maintenanceDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyMaintenanceSummary(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
    
    /**
     * Get maintenance statistics
     */
    @Query("SELECT " +
           "COUNT(m) as totalMaintenance, " +
           "COALESCE(SUM(m.totalCost), 0) as totalCost, " +
           "COALESCE(AVG(m.totalCost), 0) as averageCost, " +
           "COUNT(CASE WHEN m.maintenanceType = 'SERVICE' THEN 1 END) as serviceCount, " +
           "COUNT(CASE WHEN m.maintenanceType = 'REPAIR' THEN 1 END) as repairCount, " +
           "COUNT(CASE WHEN m.warrantyExpiryDate > CURRENT_DATE THEN 1 END) as activeWarranties " +
           "FROM Maintenance m")
    Object[] getMaintenanceStatistics();
    
    /**
     * Get maintenance performance by truck
     */
    @Query("SELECT m.truck, " +
           "COUNT(m) as maintenanceCount, " +
           "COALESCE(SUM(m.totalCost), 0) as totalCost, " +
           "COALESCE(AVG(m.totalCost), 0) as avgCost, " +
           "MAX(m.maintenanceDate) as lastMaintenanceDate " +
           "FROM Maintenance m " +
           "GROUP BY m.truck " +
           "ORDER BY totalCost DESC")
    List<Object[]> getMaintenancePerformanceByTruck();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if maintenance number exists for different maintenance
     */
    boolean existsByMaintenanceNumberAndIdNot(String maintenanceNumber, Long id);
    
    /**
     * Count maintenance records by type
     */
    long countByMaintenanceType(Maintenance.MaintenanceType maintenanceType);
    
    /**
     * Count maintenance records for truck
     */
    long countByTruckId(Long truckId);
    
    /**
     * Count maintenance records with active warranty
     */
    @Query("SELECT COUNT(m) FROM Maintenance m WHERE m.warrantyExpiryDate > CURRENT_DATE")
    long countMaintenanceWithActiveWarranty();
    
    /**
     * Count maintenance records with service due
     */
    @Query("SELECT COUNT(m) FROM Maintenance m WHERE m.nextServiceDate <= CURRENT_DATE")
    long countMaintenanceWithServiceDue();
}

