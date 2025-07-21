package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Trip entity
 * Provides data access operations for trip management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Basic queries
    Optional<Trip> findByTripNumber(String tripNumber);
    Page<Trip> findByStatus(String status, Pageable pageable);
    Page<Trip> findByTruckId(Long truckId, Pageable pageable);
    Page<Trip> findByDriverId(Long driverId, Pageable pageable);
    Page<Trip> findByClientId(Long clientId, Pageable pageable);
    
    // Search queries
    @Query("SELECT t FROM Trip t WHERE " +
           "(:tripNumber IS NULL OR t.tripNumber LIKE %:tripNumber%) AND " +
           "(:truckId IS NULL OR t.truck.id = :truckId) AND " +
           "(:driverId IS NULL OR t.driver.id = :driverId) AND " +
           "(:clientId IS NULL OR t.client.id = :clientId) AND " +
           "(:sourceLocation IS NULL OR t.sourceLocation LIKE %:sourceLocation%) AND " +
           "(:destinationLocation IS NULL OR t.destinationLocation LIKE %:destinationLocation%) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:startDate IS NULL OR t.plannedStartDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.plannedStartDate <= :endDate)")
    Page<Trip> searchTrips(@Param("tripNumber") String tripNumber,
                          @Param("truckId") Long truckId,
                          @Param("driverId") Long driverId,
                          @Param("clientId") Long clientId,
                          @Param("sourceLocation") String sourceLocation,
                          @Param("destinationLocation") String destinationLocation,
                          @Param("status") String status,
                          @Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate,
                          Pageable pageable);
    
    // Date range queries
    @Query("SELECT t FROM Trip t WHERE t.plannedStartDate BETWEEN :startDate AND :endDate")
    Page<Trip> findTripsPlannedBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);
    
    @Query("SELECT t FROM Trip t WHERE t.actualStartDate BETWEEN :startDate AND :endDate")
    Page<Trip> findTripsCompletedBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);
    
    // Status-based queries
    @Query("SELECT t FROM Trip t WHERE t.status = 'PLANNED' AND t.plannedStartDate <= :date")
    List<Trip> findOverdueTrips(@Param("date") LocalDateTime date);
    
    @Query("SELECT t FROM Trip t WHERE t.status = 'RUNNING'")
    List<Trip> findRunningTrips();
    
    @Query("SELECT t FROM Trip t WHERE t.status = 'PLANNED' AND t.plannedStartDate BETWEEN :startDate AND :endDate")
    List<Trip> findUpcomingTrips(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
    
    // Financial queries
    @Query("SELECT SUM(t.tripCharges) FROM Trip t WHERE t.status = 'COMPLETED' AND " +
           "t.actualStartDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.fuelCost + t.tollCharges + t.otherExpenses) FROM Trip t WHERE " +
           "t.status = 'COMPLETED' AND t.actualStartDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalExpenses(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
    
    // Performance queries
    @Query("SELECT t.sourceLocation, t.destinationLocation, COUNT(t), AVG(t.distance), AVG(t.tripCharges) " +
           "FROM Trip t WHERE t.status = 'COMPLETED' AND t.actualStartDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.sourceLocation, t.destinationLocation")
    List<Object[]> getRoutePerformanceReport(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DATE(t.actualStartDate), COUNT(t), SUM(t.tripCharges), AVG(t.distance) " +
           "FROM Trip t WHERE t.status = 'COMPLETED' AND t.actualStartDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(t.actualStartDate) ORDER BY DATE(t.actualStartDate)")
    List<Object[]> getDailyTripReport(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT MONTH(t.actualStartDate), YEAR(t.actualStartDate), COUNT(t), SUM(t.tripCharges) " +
           "FROM Trip t WHERE t.status = 'COMPLETED' AND t.actualStartDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(t.actualStartDate), MONTH(t.actualStartDate) " +
           "ORDER BY YEAR(t.actualStartDate), MONTH(t.actualStartDate)")
    List<Object[]> getMonthlyTripReport(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    // Efficiency queries
    @Query("SELECT t FROM Trip t WHERE t.status = 'COMPLETED' AND " +
           "(t.actualEndDate IS NULL OR DATEDIFF(t.actualEndDate, t.actualStartDate) > DATEDIFF(t.plannedEndDate, t.plannedStartDate))")
    List<Trip> findDelayedTrips();
    
    @Query("SELECT AVG(DATEDIFF(t.actualEndDate, t.actualStartDate)) FROM Trip t WHERE " +
           "t.status = 'COMPLETED' AND t.actualStartDate BETWEEN :startDate AND :endDate")
    Double getAverageTripDuration(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
    
    // Statistics queries
    @Query("SELECT COUNT(t), " +
           "COUNT(CASE WHEN t.status = 'PLANNED' THEN 1 END), " +
           "COUNT(CASE WHEN t.status = 'RUNNING' THEN 1 END), " +
           "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END), " +
           "COUNT(CASE WHEN t.status = 'CANCELLED' THEN 1 END) " +
           "FROM Trip t")
    Object[] getTripStatistics();
    
    // Utility queries
    boolean existsByTripNumberAndIdNot(String tripNumber, Long id);
    
    @Query("SELECT t.tripNumber FROM Trip t WHERE t.tripNumber LIKE :pattern ORDER BY t.tripNumber DESC")
    List<String> findLastTripNumberForDate(@Param("pattern") String pattern);
}

