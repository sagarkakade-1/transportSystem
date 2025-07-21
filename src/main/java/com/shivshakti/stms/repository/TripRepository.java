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
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Trip entity
 * Provides CRUD operations and custom queries for trip management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find trip by trip number
     */
    Optional<Trip> findByTripNumber(String tripNumber);
    
    /**
     * Find trips by status
     */
    List<Trip> findByStatus(Trip.TripStatus status);
    
    /**
     * Find trips by truck
     */
    List<Trip> findByTruckId(Long truckId);
    
    /**
     * Find trips by driver
     */
    List<Trip> findByDriverId(Long driverId);
    
    /**
     * Find trips by date range
     */
    List<Trip> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // STATUS-BASED QUERIES
    // ===============================================
    
    /**
     * Find all pending trips
     */
    List<Trip> findByStatusOrderByStartDateAsc(Trip.TripStatus status);
    
    /**
     * Find running trips
     */
    @Query("SELECT t FROM Trip t WHERE t.status = 'RUNNING' ORDER BY t.actualStartDatetime ASC")
    List<Trip> findRunningTrips();
    
    /**
     * Find completed trips for date range
     */
    @Query("SELECT t FROM Trip t WHERE t.status = 'COMPLETED' AND t.endDate BETWEEN :startDate AND :endDate")
    List<Trip> findCompletedTripsInDateRange(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Find overdue trips (pending trips past start date)
     */
    @Query("SELECT t FROM Trip t WHERE t.status = 'PENDING' AND t.startDate < CURRENT_DATE")
    List<Trip> findOverdueTrips();
    
    /**
     * Find long running trips (running for more than specified days)
     */
    @Query("SELECT t FROM Trip t WHERE t.status = 'RUNNING' AND t.actualStartDatetime < CURRENT_TIMESTAMP - :days")
    List<Trip> findLongRunningTrips(@Param("days") int days);

    // ===============================================
    // ROUTE AND LOCATION QUERIES
    // ===============================================
    
    /**
     * Find trips by source location
     */
    List<Trip> findBySourceLocationContainingIgnoreCase(String sourceLocation);
    
    /**
     * Find trips by destination location
     */
    List<Trip> findByDestinationLocationContainingIgnoreCase(String destinationLocation);
    
    /**
     * Find trips by route (source and destination)
     */
    @Query("SELECT t FROM Trip t WHERE " +
           "LOWER(t.sourceLocation) LIKE LOWER(CONCAT('%', :source, '%')) AND " +
           "LOWER(t.destinationLocation) LIKE LOWER(CONCAT('%', :destination, '%'))")
    List<Trip> findTripsByRoute(@Param("source") String source, @Param("destination") String destination);
    
    /**
     * Get popular routes
     */
    @Query("SELECT t.sourceLocation, t.destinationLocation, COUNT(t) as tripCount " +
           "FROM Trip t " +
           "WHERE t.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.sourceLocation, t.destinationLocation " +
           "ORDER BY tripCount DESC")
    List<Object[]> getPopularRoutes(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    // ===============================================
    // FINANCIAL ANALYSIS QUERIES
    // ===============================================
    
    /**
     * Get trip profitability analysis
     */
    @Query("SELECT t, " +
           "COALESCE(SUM(i.amount), 0) as totalIncome, " +
           "COALESCE(SUM(e.amount), 0) as totalExpenses, " +
           "(COALESCE(SUM(i.amount), 0) - COALESCE(SUM(e.amount), 0)) as profit " +
           "FROM Trip t " +
           "LEFT JOIN t.incomes i " +
           "LEFT JOIN t.expenses e " +
           "WHERE t.status = 'COMPLETED' " +
           "GROUP BY t " +
           "ORDER BY profit DESC")
    List<Object[]> getTripProfitabilityAnalysis();
    
    /**
     * Get most profitable trips
     */
    @Query("SELECT t, " +
           "(COALESCE(SUM(i.amount), 0) - COALESCE(SUM(e.amount), 0)) as profit " +
           "FROM Trip t " +
           "LEFT JOIN t.incomes i " +
           "LEFT JOIN t.expenses e " +
           "WHERE t.status = 'COMPLETED' AND t.endDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t " +
           "HAVING profit > 0 " +
           "ORDER BY profit DESC")
    Page<Object[]> getMostProfitableTrips(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         Pageable pageable);
    
    /**
     * Calculate total revenue for period
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Trip t JOIN t.incomes i " +
           "WHERE t.endDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenueForPeriod(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // ===============================================
    // PERFORMANCE METRICS
    // ===============================================
    
    /**
     * Get fuel efficiency statistics
     */
    @Query("SELECT t, (t.distanceKm / t.fuelUsedLiters) as efficiency " +
           "FROM Trip t " +
           "WHERE t.status = 'COMPLETED' AND t.fuelUsedLiters > 0 " +
           "ORDER BY efficiency DESC")
    List<Object[]> getFuelEfficiencyStatistics();
    
    /**
     * Get trip duration analysis
     */
    @Query("SELECT t, " +
           "EXTRACT(DAY FROM (t.actualEndDatetime - t.actualStartDatetime)) as durationDays " +
           "FROM Trip t " +
           "WHERE t.status = 'COMPLETED' AND t.actualStartDatetime IS NOT NULL AND t.actualEndDatetime IS NOT NULL " +
           "ORDER BY durationDays DESC")
    List<Object[]> getTripDurationAnalysis();
    
    /**
     * Get average trip statistics
     */
    @Query("SELECT " +
           "COUNT(t) as totalTrips, " +
           "COALESCE(AVG(t.distanceKm), 0) as avgDistance, " +
           "COALESCE(AVG(t.fuelUsedLiters), 0) as avgFuelUsed, " +
           "COALESCE(AVG(t.distanceKm / t.fuelUsedLiters), 0) as avgEfficiency " +
           "FROM Trip t " +
           "WHERE t.status = 'COMPLETED' AND t.fuelUsedLiters > 0")
    Object[] getAverageTripStatistics();

    // ===============================================
    // TRUCK AND DRIVER PERFORMANCE
    // ===============================================
    
    /**
     * Get truck performance summary
     */
    @Query("SELECT t.truck, COUNT(t), " +
           "COALESCE(SUM(t.distanceKm), 0) as totalDistance, " +
           "COALESCE(SUM(t.fuelUsedLiters), 0) as totalFuel, " +
           "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as completedTrips " +
           "FROM Trip t " +
           "WHERE t.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.truck " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getTruckPerformanceSummary(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * Get driver performance summary
     */
    @Query("SELECT t.driver, COUNT(t), " +
           "COALESCE(SUM(t.distanceKm), 0) as totalDistance, " +
           "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as completedTrips, " +
           "COUNT(CASE WHEN t.status = 'CANCELLED' THEN 1 END) as cancelledTrips " +
           "FROM Trip t " +
           "WHERE t.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.driver " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getDriverPerformanceSummary(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search trips by multiple criteria
     */
    @Query("SELECT t FROM Trip t WHERE " +
           "(:tripNumber IS NULL OR t.tripNumber = :tripNumber) AND " +
           "(:truckId IS NULL OR t.truck.id = :truckId) AND " +
           "(:driverId IS NULL OR t.driver.id = :driverId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:sourceLocation IS NULL OR LOWER(t.sourceLocation) LIKE LOWER(CONCAT('%', :sourceLocation, '%'))) AND " +
           "(:destinationLocation IS NULL OR LOWER(t.destinationLocation) LIKE LOWER(CONCAT('%', :destinationLocation, '%'))) AND " +
           "(:startDate IS NULL OR t.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.startDate <= :endDate)")
    Page<Trip> searchTrips(@Param("tripNumber") String tripNumber,
                          @Param("truckId") Long truckId,
                          @Param("driverId") Long driverId,
                          @Param("status") Trip.TripStatus status,
                          @Param("sourceLocation") String sourceLocation,
                          @Param("destinationLocation") String destinationLocation,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate,
                          Pageable pageable);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly trip summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM t.startDate) as year, " +
           "EXTRACT(MONTH FROM t.startDate) as month, " +
           "COUNT(t) as totalTrips, " +
           "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as completedTrips, " +
           "COUNT(CASE WHEN t.status = 'RUNNING' THEN 1 END) as runningTrips, " +
           "COUNT(CASE WHEN t.status = 'CANCELLED' THEN 1 END) as cancelledTrips, " +
           "COALESCE(SUM(t.distanceKm), 0) as totalDistance " +
           "FROM Trip t " +
           "WHERE t.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM t.startDate), EXTRACT(MONTH FROM t.startDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyTripSummary(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    /**
     * Get trip statistics
     */
    @Query("SELECT " +
           "COUNT(t) as totalTrips, " +
           "COUNT(CASE WHEN t.status = 'PENDING' THEN 1 END) as pendingTrips, " +
           "COUNT(CASE WHEN t.status = 'RUNNING' THEN 1 END) as runningTrips, " +
           "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as completedTrips, " +
           "COUNT(CASE WHEN t.status = 'CANCELLED' THEN 1 END) as cancelledTrips, " +
           "COALESCE(SUM(t.distanceKm), 0) as totalDistance, " +
           "COALESCE(AVG(t.distanceKm), 0) as averageDistance " +
           "FROM Trip t")
    Object[] getTripStatistics();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if trip number exists for different trip
     */
    boolean existsByTripNumberAndIdNot(String tripNumber, Long id);
    
    /**
     * Count trips by status
     */
    long countByStatus(Trip.TripStatus status);
    
    /**
     * Count trips for truck in date range
     */
    long countByTruckIdAndStartDateBetween(Long truckId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Count trips for driver in date range
     */
    long countByDriverIdAndStartDateBetween(Long driverId, LocalDate startDate, LocalDate endDate);
}

