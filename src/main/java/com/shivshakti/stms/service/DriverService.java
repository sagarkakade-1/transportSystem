package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.DriverDTO;
import com.shivshakti.stms.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Driver management
 * Defines business operations for driver-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface DriverService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    /**
     * Create a new driver
     * @param driverDTO Driver data transfer object
     * @return Created driver DTO
     */
    DriverDTO createDriver(DriverDTO driverDTO);
    
    /**
     * Update an existing driver
     * @param id Driver ID
     * @param driverDTO Updated driver data
     * @return Updated driver DTO
     */
    DriverDTO updateDriver(Long id, DriverDTO driverDTO);
    
    /**
     * Get driver by ID
     * @param id Driver ID
     * @return Driver DTO if found
     */
    Optional<DriverDTO> getDriverById(Long id);
    
    /**
     * Get all active drivers
     * @return List of active driver DTOs
     */
    List<DriverDTO> getAllActiveDrivers();
    
    /**
     * Get all drivers with pagination
     * @param pageable Pagination information
     * @return Page of driver DTOs
     */
    Page<DriverDTO> getAllDrivers(Pageable pageable);
    
    /**
     * Soft delete a driver (mark as inactive)
     * @param id Driver ID
     */
    void deleteDriver(Long id);
    
    /**
     * Activate a driver
     * @param id Driver ID
     */
    void activateDriver(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    /**
     * Search drivers by multiple criteria
     * @param name Driver name (partial match)
     * @param licenseNumber License number
     * @param contactNumber Contact number
     * @param isActive Active status
     * @param pageable Pagination information
     * @return Page of matching driver DTOs
     */
    Page<DriverDTO> searchDrivers(String name, String licenseNumber, 
                                 String contactNumber, Boolean isActive, 
                                 Pageable pageable);
    
    /**
     * Find driver by license number
     * @param licenseNumber License number
     * @return Driver DTO if found
     */
    Optional<DriverDTO> findByLicenseNumber(String licenseNumber);
    
    /**
     * Find driver by contact number
     * @param contactNumber Contact number
     * @return Driver DTO if found
     */
    Optional<DriverDTO> findByContactNumber(String contactNumber);

    // ===============================================
    // LICENSE MANAGEMENT
    // ===============================================
    
    /**
     * Get drivers with expired licenses
     * @return List of drivers with expired licenses
     */
    List<DriverDTO> getDriversWithExpiredLicenses();
    
    /**
     * Get drivers with licenses expiring soon
     * @param days Number of days to check ahead
     * @return List of drivers with licenses expiring soon
     */
    List<DriverDTO> getDriversWithLicensesExpiringSoon(int days);
    
    /**
     * Update driver license information
     * @param id Driver ID
     * @param licenseNumber New license number
     * @param expiryDate New expiry date
     * @return Updated driver DTO
     */
    DriverDTO updateLicenseInfo(Long id, String licenseNumber, LocalDate expiryDate);

    // ===============================================
    // SALARY AND ADVANCE MANAGEMENT
    // ===============================================
    
    /**
     * Update driver salary
     * @param id Driver ID
     * @param newSalary New salary amount
     * @return Updated driver DTO
     */
    DriverDTO updateSalary(Long id, BigDecimal newSalary);
    
    /**
     * Add advance payment to driver
     * @param id Driver ID
     * @param advanceAmount Advance amount to add
     * @param remarks Remarks for the advance
     * @return Updated driver DTO
     */
    DriverDTO addAdvancePayment(Long id, BigDecimal advanceAmount, String remarks);
    
    /**
     * Deduct advance from driver
     * @param id Driver ID
     * @param deductionAmount Amount to deduct
     * @param remarks Remarks for the deduction
     * @return Updated driver DTO
     */
    DriverDTO deductAdvance(Long id, BigDecimal deductionAmount, String remarks);
    
    /**
     * Get drivers with outstanding advances
     * @return List of drivers with outstanding advances
     */
    List<DriverDTO> getDriversWithOutstandingAdvances();
    
    /**
     * Calculate total salary expense for all active drivers
     * @return Total salary expense
     */
    BigDecimal calculateTotalSalaryExpense();
    
    /**
     * Calculate total outstanding advances
     * @return Total outstanding advances
     */
    BigDecimal calculateTotalOutstandingAdvances();

    // ===============================================
    // AVAILABILITY AND ASSIGNMENT
    // ===============================================
    
    /**
     * Get available drivers (not assigned to running trips)
     * @return List of available driver DTOs
     */
    List<DriverDTO> getAvailableDrivers();
    
    /**
     * Get drivers with active trips
     * @return List of drivers with active trips
     */
    List<DriverDTO> getDriversWithActiveTrips();
    
    /**
     * Check if driver is available for assignment
     * @param id Driver ID
     * @return true if driver is available, false otherwise
     */
    boolean isDriverAvailable(Long id);

    // ===============================================
    // PERFORMANCE ANALYTICS
    // ===============================================
    
    /**
     * Get driver performance summary
     * @return List of driver performance data
     */
    List<Object[]> getDriverPerformanceSummary();
    
    /**
     * Get top performing drivers
     * @param pageable Pagination information
     * @return Page of top performing drivers
     */
    Page<DriverDTO> getTopPerformingDrivers(Pageable pageable);
    
    /**
     * Get driver trip counts for date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of driver trip count data
     */
    List<Object[]> getDriverTripCounts(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // REPORTING
    // ===============================================
    
    /**
     * Get monthly driver summary
     * @param startDate Start date
     * @param endDate End date
     * @return List of monthly summary data
     */
    List<Object[]> getMonthlyDriverSummary(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get driver statistics
     * @return Driver statistics array
     */
    Object[] getDriverStatistics();
    
    /**
     * Generate driver report for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Driver report data
     */
    List<DriverDTO> generateDriverReport(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    /**
     * Validate driver data for creation
     * @param driverDTO Driver data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateDriverForCreation(DriverDTO driverDTO);
    
    /**
     * Validate driver data for update
     * @param id Driver ID
     * @param driverDTO Driver data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateDriverForUpdate(Long id, DriverDTO driverDTO);
    
    /**
     * Check if license number is unique
     * @param licenseNumber License number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isLicenseNumberUnique(String licenseNumber, Long excludeId);
    
    /**
     * Check if contact number is unique
     * @param contactNumber Contact number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isContactNumberUnique(String contactNumber, Long excludeId);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    /**
     * Convert Driver entity to DTO
     * @param driver Driver entity
     * @return Driver DTO
     */
    DriverDTO convertToDTO(Driver driver);
    
    /**
     * Convert DriverDTO to entity
     * @param driverDTO Driver DTO
     * @return Driver entity
     */
    Driver convertToEntity(DriverDTO driverDTO);
    
    /**
     * Get driver count by status
     * @param isActive Active status
     * @return Count of drivers
     */
    long getDriverCount(Boolean isActive);
}
