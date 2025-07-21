package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Truck entity
 * Provides data access operations for truck management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {

    // Basic queries
    Optional<Truck> findByTruckNumber(String truckNumber);
    Page<Truck> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Truck> findByStatus(String status, Pageable pageable);
    Page<Truck> findByFuelType(String fuelType, Pageable pageable);
    Page<Truck> findByModel(String model, Pageable pageable);
    
    // Search queries
    @Query("SELECT t FROM Truck t WHERE " +
           "(:truckNumber IS NULL OR t.truckNumber LIKE %:truckNumber%) AND " +
           "(:model IS NULL OR t.model LIKE %:model%) AND " +
           "(:manufacturer IS NULL OR t.manufacturer LIKE %:manufacturer%) AND " +
           "(:fuelType IS NULL OR t.fuelType = :fuelType) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:isActive IS NULL OR t.isActive = :isActive)")
    Page<Truck> searchTrucks(@Param("truckNumber") String truckNumber,
                            @Param("model") String model,
                            @Param("manufacturer") String manufacturer,
                            @Param("fuelType") String fuelType,
                            @Param("status") String status,
                            @Param("isActive") Boolean isActive,
                            Pageable pageable);
    
    // Document expiry queries
    @Query("SELECT t FROM Truck t WHERE t.insuranceExpiryDate BETWEEN :startDate AND :endDate")
    List<Truck> findTrucksWithInsuranceExpiringBetween(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t FROM Truck t WHERE t.permitExpiryDate BETWEEN :startDate AND :endDate")
    List<Truck> findTrucksWithPermitExpiringBetween(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t FROM Truck t WHERE t.fitnessCertificateExpiry BETWEEN :startDate AND :endDate")
    List<Truck> findTrucksWithFitnessExpiringBetween(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t FROM Truck t WHERE t.pollutionCertificateExpiry BETWEEN :startDate AND :endDate")
    List<Truck> findTrucksWithPollutionExpiringBetween(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
    
    // Availability queries
    @Query("SELECT CASE WHEN COUNT(tr) = 0 THEN true ELSE false END FROM Trip tr " +
           "WHERE tr.truck.id = :truckId AND tr.status IN ('PLANNED', 'RUNNING') AND " +
           "((tr.plannedStartDate <= :endDate AND tr.plannedEndDate >= :startDate) OR " +
           "(tr.actualStartDate <= :endDate AND (tr.actualEndDate IS NULL OR tr.actualEndDate >= :startDate))) AND " +
           "(:excludeTripId IS NULL OR tr.id != :excludeTripId)")
    boolean isTruckAvailable(@Param("truckId") Long truckId,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate,
                            @Param("excludeTripId") Long excludeTripId);
    
    @Query("SELECT t FROM Truck t WHERE t.status = 'AVAILABLE' AND t.isActive = true")
    List<Truck> findAvailableTrucks();
    
    // Performance queries
    @Query("SELECT t.id, t.truckNumber, COUNT(tr), SUM(tr.distance), AVG(tr.fuelConsumed) " +
           "FROM Truck t LEFT JOIN t.trips tr " +
           "WHERE tr.actualStartDate BETWEEN :startDate AND :endDate AND tr.status = 'COMPLETED' " +
           "GROUP BY t.id, t.truckNumber")
    List<Object[]> getTruckPerformanceReport(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.id, t.truckNumber, SUM(e.amount) " +
           "FROM Truck t LEFT JOIN t.expenses e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.id, t.truckNumber")
    List<Object[]> getTruckExpenseReport(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    // Maintenance queries
    @Query("SELECT t FROM Truck t WHERE EXISTS " +
           "(SELECT m FROM Maintenance m WHERE m.truck = t AND m.status = 'OVERDUE')")
    List<Truck> findTrucksWithOverdueMaintenance();
    
    @Query("SELECT t FROM Truck t WHERE EXISTS " +
           "(SELECT m FROM Maintenance m WHERE m.truck = t AND m.scheduledDate <= :date AND m.status = 'SCHEDULED')")
    List<Truck> findTrucksWithUpcomingMaintenance(@Param("date") LocalDate date);
    
    // Statistics queries
    @Query("SELECT COUNT(t), " +
           "COUNT(CASE WHEN t.isActive = true THEN 1 END), " +
           "COUNT(CASE WHEN t.status = 'AVAILABLE' THEN 1 END), " +
           "COUNT(CASE WHEN t.status = 'RUNNING' THEN 1 END), " +
           "COUNT(CASE WHEN t.status = 'MAINTENANCE' THEN 1 END) " +
           "FROM Truck t")
    Object[] getTruckStatistics();
    
    // Utility queries
    boolean existsByTruckNumberAndIdNot(String truckNumber, Long id);
    
    @Query("SELECT t.truckNumber FROM Truck t WHERE t.truckNumber LIKE :pattern ORDER BY t.truckNumber DESC")
    List<String> findLastTruckNumberForDate(@Param("pattern") String pattern);
}

