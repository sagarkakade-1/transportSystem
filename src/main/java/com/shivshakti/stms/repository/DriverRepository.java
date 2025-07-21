package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Driver;
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
 * Repository interface for Driver entity
 * Provides CRUD operations and custom queries for driver management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find driver by license number
     */
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    
    /**
     * Find driver by contact number
     */
    Optional<Driver> findByContactNumber(String contactNumber);
    
    /**
     * Find all active drivers
     */
    List<Driver> findByIsActiveTrue();
    
    /**
     * Find all inactive drivers
     */
    List<Driver> findByIsActiveFalse();
    
    /**
     * Find drivers by name containing (case insensitive)
     */
    List<Driver> findByNameContainingIgnoreCase(String name);

    // ===============================================
    // LICENSE MANAGEMENT QUERIES
    // ===============================================
    
    /**
     * Find drivers with expired licenses
     */
    @Query("SELECT d FROM Driver d WHERE d.licenseExpiryDate < CURRENT_DATE AND d.isActive = true")
    List<Driver> findDriversWithExpiredLicenses();
    
    /**
     * Find drivers with licenses expiring within specified days
     */
    @Query("SELECT d FROM Driver d WHERE d.licenseExpiryDate BETWEEN CURRENT_DATE AND :expiryDate AND d.isActive = true")
    List<Driver> findDriversWithLicensesExpiringSoon(@Param("expiryDate") LocalDate expiryDate);
    
    /**
     * Find drivers with licenses expiring in next N days
     */
    @Query("SELECT d FROM Driver d WHERE d.licenseExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days AND d.isActive = true")
    List<Driver> findDriversWithLicensesExpiringInDays(@Param("days") int days);

    // ===============================================
    // SALARY AND ADVANCE QUERIES
    // ===============================================
    
    /**
     * Find drivers with outstanding advances
     */
    @Query("SELECT d FROM Driver d WHERE d.advancePaid > 0 AND d.isActive = true")
    List<Driver> findDriversWithOutstandingAdvances();
    
    /**
     * Find drivers with advance greater than specified amount
     */
    List<Driver> findByAdvancePaidGreaterThan(BigDecimal amount);
    
    /**
     * Find drivers with salary in range
     */
    List<Driver> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);
    
    /**
     * Calculate total salary expense for all active drivers
     */
    @Query("SELECT COALESCE(SUM(d.salary), 0) FROM Driver d WHERE d.isActive = true")
    BigDecimal calculateTotalSalaryExpense();
    
    /**
     * Calculate total outstanding advances
     */
    @Query("SELECT COALESCE(SUM(d.advancePaid), 0) FROM Driver d WHERE d.isActive = true")
    BigDecimal calculateTotalOutstandingAdvances();

    // ===============================================
    // TRIP RELATED QUERIES
    // ===============================================
    
    /**
     * Find drivers with active trips
     */
    @Query("SELECT DISTINCT d FROM Driver d JOIN d.trips t WHERE t.status = 'RUNNING' AND d.isActive = true")
    List<Driver> findDriversWithActiveTrips();
    
    /**
     * Find available drivers (no running trips)
     */
    @Query("SELECT d FROM Driver d WHERE d.isActive = true AND d.id NOT IN " +
           "(SELECT DISTINCT t.driver.id FROM Trip t WHERE t.status = 'RUNNING')")
    List<Driver> findAvailableDrivers();
    
    /**
     * Get driver trip count for a date range
     */
    @Query("SELECT d, COUNT(t) FROM Driver d LEFT JOIN d.trips t " +
           "WHERE t.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY d ORDER BY COUNT(t) DESC")
    List<Object[]> getDriverTripCounts(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);

    // ===============================================
    // PERFORMANCE ANALYTICS
    // ===============================================
    
    /**
     * Get driver performance summary
     */
    @Query("SELECT d.id, d.name, COUNT(t), " +
           "COALESCE(SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END), 0), " +
           "COALESCE(AVG(t.distanceKm), 0) " +
           "FROM Driver d LEFT JOIN d.trips t " +
           "WHERE d.isActive = true " +
           "GROUP BY d.id, d.name " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getDriverPerformanceSummary();
    
    /**
     * Get top performing drivers by completed trips
     */
    @Query("SELECT d FROM Driver d JOIN d.trips t " +
           "WHERE t.status = 'COMPLETED' AND d.isActive = true " +
           "GROUP BY d " +
           "ORDER BY COUNT(t) DESC")
    Page<Driver> findTopPerformingDrivers(Pageable pageable);

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search drivers by multiple criteria
     */
    @Query("SELECT d FROM Driver d WHERE " +
           "(:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:licenseNumber IS NULL OR d.licenseNumber = :licenseNumber) AND " +
           "(:contactNumber IS NULL OR d.contactNumber = :contactNumber) AND " +
           "(:isActive IS NULL OR d.isActive = :isActive)")
    Page<Driver> searchDrivers(@Param("name") String name,
                              @Param("licenseNumber") String licenseNumber,
                              @Param("contactNumber") String contactNumber,
                              @Param("isActive") Boolean isActive,
                              Pageable pageable);
    
    /**
     * Find drivers by age range
     */
    @Query("SELECT d FROM Driver d WHERE " +
           "d.dateOfBirth BETWEEN :startDate AND :endDate AND d.isActive = true")
    List<Driver> findDriversByAgeRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly driver summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM d.createdDate) as year, " +
           "EXTRACT(MONTH FROM d.createdDate) as month, " +
           "COUNT(d) as driverCount, " +
           "COUNT(CASE WHEN d.isActive = true THEN 1 END) as activeCount, " +
           "COALESCE(SUM(d.salary), 0) as totalSalary, " +
           "COALESCE(SUM(d.advancePaid), 0) as totalAdvances " +
           "FROM Driver d " +
           "WHERE d.createdDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM d.createdDate), EXTRACT(MONTH FROM d.createdDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyDriverSummary(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * Get driver statistics
     */
    @Query("SELECT " +
           "COUNT(d) as totalDrivers, " +
           "COUNT(CASE WHEN d.isActive = true THEN 1 END) as activeDrivers, " +
           "COUNT(CASE WHEN d.licenseExpiryDate < CURRENT_DATE THEN 1 END) as expiredLicenses, " +
           "COUNT(CASE WHEN d.advancePaid > 0 THEN 1 END) as driversWithAdvances, " +
           "COALESCE(AVG(d.salary), 0) as averageSalary, " +
           "COALESCE(SUM(d.advancePaid), 0) as totalOutstandingAdvances " +
           "FROM Driver d")
    Object[] getDriverStatistics();

    // ===============================================
    // CUSTOM UPDATE QUERIES
    // ===============================================
    
    /**
     * Check if license number exists for different driver
     */
    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);
    
    /**
     * Check if contact number exists for different driver
     */
    boolean existsByContactNumberAndIdNot(String contactNumber, Long id);
    
    /**
     * Count active drivers
     */
    long countByIsActiveTrue();
    
    /**
     * Count drivers with expired licenses
     */
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.licenseExpiryDate < CURRENT_DATE AND d.isActive = true")
    long countDriversWithExpiredLicenses();
}

