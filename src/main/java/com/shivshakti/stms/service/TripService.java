package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.TripDTO;
import com.shivshakti.stms.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Trip management
 * Defines business operations for trip-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface TripService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    /**
     * Create a new trip
     * @param tripDTO Trip data transfer object
     * @return Created trip DTO
     */
    TripDTO createTrip(TripDTO tripDTO);
    
    /**
     * Update an existing trip
     * @param id Trip ID
     * @param tripDTO Updated trip data
     * @return Updated trip DTO
     */
    TripDTO updateTrip(Long id, TripDTO tripDTO);
    
    /**
     * Get trip by ID
     * @param id Trip ID
     * @return Trip DTO if found
     */
    Optional<TripDTO> getTripById(Long id);
    
    /**
     * Get all trips with pagination
     * @param pageable Pagination information
     * @return Page of trip DTOs
     */
    Page<TripDTO> getAllTrips(Pageable pageable);
    
    /**
     * Delete a trip (only if not started)
     * @param id Trip ID
     */
    void deleteTrip(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    /**
     * Search trips by multiple criteria
     * @param tripNumber Trip number (partial match)
     * @param truckId Truck ID
     * @param driverId Driver ID
     * @param clientId Client ID
     * @param status Trip status
     * @param startDate Start date range
     * @param endDate End date range
     * @param pageable Pagination information
     * @return Page of matching trip DTOs
     */
    Page<TripDTO> searchTrips(String tripNumber, Long truckId, Long driverId, Long clientId,
                             String status, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find trip by trip number
     * @param tripNumber Trip number
     * @return Trip DTO if found
     */
    Optional<TripDTO> findByTripNumber(String tripNumber);
    
    /**
     * Get trips by status
     * @param status Trip status
     * @param pageable Pagination information
     * @return Page of trips with specified status
     */
    Page<TripDTO> getTripsByStatus(String status, Pageable pageable);
    
    /**
     * Get trips by truck
     * @param truckId Truck ID
     * @param pageable Pagination information
     * @return Page of trips for specified truck
     */
    Page<TripDTO> getTripsByTruck(Long truckId, Pageable pageable);
    
    /**
     * Get trips by driver
     * @param driverId Driver ID
     * @param pageable Pagination information
     * @return Page of trips for specified driver
     */
    Page<TripDTO> getTripsByDriver(Long driverId, Pageable pageable);
    
    /**
     * Get trips by client
     * @param clientId Client ID
     * @param pageable Pagination information
     * @return Page of trips for specified client
     */
    Page<TripDTO> getTripsByClient(Long clientId, Pageable pageable);

    // ===============================================
    // TRIP STATUS MANAGEMENT
    // ===============================================
    
    /**
     * Start a trip
     * @param id Trip ID
     * @param actualStartDate Actual start date and time
     * @return Updated trip DTO
     */
    TripDTO startTrip(Long id, LocalDateTime actualStartDate);
    
    /**
     * Complete a trip
     * @param id Trip ID
     * @param actualEndDate Actual end date and time
     * @param fuelConsumed Fuel consumed during trip
     * @param fuelCost Total fuel cost
     * @param tollCharges Toll charges paid
     * @param otherExpenses Other expenses incurred
     * @param remarks Trip completion remarks
     * @return Updated trip DTO
     */
    TripDTO completeTrip(Long id, LocalDateTime actualEndDate, BigDecimal fuelConsumed,
                        BigDecimal fuelCost, BigDecimal tollCharges, BigDecimal otherExpenses,
                        String remarks);
    
    /**
     * Cancel a trip
     * @param id Trip ID
     * @param reason Cancellation reason
     * @return Updated trip DTO
     */
    TripDTO cancelTrip(Long id, String reason);
    
    /**
     * Get planned trips
     * @param pageable Pagination information
     * @return Page of planned trips
     */
    Page<TripDTO> getPlannedTrips(Pageable pageable);
    
    /**
     * Get running trips
     * @param pageable Pagination information
     * @return Page of running trips
     */
    Page<TripDTO> getRunningTrips(Pageable pageable);
    
    /**
     * Get completed trips
     * @param pageable Pagination information
     * @return Page of completed trips
     */
    Page<TripDTO> getCompletedTrips(Pageable pageable);
    
    /**
     * Get cancelled trips
     * @param pageable Pagination information
     * @return Page of cancelled trips
     */
    Page<TripDTO> getCancelledTrips(Pageable pageable);

    // ===============================================
    // TRIP PLANNING AND SCHEDULING
    // ===============================================
    
    /**
     * Get available trucks for trip planning
     * @param requiredCapacity Required capacity
     * @param plannedStartDate Planned start date
     * @return List of available trucks
     */
    List<Object[]> getAvailableTrucksForTrip(BigDecimal requiredCapacity, LocalDateTime plannedStartDate);
    
    /**
     * Get available drivers for trip planning
     * @param plannedStartDate Planned start date
     * @return List of available drivers
     */
    List<Object[]> getAvailableDriversForTrip(LocalDateTime plannedStartDate);
    
    /**
     * Calculate estimated trip duration
     * @param distance Distance in kilometers
     * @param averageSpeed Average speed in km/hour
     * @return Estimated duration in hours
     */
    Long calculateEstimatedDuration(BigDecimal distance, BigDecimal averageSpeed);
    
    /**
     * Calculate estimated fuel cost
     * @param distance Distance in kilometers
     * @param fuelEfficiency Fuel efficiency in km/liter
     * @param fuelPricePerLiter Fuel price per liter
     * @return Estimated fuel cost
     */
    BigDecimal calculateEstimatedFuelCost(BigDecimal distance, BigDecimal fuelEfficiency, BigDecimal fuelPricePerLiter);
    
    /**
     * Suggest optimal truck for trip
     * @param loadWeight Load weight in tons
     * @param distance Distance in kilometers
     * @param plannedStartDate Planned start date
     * @return Suggested truck information
     */
    Optional<Object[]> suggestOptimalTruck(BigDecimal loadWeight, BigDecimal distance, LocalDateTime plannedStartDate);

    // ===============================================
    // FINANCIAL MANAGEMENT
    // ===============================================
    
    /**
     * Update trip charges
     * @param id Trip ID
     * @param newCharges New trip charges
     * @param reason Reason for change
     * @return Updated trip DTO
     */
    TripDTO updateTripCharges(Long id, BigDecimal newCharges, String reason);
    
    /**
     * Add advance payment
     * @param id Trip ID
     * @param advanceAmount Advance amount
     * @param remarks Advance payment remarks
     * @return Updated trip DTO
     */
    TripDTO addAdvancePayment(Long id, BigDecimal advanceAmount, String remarks);
    
    /**
     * Calculate trip profitability
     * @param id Trip ID
     * @return Profitability analysis
     */
    Object[] calculateTripProfitability(Long id);
    
    /**
     * Get trips with outstanding balance
     * @param pageable Pagination information
     * @return Page of trips with outstanding balance
     */
    Page<TripDTO> getTripsWithOutstandingBalance(Pageable pageable);
    
    /**
     * Get profitable trips
     * @param minProfitMargin Minimum profit margin percentage
     * @param pageable Pagination information
     * @return Page of profitable trips
     */
    Page<TripDTO> getProfitableTrips(BigDecimal minProfitMargin, Pageable pageable);
    
    /**
     * Get loss-making trips
     * @param pageable Pagination information
     * @return Page of loss-making trips
     */
    Page<TripDTO> getLossMakingTrips(Pageable pageable);

    // ===============================================
    // PERFORMANCE ANALYTICS
    // ===============================================
    
    /**
     * Get trip performance summary
     * @param startDate Start date
     * @param endDate End date
     * @return Trip performance data
     */
    List<Object[]> getTripPerformanceSummary(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get fuel efficiency report
     * @param startDate Start date
     * @param endDate End date
     * @return Fuel efficiency data by trip
     */
    List<Object[]> getFuelEfficiencyReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get route analysis
     * @param sourceLocation Source location
     * @param destinationLocation Destination location
     * @return Route analysis data
     */
    List<Object[]> getRouteAnalysis(String sourceLocation, String destinationLocation);
    
    /**
     * Get delayed trips
     * @param pageable Pagination information
     * @return Page of delayed trips
     */
    Page<TripDTO> getDelayedTrips(Pageable pageable);
    
    /**
     * Get overloaded trips
     * @param pageable Pagination information
     * @return Page of overloaded trips
     */
    Page<TripDTO> getOverloadedTrips(Pageable pageable);
    
    /**
     * Get capacity utilization report
     * @param startDate Start date
     * @param endDate End date
     * @return Capacity utilization data
     */
    List<Object[]> getCapacityUtilizationReport(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // REPORTING
    // ===============================================
    
    /**
     * Get daily trip summary
     * @param date Date
     * @return Daily trip summary
     */
    Object[] getDailyTripSummary(LocalDate date);
    
    /**
     * Get monthly trip summary
     * @param startDate Start date
     * @param endDate End date
     * @return Monthly trip summary data
     */
    List<Object[]> getMonthlyTripSummary(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get trip statistics
     * @return Trip statistics array
     */
    Object[] getTripStatistics();
    
    /**
     * Generate trip report
     * @param startDate Start date
     * @param endDate End date
     * @param status Trip status filter
     * @return Trip report data
     */
    List<TripDTO> generateTripReport(LocalDate startDate, LocalDate endDate, String status);
    
    /**
     * Generate profitability report
     * @param startDate Start date
     * @param endDate End date
     * @return Profitability report data
     */
    List<Object[]> generateProfitabilityReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate driver performance report
     * @param startDate Start date
     * @param endDate End date
     * @return Driver performance data
     */
    List<Object[]> generateDriverPerformanceReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate truck utilization report
     * @param startDate Start date
     * @param endDate End date
     * @return Truck utilization data
     */
    List<Object[]> generateTruckUtilizationReport(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    /**
     * Validate trip data for creation
     * @param tripDTO Trip data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateTripForCreation(TripDTO tripDTO);
    
    /**
     * Validate trip data for update
     * @param id Trip ID
     * @param tripDTO Trip data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateTripForUpdate(Long id, TripDTO tripDTO);
    
    /**
     * Check if trip number is unique
     * @param tripNumber Trip number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isTripNumberUnique(String tripNumber, Long excludeId);
    
    /**
     * Validate truck availability
     * @param truckId Truck ID
     * @param plannedStartDate Planned start date
     * @param plannedEndDate Planned end date
     * @param excludeTripId Trip ID to exclude from check
     * @return true if available, false otherwise
     */
    boolean isTruckAvailable(Long truckId, LocalDateTime plannedStartDate, 
                           LocalDateTime plannedEndDate, Long excludeTripId);
    
    /**
     * Validate driver availability
     * @param driverId Driver ID
     * @param plannedStartDate Planned start date
     * @param plannedEndDate Planned end date
     * @param excludeTripId Trip ID to exclude from check
     * @return true if available, false otherwise
     */
    boolean isDriverAvailable(Long driverId, LocalDateTime plannedStartDate, 
                            LocalDateTime plannedEndDate, Long excludeTripId);
    
    /**
     * Validate load capacity
     * @param truckId Truck ID
     * @param loadWeight Load weight
     * @return true if within capacity, false otherwise
     */
    boolean isLoadWithinCapacity(Long truckId, BigDecimal loadWeight);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    /**
     * Convert Trip entity to DTO
     * @param trip Trip entity
     * @return Trip DTO
     */
    TripDTO convertToDTO(Trip trip);
    
    /**
     * Convert TripDTO to entity
     * @param tripDTO Trip DTO
     * @return Trip entity
     */
    Trip convertToEntity(TripDTO tripDTO);
    
    /**
     * Generate unique trip number
     * @return Unique trip number
     */
    String generateTripNumber();
    
    /**
     * Calculate distance between locations
     * @param sourceLocation Source location
     * @param destinationLocation Destination location
     * @return Distance in kilometers (estimated)
     */
    BigDecimal calculateDistance(String sourceLocation, String destinationLocation);
    
    /**
     * Get trip count by status
     * @param status Trip status
     * @return Count of trips
     */
    long getTripCount(String status);
    
    /**
     * Get total revenue for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue
     */
    BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get total expenses for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Total expenses
     */
    BigDecimal getTotalExpenses(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get net profit for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Net profit
     */
    BigDecimal getNetProfit(LocalDate startDate, LocalDate endDate);
}
