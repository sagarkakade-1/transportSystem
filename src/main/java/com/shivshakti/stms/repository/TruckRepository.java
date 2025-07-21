package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Truck;
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
 * Repository interface for Truck entity
 * Provides CRUD operations and custom queries for truck management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find truck by truck number
     */
    Optional<Truck> findByTruckNumber(String truckNumber);
    
    /**
     * Find all active trucks
     */
    List<Truck> findByIsActiveTrue();
    
    /**
     * Find trucks by make
     */
    List<Truck> findByMakeIgnoreCase(String make);
    
    /**
     * Find trucks by model
     */
    List<Truck> findByModelIgnoreCase(String model);
    
    /**
     * Find trucks by fuel type
     */
    List<Truck> findByFuelType(Truck.FuelType fuelType);
    
    /**
     * Find trucks by capacity range
     */
    List<Truck> findByCapacityTonsBetween(BigDecimal minCapacity, BigDecimal maxCapacity);

    // ===============================================
    // DOCUMENT EXPIRY MANAGEMENT
    // ===============================================
    
    /**
     * Find trucks with expired insurance
     */
    @Query("SELECT t FROM Truck t WHERE t.insuranceExpiryDate < CURRENT_DATE AND t.isActive = true")
    List<Truck> findTrucksWithExpiredInsurance();
    
    /**
     * Find trucks with insurance expiring within specified days
     */
    @Query("SELECT t FROM Truck t WHERE t.insuranceExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days AND t.isActive = true")
    List<Truck> findTrucksWithInsuranceExpiringSoon(@Param("days") int days);
    
    /**
     * Find trucks with expired RC
     */
    @Query("SELECT t FROM Truck t WHERE t.rcExpiryDate < CURRENT_DATE AND t.isActive = true")
    List<Truck> findTrucksWithExpiredRC();
    
    /**
     * Find trucks with expired permits
     */
    @Query("SELECT t FROM Truck t WHERE t.permitExpiryDate < CURRENT_DATE AND t.isActive = true")
    List<Truck> findTrucksWithExpiredPermits();
    
    /**
     * Find trucks with expired fitness certificates
     */
    @Query("SELECT t FROM Truck t WHERE t.fitnessExpiryDate < CURRENT_DATE AND t.isActive = true")
    List<Truck> findTrucksWithExpiredFitness();
    
    /**
     * Find trucks with expired PUC certificates
     */
    @Query("SELECT t FROM Truck t WHERE t.pucExpiryDate < CURRENT_DATE AND t.isActive = true")
    List<Truck> findTrucksWithExpiredPUC();
    
    /**
     * Find trucks with any document expiring soon
     */
    @Query("SELECT DISTINCT t FROM Truck t WHERE t.isActive = true AND (" +
           "t.insuranceExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days OR " +
           "t.rcExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days OR " +
           "t.permitExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days OR " +
           "t.fitnessExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days OR " +
           "t.pucExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days)")
    List<Truck> findTrucksWithDocumentsExpiringSoon(@Param("days") int days);

    // ===============================================
    // AVAILABILITY AND TRIP QUERIES
    // ===============================================
    
    /**
     * Find trucks with active trips
     */
    @Query("SELECT DISTINCT t FROM Truck t JOIN t.trips tr WHERE tr.status = 'RUNNING' AND t.isActive = true")
    List<Truck> findTrucksWithActiveTrips();
    
    /**
     * Find available trucks (no running trips)
     */
    @Query("SELECT t FROM Truck t WHERE t.isActive = true AND t.id NOT IN " +
           "(SELECT DISTINCT tr.truck.id FROM Trip tr WHERE tr.status = 'RUNNING')")
    List<Truck> findAvailableTrucks();
    
    /**
     * Find trucks by capacity and availability
     */
    @Query("SELECT t FROM Truck t WHERE t.isActive = true AND " +
           "t.capacityTons >= :minCapacity AND " +
           "t.id NOT IN (SELECT DISTINCT tr.truck.id FROM Trip tr WHERE tr.status = 'RUNNING')")
    List<Truck> findAvailableTrucksByCapacity(@Param("minCapacity") BigDecimal minCapacity);

    // ===============================================
    // PERFORMANCE AND ANALYTICS
    // ===============================================
    
    /**
     * Get truck utilization statistics
     */
    @Query("SELECT t, COUNT(tr), " +
           "COALESCE(SUM(CASE WHEN tr.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) as completedTrips, " +
           "COALESCE(SUM(tr.distanceKm), 0) as totalDistance, " +
           "COALESCE(SUM(tr.fuelUsedLiters), 0) as totalFuelUsed " +
           "FROM Truck t LEFT JOIN t.trips tr " +
           "WHERE t.isActive = true " +
           "GROUP BY t " +
           "ORDER BY COUNT(tr) DESC")
    List<Object[]> getTruckUtilizationStatistics();
    
    /**
     * Get truck performance for date range
     */
    @Query("SELECT t, COUNT(tr), COALESCE(SUM(tr.distanceKm), 0), " +
           "COALESCE(SUM(tr.fuelUsedLiters), 0), COALESCE(AVG(tr.fuelUsedLiters/tr.distanceKm), 0) " +
           "FROM Truck t LEFT JOIN t.trips tr " +
           "WHERE tr.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t " +
           "ORDER BY COUNT(tr) DESC")
    List<Object[]> getTruckPerformanceForPeriod(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
    
    /**
     * Find most efficient trucks by fuel consumption
     */
    @Query("SELECT t, AVG(tr.distanceKm / tr.fuelUsedLiters) as efficiency " +
           "FROM Truck t JOIN t.trips tr " +
           "WHERE tr.status = 'COMPLETED' AND tr.fuelUsedLiters > 0 AND t.isActive = true " +
           "GROUP BY t " +
           "ORDER BY efficiency DESC")
    Page<Object[]> findMostEfficientTrucks(Pageable pageable);

    // ===============================================
    // MAINTENANCE AND EXPENSE QUERIES
    // ===============================================
    
    /**
     * Find trucks with high maintenance costs
     */
    @Query("SELECT t, COALESCE(SUM(m.totalCost), 0) as maintenanceCost " +
           "FROM Truck t LEFT JOIN t.maintenanceRecords m " +
           "WHERE m.maintenanceDate >= :startDate AND t.isActive = true " +
           "GROUP BY t " +
           "HAVING SUM(m.totalCost) > :threshold " +
           "ORDER BY maintenanceCost DESC")
    List<Object[]> findTrucksWithHighMaintenanceCosts(@Param("startDate") LocalDate startDate,
                                                     @Param("threshold") BigDecimal threshold);
    
    /**
     * Get truck expense summary
     */
    @Query("SELECT t, " +
           "COALESCE(SUM(e.amount), 0) as totalExpenses, " +
           "COALESCE(SUM(CASE WHEN e.category.categoryName = 'FUEL' THEN e.amount ELSE 0 END), 0) as fuelExpenses, " +
           "COALESCE(SUM(CASE WHEN e.category.categoryName = 'MAINTENANCE' THEN e.amount ELSE 0 END), 0) as maintenanceExpenses " +
           "FROM Truck t LEFT JOIN t.expenses e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t " +
           "ORDER BY totalExpenses DESC")
    List<Object[]> getTruckExpenseSummary(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search trucks by multiple criteria
     */
    @Query("SELECT t FROM Truck t WHERE " +
           "(:truckNumber IS NULL OR t.truckNumber = :truckNumber) AND " +
           "(:make IS NULL OR LOWER(t.make) LIKE LOWER(CONCAT('%', :make, '%'))) AND " +
           "(:model IS NULL OR LOWER(t.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:fuelType IS NULL OR t.fuelType = :fuelType) AND " +
           "(:minCapacity IS NULL OR t.capacityTons >= :minCapacity) AND " +
           "(:maxCapacity IS NULL OR t.capacityTons <= :maxCapacity) AND " +
           "(:isActive IS NULL OR t.isActive = :isActive)")
    Page<Truck> searchTrucks(@Param("truckNumber") String truckNumber,
                            @Param("make") String make,
                            @Param("model") String model,
                            @Param("fuelType") Truck.FuelType fuelType,
                            @Param("minCapacity") BigDecimal minCapacity,
                            @Param("maxCapacity") BigDecimal maxCapacity,
                            @Param("isActive") Boolean isActive,
                            Pageable pageable);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get truck fleet summary
     */
    @Query("SELECT " +
           "COUNT(t) as totalTrucks, " +
           "COUNT(CASE WHEN t.isActive = true THEN 1 END) as activeTrucks, " +
           "COALESCE(SUM(t.capacityTons), 0) as totalCapacity, " +
           "COALESCE(AVG(t.capacityTons), 0) as averageCapacity, " +
           "COUNT(CASE WHEN t.insuranceExpiryDate < CURRENT_DATE THEN 1 END) as expiredInsurance, " +
           "COUNT(CASE WHEN t.permitExpiryDate < CURRENT_DATE THEN 1 END) as expiredPermits " +
           "FROM Truck t")
    Object[] getTruckFleetSummary();
    
    /**
     * Get monthly truck summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM t.createdDate) as year, " +
           "EXTRACT(MONTH FROM t.createdDate) as month, " +
           "COUNT(t) as truckCount, " +
           "COUNT(CASE WHEN t.isActive = true THEN 1 END) as activeCount, " +
           "COALESCE(SUM(t.capacityTons), 0) as totalCapacity " +
           "FROM Truck t " +
           "WHERE t.createdDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM t.createdDate), EXTRACT(MONTH FROM t.createdDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyTruckSummary(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if truck number exists for different truck
     */
    boolean existsByTruckNumberAndIdNot(String truckNumber, Long id);
    
    /**
     * Count active trucks
     */
    long countByIsActiveTrue();
    
    /**
     * Count trucks with expired documents
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Truck t WHERE t.isActive = true AND (" +
           "t.insuranceExpiryDate < CURRENT_DATE OR " +
           "t.rcExpiryDate < CURRENT_DATE OR " +
           "t.permitExpiryDate < CURRENT_DATE OR " +
           "t.fitnessExpiryDate < CURRENT_DATE OR " +
           "t.pucExpiryDate < CURRENT_DATE)")
    long countTrucksWithExpiredDocuments();
    
    /**
     * Count available trucks
     */
    @Query("SELECT COUNT(t) FROM Truck t WHERE t.isActive = true AND t.id NOT IN " +
           "(SELECT DISTINCT tr.truck.id FROM Trip tr WHERE tr.status = 'RUNNING')")
    long countAvailableTrucks();
}

