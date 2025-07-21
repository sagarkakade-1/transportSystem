package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Driver;
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
 * Repository interface for Driver entity
 * Provides data access operations for driver management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Basic queries
    Optional<Driver> findByDriverNumber(String driverNumber);
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    Page<Driver> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Driver> findByLicenseType(String licenseType, Pageable pageable);
    
    // Search queries
    @Query("SELECT d FROM Driver d WHERE " +
           "(:driverNumber IS NULL OR d.driverNumber LIKE %:driverNumber%) AND " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:phone IS NULL OR d.phone LIKE %:phone%) AND " +
           "(:licenseType IS NULL OR d.licenseType = :licenseType) AND " +
           "(:isActive IS NULL OR d.isActive = :isActive)")
    Page<Driver> searchDrivers(@Param("driverNumber") String driverNumber,
                              @Param("name") String name,
                              @Param("phone") String phone,
                              @Param("licenseType") String licenseType,
                              @Param("isActive") Boolean isActive,
                              Pageable pageable);
    
    // License expiry queries
    @Query("SELECT d FROM Driver d WHERE d.licenseExpiryDate BETWEEN :startDate AND :endDate")
    List<Driver> findDriversWithLicenseExpiringBetween(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT d FROM Driver d WHERE d.licenseExpiryDate <= :date AND d.isActive = true")
    List<Driver> findDriversWithExpiredLicense(@Param("date") LocalDate date);
    
    // Availability queries
    @Query("SELECT CASE WHEN COUNT(t) = 0 THEN true ELSE false END FROM Trip t " +
           "WHERE t.driver.id = :driverId AND t.status IN ('PLANNED', 'RUNNING') AND " +
           "((t.plannedStartDate <= :endDate AND t.plannedEndDate >= :startDate) OR " +
           "(t.actualStartDate <= :endDate AND (t.actualEndDate IS NULL OR t.actualEndDate >= :startDate))) AND " +
           "(:excludeTripId IS NULL OR t.id != :excludeTripId)")
    boolean isDriverAvailable(@Param("driverId") Long driverId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate,
                             @Param("excludeTripId") Long excludeTripId);
    
    // Performance queries
    @Query("SELECT d.id, d.name, COUNT(t), AVG(t.distance), SUM(t.tripCharges) " +
           "FROM Driver d LEFT JOIN d.trips t " +
           "WHERE t.actualStartDate BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED' " +
           "GROUP BY d.id, d.name")
    List<Object[]> getDriverPerformanceReport(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    // Statistics queries
    @Query("SELECT COUNT(d), " +
           "COUNT(CASE WHEN d.isActive = true THEN 1 END), " +
           "COUNT(CASE WHEN d.licenseExpiryDate <= CURRENT_DATE THEN 1 END) " +
           "FROM Driver d")
    Object[] getDriverStatistics();
    
    // Utility queries
    boolean existsByDriverNumberAndIdNot(String driverNumber, Long id);
    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);
    
    @Query("SELECT d.driverNumber FROM Driver d WHERE d.driverNumber LIKE :pattern ORDER BY d.driverNumber DESC")
    List<String> findLastDriverNumberForDate(@Param("pattern") String pattern);
}

