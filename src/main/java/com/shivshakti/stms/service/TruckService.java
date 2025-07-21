package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.TruckDTO;
import com.shivshakti.stms.entity.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Truck management
 * Defines business operations for truck-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface TruckService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    /**
     * Create a new truck
     * @param truckDTO Truck data transfer object
     * @return Created truck DTO
     */
    TruckDTO createTruck(TruckDTO truckDTO);
    
    /**
     * Update an existing truck
     * @param id Truck ID
     * @param truckDTO Updated truck data
     * @return Updated truck DTO
     */
    TruckDTO updateTruck(Long id, TruckDTO truckDTO);
    
    /**
     * Get truck by ID
     * @param id Truck ID
     * @return Truck DTO if found
     */
    Optional<TruckDTO> getTruckById(Long id);
    
    /**
     * Get all active trucks
     * @return List of active truck DTOs
     */
    List<TruckDTO> getAllActiveTrucks();
    
    /**
     * Get all trucks with pagination
     * @param pageable Pagination information
     * @return Page of truck DTOs
     */
    Page<TruckDTO> getAllTrucks(Pageable pageable);
    
    /**
     * Soft delete a truck (mark as inactive)
     * @param id Truck ID
     */
    void deleteTruck(Long id);
    
    /**
     * Activate a truck
     * @param id Truck ID
     */
    void activateTruck(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    /**
     * Search trucks by multiple criteria
     * @param truckNumber Truck number (partial match)
     * @param model Model (partial match)
     * @param fuelType Fuel type
     * @param isActive Active status
     * @param pageable Pagination information
     * @return Page of matching truck DTOs
     */
    Page<TruckDTO> searchTrucks(String truckNumber, String model, String fuelType, 
                               Boolean isActive, Pageable pageable);
    
    /**
     * Find truck by truck number
     * @param truckNumber Truck number
     * @return Truck DTO if found
     */
    Optional<TruckDTO> findByTruckNumber(String truckNumber);
    
    /**
     * Find trucks by capacity range
     * @param minCapacity Minimum capacity
     * @param maxCapacity Maximum capacity
     * @return List of trucks within capacity range
     */
    List<TruckDTO> findByCapacityRange(BigDecimal minCapacity, BigDecimal maxCapacity);
    
    /**
     * Find trucks by fuel type
     * @param fuelType Fuel type
     * @return List of trucks with specified fuel type
     */
    List<TruckDTO> findByFuelType(String fuelType);

    // ===============================================
    // DOCUMENT MANAGEMENT
    // ===============================================
    
    /**
     * Get trucks with expired documents
     * @return List of trucks with expired documents
     */
    List<TruckDTO> getTrucksWithExpiredDocuments();
    
    /**
     * Get trucks with documents expiring soon
     * @param days Number of days to check ahead
     * @return List of trucks with documents expiring soon
     */
    List<TruckDTO> getTrucksWithDocumentsExpiringSoon(int days);
    
    /**
     * Get trucks with expired RC
     * @return List of trucks with expired RC
     */
    List<TruckDTO> getTrucksWithExpiredRC();
    
    /**
     * Get trucks with expired insurance
     * @return List of trucks with expired insurance
     */
    List<TruckDTO> getTrucksWithExpiredInsurance();
    
    /**
     * Get trucks with expired permits
     * @return List of trucks with expired permits
     */
    List<TruckDTO> getTrucksWithExpiredPermits();
    
    /**
     * Get trucks with expired fitness certificates
     * @return List of trucks with expired fitness certificates
     */
    List<TruckDTO> getTrucksWithExpiredFitness();
    
    /**
     * Get trucks with expired PUC certificates
     * @return List of trucks with expired PUC certificates
     */
    List<TruckDTO> getTrucksWithExpiredPUC();
    
    /**
     * Update truck document information
     * @param id Truck ID
     * @param documentType Document type (RC, INSURANCE, PERMIT, FITNESS, PUC)
     * @param documentNumber New document number
     * @param expiryDate New expiry date
     * @return Updated truck DTO
     */
    TruckDTO updateDocumentInfo(Long id, String documentType, String documentNumber, LocalDate expiryDate);

    // ===============================================
    // AVAILABILITY AND ASSIGNMENT
    // ===============================================
    
    /**
     * Get available trucks (not assigned to running trips)
     * @return List of available truck DTOs
     */
    List<TruckDTO> getAvailableTrucks();
    
    /**
     * Get trucks with active trips
     * @return List of trucks with active trips
     */
    List<TruckDTO> getTrucksWithActiveTrips();
    
    /**
     * Check if truck is available for assignment
     * @param id Truck ID
     * @return true if truck is available, false otherwise
     */
    boolean isTruckAvailable(Long id);
    
    /**
     * Get trucks by capacity for specific requirement
     * @param requiredCapacity Required capacity
     * @return List of trucks with capacity >= required capacity
     */
    List<TruckDTO> getTrucksByCapacity(BigDecimal requiredCapacity);

    // ===============================================
    // MAINTENANCE MANAGEMENT
    // ===============================================
    
    /**
     * Get trucks due for service
     * @return List of trucks due for service
     */
    List<TruckDTO> getTrucksDueForService();
    
    /**
     * Update truck odometer reading
     * @param id Truck ID
     * @param newReading New odometer reading
     * @return Updated truck DTO
     */
    TruckDTO updateOdometerReading(Long id, BigDecimal newReading);
    
    /**
     * Update truck service information
     * @param id Truck ID
     * @param serviceDate Service date
     * @param nextServiceDue Next service due (odometer reading)
     * @param remarks Service remarks
     * @return Updated truck DTO
     */
    TruckDTO updateServiceInfo(Long id, LocalDate serviceDate, BigDecimal nextServiceDue, String remarks);
    
    /**
     * Get trucks with high maintenance cost
     * @param threshold Maintenance cost threshold
     * @return List of trucks with maintenance cost above threshold
     */
    List<TruckDTO> getTrucksWithHighMaintenanceCost(BigDecimal threshold);

    // ===============================================
    // PERFORMANCE ANALYTICS
    // ===============================================
    
    /**
     * Get truck performance summary
     * @return List of truck performance data
     */
    List<Object[]> getTruckPerformanceSummary();
    
    /**
     * Get top performing trucks by profitability
     * @param pageable Pagination information
     * @return Page of top performing trucks
     */
    Page<TruckDTO> getTopPerformingTrucks(Pageable pageable);
    
    /**
     * Get trucks with low fuel efficiency
     * @param threshold Fuel efficiency threshold
     * @return List of trucks with fuel efficiency below threshold
     */
    List<TruckDTO> getTrucksWithLowFuelEfficiency(BigDecimal threshold);
    
    /**
     * Get truck utilization report
     * @param startDate Start date
     * @param endDate End date
     * @return List of truck utilization data
     */
    List<Object[]> getTruckUtilizationReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate truck profitability
     * @param id Truck ID
     * @param startDate Start date
     * @param endDate End date
     * @return Profitability amount
     */
    BigDecimal calculateTruckProfitability(Long id, LocalDate startDate, LocalDate endDate);

    // ===============================================
    // FUEL MANAGEMENT
    // ===============================================
    
    /**
     * Update truck fuel efficiency
     * @param id Truck ID
     * @param newEfficiency New fuel efficiency
     * @return Updated truck DTO
     */
    TruckDTO updateFuelEfficiency(Long id, BigDecimal newEfficiency);
    
    /**
     * Get fuel consumption report
     * @param startDate Start date
     * @param endDate End date
     * @return List of fuel consumption data
     */
    List<Object[]> getFuelConsumptionReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get trucks with high fuel consumption
     * @param threshold Fuel consumption threshold
     * @return List of trucks with high fuel consumption
     */
    List<TruckDTO> getTrucksWithHighFuelConsumption(BigDecimal threshold);

    // ===============================================
    // REPORTING
    // ===============================================
    
    /**
     * Get monthly truck summary
     * @param startDate Start date
     * @param endDate End date
     * @return List of monthly summary data
     */
    List<Object[]> getMonthlyTruckSummary(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get truck statistics
     * @return Truck statistics array
     */
    Object[] getTruckStatistics();
    
    /**
     * Generate truck report for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Truck report data
     */
    List<TruckDTO> generateTruckReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate document expiry report
     * @param days Number of days to check ahead
     * @return Document expiry report data
     */
    List<TruckDTO> generateDocumentExpiryReport(int days);

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    /**
     * Validate truck data for creation
     * @param truckDTO Truck data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateTruckForCreation(TruckDTO truckDTO);
    
    /**
     * Validate truck data for update
     * @param id Truck ID
     * @param truckDTO Truck data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateTruckForUpdate(Long id, TruckDTO truckDTO);
    
    /**
     * Check if truck number is unique
     * @param truckNumber Truck number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isTruckNumberUnique(String truckNumber, Long excludeId);
    
    /**
     * Check if RC book number is unique
     * @param rcBookNumber RC book number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isRcBookNumberUnique(String rcBookNumber, Long excludeId);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    /**
     * Convert Truck entity to DTO
     * @param truck Truck entity
     * @return Truck DTO
     */
    TruckDTO convertToDTO(Truck truck);
    
    /**
     * Convert TruckDTO to entity
     * @param truckDTO Truck DTO
     * @return Truck entity
     */
    Truck convertToEntity(TruckDTO truckDTO);
    
    /**
     * Get truck count by status
     * @param isActive Active status
     * @return Count of trucks
     */
    long getTruckCount(Boolean isActive);
    
    /**
     * Calculate depreciation value
     * @param purchasePrice Purchase price
     * @param purchaseDate Purchase date
     * @param depreciationRate Annual depreciation rate (as percentage)
     * @return Current depreciated value
     */
    BigDecimal calculateDepreciationValue(BigDecimal purchasePrice, LocalDate purchaseDate, BigDecimal depreciationRate);
}
