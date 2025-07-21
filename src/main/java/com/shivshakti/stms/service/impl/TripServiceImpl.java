package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.TripDTO;
import com.shivshakti.stms.entity.Trip;
import com.shivshakti.stms.entity.Truck;
import com.shivshakti.stms.entity.Driver;
import com.shivshakti.stms.entity.Client;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.exception.DuplicateResourceException;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.repository.TripRepository;
import com.shivshakti.stms.repository.TruckRepository;
import com.shivshakti.stms.repository.DriverRepository;
import com.shivshakti.stms.repository.ClientRepository;
import com.shivshakti.stms.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TripService interface
 * Provides comprehensive trip management functionality with business logic
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class TripServiceImpl implements TripService {

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);
    
    private final TripRepository tripRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    private final ClientRepository clientRepository;
    
    @Autowired
    public TripServiceImpl(TripRepository tripRepository, TruckRepository truckRepository,
                          DriverRepository driverRepository, ClientRepository clientRepository) {
        this.tripRepository = tripRepository;
        this.truckRepository = truckRepository;
        this.driverRepository = driverRepository;
        this.clientRepository = clientRepository;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    @Override
    public TripDTO createTrip(TripDTO tripDTO) {
        logger.info("Creating new trip: {}", tripDTO.getTripNumber());
        
        // Validate trip data
        validateTripForCreation(tripDTO);
        
        // Generate trip number if not provided
        if (!StringUtils.hasText(tripDTO.getTripNumber())) {
            tripDTO.setTripNumber(generateTripNumber());
        }
        
        // Convert DTO to entity
        Trip trip = convertToEntity(tripDTO);
        
        // Set default values
        if (trip.getCreatedDate() == null) {
            trip.setCreatedDate(LocalDate.now());
        }
        
        // Calculate planned end date if not provided
        if (trip.getPlannedEndDate() == null && trip.getDistance() != null) {
            Long estimatedDuration = calculateEstimatedDuration(trip.getDistance(), BigDecimal.valueOf(50)); // 50 km/h average
            trip.setPlannedEndDate(trip.getPlannedStartDate().plusHours(estimatedDuration));
        }
        
        // Save trip
        Trip savedTrip = tripRepository.save(trip);
        
        logger.info("Successfully created trip with ID: {}", savedTrip.getId());
        return convertToDTO(savedTrip);
    }
    
    @Override
    public TripDTO updateTrip(Long id, TripDTO tripDTO) {
        logger.info("Updating trip with ID: {}", id);
        
        // Validate trip data
        validateTripForUpdate(id, tripDTO);
        
        // Find existing trip
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        // Check if trip can be updated
        if ("COMPLETED".equals(existingTrip.getStatus()) || "CANCELLED".equals(existingTrip.getStatus())) {
            throw new BusinessValidationException("Cannot update completed or cancelled trip");
        }
        
        // Update fields
        updateTripFields(existingTrip, tripDTO);
        
        // Save updated trip
        Trip updatedTrip = tripRepository.save(existingTrip);
        
        logger.info("Successfully updated trip with ID: {}", id);
        return convertToDTO(updatedTrip);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TripDTO> getTripById(Long id) {
        logger.debug("Fetching trip with ID: {}", id);
        
        return tripRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getAllTrips(Pageable pageable) {
        logger.debug("Fetching all trips with pagination: {}", pageable);
        
        return tripRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public void deleteTrip(Long id) {
        logger.info("Deleting trip with ID: {}", id);
        
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        // Check if trip can be deleted
        if (!"PLANNED".equals(trip.getStatus())) {
            throw new BusinessValidationException("Can only delete planned trips");
        }
        
        tripRepository.delete(trip);
        
        logger.info("Successfully deleted trip with ID: {}", id);
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> searchTrips(String tripNumber, Long truckId, Long driverId, Long clientId,
                                    String status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.debug("Searching trips with criteria - number: {}, truck: {}, driver: {}, client: {}, status: {}", 
                    tripNumber, truckId, driverId, clientId, status);
        
        return tripRepository.searchTrips(tripNumber, truckId, driverId, clientId, status, 
                                         startDate, endDate, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TripDTO> findByTripNumber(String tripNumber) {
        logger.debug("Finding trip by trip number: {}", tripNumber);
        
        return tripRepository.findByTripNumber(tripNumber)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByStatus(String status, Pageable pageable) {
        logger.debug("Fetching trips by status: {}", status);
        
        return tripRepository.findByStatus(status, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByTruck(Long truckId, Pageable pageable) {
        logger.debug("Fetching trips by truck ID: {}", truckId);
        
        return tripRepository.findByTruckId(truckId, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByDriver(Long driverId, Pageable pageable) {
        logger.debug("Fetching trips by driver ID: {}", driverId);
        
        return tripRepository.findByDriverId(driverId, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByClient(Long clientId, Pageable pageable) {
        logger.debug("Fetching trips by client ID: {}", clientId);
        
        return tripRepository.findByClientId(clientId, pageable)
                .map(this::convertToDTO);
    }

    // ===============================================
    // TRIP STATUS MANAGEMENT
    // ===============================================
    
    @Override
    public TripDTO startTrip(Long id, LocalDateTime actualStartDate) {
        logger.info("Starting trip with ID: {}", id);
        
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        // Validate trip can be started
        if (!"PLANNED".equals(trip.getStatus())) {
            throw new BusinessValidationException("Can only start planned trips");
        }
        
        // Validate start date
        if (actualStartDate.isBefore(LocalDateTime.now().minusHours(1))) {
            throw new BusinessValidationException("Start date cannot be more than 1 hour in the past");
        }
        
        // Update trip status and start date
        trip.setStatus("RUNNING");
        trip.setActualStartDate(actualStartDate);
        
        // Add audit trail
        String auditMessage = String.format("Trip started on %s", 
                                           actualStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addToRemarks(trip, auditMessage);
        
        Trip updatedTrip = tripRepository.save(trip);
        
        logger.info("Successfully started trip with ID: {}", id);
        return convertToDTO(updatedTrip);
    }
    
    @Override
    public TripDTO completeTrip(Long id, LocalDateTime actualEndDate, BigDecimal fuelConsumed,
                               BigDecimal fuelCost, BigDecimal tollCharges, BigDecimal otherExpenses,
                               String remarks) {
        logger.info("Completing trip with ID: {}", id);
        
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        // Validate trip can be completed
        if (!"RUNNING".equals(trip.getStatus())) {
            throw new BusinessValidationException("Can only complete running trips");
        }
        
        // Validate end date
        if (trip.getActualStartDate() != null && actualEndDate.isBefore(trip.getActualStartDate())) {
            throw new BusinessValidationException("End date cannot be before start date");
        }
        
        // Update trip with completion data
        trip.setStatus("COMPLETED");
        trip.setActualEndDate(actualEndDate);
        trip.setFuelConsumed(fuelConsumed);
        trip.setFuelCost(fuelCost);
        trip.setTollCharges(tollCharges != null ? tollCharges : BigDecimal.ZERO);
        trip.setOtherExpenses(otherExpenses != null ? otherExpenses : BigDecimal.ZERO);
        
        // Add completion audit trail
        String auditMessage = String.format("Trip completed on %s", 
                                           actualEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(trip, auditMessage);
        
        // Update truck odometer if possible
        updateTruckOdometer(trip);
        
        Trip updatedTrip = tripRepository.save(trip);
        
        logger.info("Successfully completed trip with ID: {}", id);
        return convertToDTO(updatedTrip);
    }
    
    @Override
    public TripDTO cancelTrip(Long id, String reason) {
        logger.info("Cancelling trip with ID: {}", id);
        
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        // Validate trip can be cancelled
        if ("COMPLETED".equals(trip.getStatus()) || "CANCELLED".equals(trip.getStatus())) {
            throw new BusinessValidationException("Cannot cancel completed or already cancelled trip");
        }
        
        // Update trip status
        trip.setStatus("CANCELLED");
        
        // Add cancellation audit trail
        String auditMessage = String.format("Trip cancelled on %s", LocalDateTime.now()
                                           .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (StringUtils.hasText(reason)) {
            auditMessage += " - Reason: " + reason;
        }
        addToRemarks(trip, auditMessage);
        
        Trip updatedTrip = tripRepository.save(trip);
        
        logger.info("Successfully cancelled trip with ID: {}", id);
        return convertToDTO(updatedTrip);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getPlannedTrips(Pageable pageable) {
        logger.debug("Fetching planned trips");
        
        return tripRepository.findByStatus("PLANNED", pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getRunningTrips(Pageable pageable) {
        logger.debug("Fetching running trips");
        
        return tripRepository.findByStatus("RUNNING", pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getCompletedTrips(Pageable pageable) {
        logger.debug("Fetching completed trips");
        
        return tripRepository.findByStatus("COMPLETED", pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getCancelledTrips(Pageable pageable) {
        logger.debug("Fetching cancelled trips");
        
        return tripRepository.findByStatus("CANCELLED", pageable)
                .map(this::convertToDTO);
    }

    // ===============================================
    // TRIP PLANNING AND SCHEDULING
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAvailableTrucksForTrip(BigDecimal requiredCapacity, LocalDateTime plannedStartDate) {
        logger.debug("Finding available trucks for capacity: {} on date: {}", requiredCapacity, plannedStartDate);
        
        return tripRepository.findAvailableTrucksForTrip(requiredCapacity, plannedStartDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAvailableDriversForTrip(LocalDateTime plannedStartDate) {
        logger.debug("Finding available drivers for date: {}", plannedStartDate);
        
        return tripRepository.findAvailableDriversForTrip(plannedStartDate);
    }
    
    @Override
    public Long calculateEstimatedDuration(BigDecimal distance, BigDecimal averageSpeed) {
        if (distance == null || averageSpeed == null || averageSpeed.compareTo(BigDecimal.ZERO) == 0) {
            return 0L;
        }
        
        return distance.divide(averageSpeed, 2, RoundingMode.HALF_UP).longValue();
    }
    
    @Override
    public BigDecimal calculateEstimatedFuelCost(BigDecimal distance, BigDecimal fuelEfficiency, BigDecimal fuelPricePerLiter) {
        if (distance == null || fuelEfficiency == null || fuelPricePerLiter == null ||
            fuelEfficiency.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal fuelRequired = distance.divide(fuelEfficiency, 2, RoundingMode.HALF_UP);
        return fuelRequired.multiply(fuelPricePerLiter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Object[]> suggestOptimalTruck(BigDecimal loadWeight, BigDecimal distance, LocalDateTime plannedStartDate) {
        logger.debug("Suggesting optimal truck for load: {} tons, distance: {} km", loadWeight, distance);
        
        return tripRepository.findOptimalTruckForTrip(loadWeight, distance, plannedStartDate);
    }

    // ===============================================
    // FINANCIAL MANAGEMENT
    // ===============================================
    
    @Override
    public TripDTO updateTripCharges(Long id, BigDecimal newCharges, String reason) {
        logger.info("Updating trip charges for trip ID: {} to {}", id, newCharges);
        
        if (newCharges.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Trip charges must be greater than zero");
        }
        
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        BigDecimal oldCharges = trip.getTripCharges();
        trip.setTripCharges(newCharges);
        
        // Add audit trail
        String auditMessage = String.format("Trip charges updated from %s to %s on %s", 
                                           oldCharges, newCharges, LocalDate.now());
        if (StringUtils.hasText(reason)) {
            auditMessage += " - Reason: " + reason;
        }
        addToRemarks(trip, auditMessage);
        
        Trip updatedTrip = tripRepository.save(trip);
        
        logger.info("Successfully updated trip charges for trip ID: {}", id);
        return convertToDTO(updatedTrip);
    }
    
    @Override
    public TripDTO addAdvancePayment(Long id, BigDecimal advanceAmount, String remarks) {
        logger.info("Adding advance payment of {} for trip ID: {}", advanceAmount, id);
        
        if (advanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Advance amount must be greater than zero");
        }
        
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        BigDecimal currentAdvance = trip.getAdvanceAmount() != null ? 
                                   trip.getAdvanceAmount() : BigDecimal.ZERO;
        BigDecimal newAdvance = currentAdvance.add(advanceAmount);
        
        // Validate advance doesn't exceed trip charges
        if (trip.getTripCharges() != null && newAdvance.compareTo(trip.getTripCharges()) > 0) {
            throw new BusinessValidationException("Total advance cannot exceed trip charges");
        }
        
        trip.setAdvanceAmount(newAdvance);
        
        // Add audit trail
        String auditMessage = String.format("Advance payment of %s added on %s. Total advance: %s", 
                                           advanceAmount, LocalDate.now(), newAdvance);
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(trip, auditMessage);
        
        Trip updatedTrip = tripRepository.save(trip);
        
        logger.info("Successfully added advance payment for trip ID: {}", id);
        return convertToDTO(updatedTrip);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object[] calculateTripProfitability(Long id) {
        logger.debug("Calculating profitability for trip ID: {}", id);
        
        return tripRepository.calculateTripProfitability(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsWithOutstandingBalance(Pageable pageable) {
        logger.debug("Fetching trips with outstanding balance");
        
        return tripRepository.findTripsWithOutstandingBalance(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getProfitableTrips(BigDecimal minProfitMargin, Pageable pageable) {
        logger.debug("Fetching profitable trips with min margin: {}", minProfitMargin);
        
        return tripRepository.findProfitableTrips(minProfitMargin, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getLossMakingTrips(Pageable pageable) {
        logger.debug("Fetching loss-making trips");
        
        return tripRepository.findLossMakingTrips(pageable)
                .map(this::convertToDTO);
    }

    // ===============================================
    // PERFORMANCE ANALYTICS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTripPerformanceSummary(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating trip performance summary from {} to {}", startDate, endDate);
        
        return tripRepository.getTripPerformanceSummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getFuelEfficiencyReport(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating fuel efficiency report from {} to {}", startDate, endDate);
        
        return tripRepository.getFuelEfficiencyReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getRouteAnalysis(String sourceLocation, String destinationLocation) {
        logger.debug("Analyzing route from {} to {}", sourceLocation, destinationLocation);
        
        return tripRepository.getRouteAnalysis(sourceLocation, destinationLocation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getDelayedTrips(Pageable pageable) {
        logger.debug("Fetching delayed trips");
        
        return tripRepository.findDelayedTrips(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getOverloadedTrips(Pageable pageable) {
        logger.debug("Fetching overloaded trips");
        
        return tripRepository.findOverloadedTrips(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCapacityUtilizationReport(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating capacity utilization report from {} to {}", startDate, endDate);
        
        return tripRepository.getCapacityUtilizationReport(startDate, endDate);
    }

    // ===============================================
    // REPORTING
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getDailyTripSummary(LocalDate date) {
        logger.debug("Generating daily trip summary for {}", date);
        
        return tripRepository.getDailyTripSummary(date);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyTripSummary(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating monthly trip summary from {} to {}", startDate, endDate);
        
        return tripRepository.getMonthlyTripSummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getTripStatistics() {
        logger.debug("Fetching trip statistics");
        
        return tripRepository.getTripStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> generateTripReport(LocalDate startDate, LocalDate endDate, String status) {
        logger.info("Generating trip report from {} to {} with status: {}", startDate, endDate, status);
        
        return tripRepository.findTripsForReport(startDate, endDate, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> generateProfitabilityReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating profitability report from {} to {}", startDate, endDate);
        
        return tripRepository.generateProfitabilityReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> generateDriverPerformanceReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating driver performance report from {} to {}", startDate, endDate);
        
        return tripRepository.generateDriverPerformanceReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> generateTruckUtilizationReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating truck utilization report from {} to {}", startDate, endDate);
        
        return tripRepository.generateTruckUtilizationReport(startDate, endDate);
    }

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    @Override
    public void validateTripForCreation(TripDTO tripDTO) {
        logger.debug("Validating trip data for creation");
        
        // Check trip number uniqueness
        if (StringUtils.hasText(tripDTO.getTripNumber()) && 
            !isTripNumberUnique(tripDTO.getTripNumber(), null)) {
            throw new DuplicateResourceException("Trip", "trip number", tripDTO.getTripNumber());
        }
        
        // Validate truck exists and is available
        if (tripDTO.getTruckId() != null) {
            Truck truck = truckRepository.findById(tripDTO.getTruckId())
                    .orElseThrow(() -> new ResourceNotFoundException("Truck", tripDTO.getTruckId()));
            
            if (!truck.getIsActive()) {
                throw new BusinessValidationException("Cannot assign inactive truck to trip");
            }
            
            // Check truck availability
            if (!isTruckAvailable(tripDTO.getTruckId(), tripDTO.getPlannedStartDate(), 
                                tripDTO.getPlannedEndDate(), null)) {
                throw new BusinessValidationException("Truck is not available for the planned dates");
            }
            
            // Check load capacity
            if (tripDTO.getLoadWeight() != null && 
                !isLoadWithinCapacity(tripDTO.getTruckId(), tripDTO.getLoadWeight())) {
                throw new BusinessValidationException("Load weight exceeds truck capacity");
            }
        }
        
        // Validate driver exists and is available
        if (tripDTO.getDriverId() != null) {
            Driver driver = driverRepository.findById(tripDTO.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", tripDTO.getDriverId()));
            
            if (!driver.getIsActive()) {
                throw new BusinessValidationException("Cannot assign inactive driver to trip");
            }
            
            // Check driver availability
            if (!isDriverAvailable(tripDTO.getDriverId(), tripDTO.getPlannedStartDate(), 
                                 tripDTO.getPlannedEndDate(), null)) {
                throw new BusinessValidationException("Driver is not available for the planned dates");
            }
        }
        
        // Validate client exists and is active
        if (tripDTO.getClientId() != null) {
            Client client = clientRepository.findById(tripDTO.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", tripDTO.getClientId()));
            
            if (!client.getIsActive()) {
                throw new BusinessValidationException("Cannot create trip for inactive client");
            }
        }
        
        // Validate planned dates
        if (tripDTO.getPlannedEndDate() != null && 
            tripDTO.getPlannedEndDate().isBefore(tripDTO.getPlannedStartDate())) {
            throw new BusinessValidationException("Planned end date cannot be before start date");
        }
        
        // Validate trip charges
        if (tripDTO.getTripCharges().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Trip charges must be greater than zero");
        }
        
        // Validate advance amount
        if (tripDTO.getAdvanceAmount() != null && 
            tripDTO.getAdvanceAmount().compareTo(tripDTO.getTripCharges()) > 0) {
            throw new BusinessValidationException("Advance amount cannot exceed trip charges");
        }
    }
    
    @Override
    public void validateTripForUpdate(Long id, TripDTO tripDTO) {
        logger.debug("Validating trip data for update");
        
        // Check trip number uniqueness
        if (StringUtils.hasText(tripDTO.getTripNumber()) && 
            !isTripNumberUnique(tripDTO.getTripNumber(), id)) {
            throw new DuplicateResourceException("Trip", "trip number", tripDTO.getTripNumber());
        }
        
        // Get existing trip for validation
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        // Similar validations as creation, but considering existing trip state
        validateTripForCreation(tripDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isTripNumberUnique(String tripNumber, Long excludeId) {
        if (excludeId != null) {
            return !tripRepository.existsByTripNumberAndIdNot(tripNumber, excludeId);
        }
        return tripRepository.findByTripNumber(tripNumber).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isTruckAvailable(Long truckId, LocalDateTime plannedStartDate, 
                                  LocalDateTime plannedEndDate, Long excludeTripId) {
        return tripRepository.isTruckAvailable(truckId, plannedStartDate, plannedEndDate, excludeTripId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isDriverAvailable(Long driverId, LocalDateTime plannedStartDate, 
                                   LocalDateTime plannedEndDate, Long excludeTripId) {
        return tripRepository.isDriverAvailable(driverId, plannedStartDate, plannedEndDate, excludeTripId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isLoadWithinCapacity(Long truckId, BigDecimal loadWeight) {
        Truck truck = truckRepository.findById(truckId).orElse(null);
        if (truck == null || truck.getCapacity() == null || loadWeight == null) {
            return false;
        }
        return loadWeight.compareTo(truck.getCapacity()) <= 0;
    }
    
    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    @Override
    public TripDTO convertToDTO(Trip trip) {
        if (trip == null) {
            return null;
        }
        
        TripDTO dto = new TripDTO();
        dto.setId(trip.getId());
        dto.setTripNumber(trip.getTripNumber());
        dto.setTruckId(trip.getTruck() != null ? trip.getTruck().getId() : null);
        dto.setDriverId(trip.getDriver() != null ? trip.getDriver().getId() : null);
        dto.setClientId(trip.getClient() != null ? trip.getClient().getId() : null);
        dto.setSourceLocation(trip.getSourceLocation());
        dto.setDestinationLocation(trip.getDestinationLocation());
        dto.setPlannedStartDate(trip.getPlannedStartDate());
        dto.setPlannedEndDate(trip.getPlannedEndDate());
        dto.setActualStartDate(trip.getActualStartDate());
        dto.setActualEndDate(trip.getActualEndDate());
        dto.setDistance(trip.getDistance());
        dto.setLoadWeight(trip.getLoadWeight());
        dto.setLoadDescription(trip.getLoadDescription());
        dto.setTripCharges(trip.getTripCharges());
        dto.setAdvanceAmount(trip.getAdvanceAmount());
        dto.setFuelConsumed(trip.getFuelConsumed());
        dto.setFuelCost(trip.getFuelCost());
        dto.setTollCharges(trip.getTollCharges());
        dto.setOtherExpenses(trip.getOtherExpenses());
        dto.setStatus(trip.getStatus());
        dto.setRemarks(trip.getRemarks());
        dto.setCreatedDate(trip.getCreatedDate());
        
        // Set related entity information
        if (trip.getTruck() != null) {
            dto.setTruckNumber(trip.getTruck().getTruckNumber());
            dto.setTruckModel(trip.getTruck().getModel());
            dto.setTruckCapacity(trip.getTruck().getCapacity());
        }
        
        if (trip.getDriver() != null) {
            dto.setDriverName(trip.getDriver().getName());
        }
        
        if (trip.getClient() != null) {
            dto.setClientName(trip.getClient().getName());
        }
        
        // Calculate derived fields
        dto.setTotalExpenses(dto.calculateTotalExpenses());
        dto.setNetProfit(dto.calculateNetProfit());
        dto.setProfitMargin(dto.calculateProfitMargin());
        dto.setFuelEfficiency(dto.calculateFuelEfficiency());
        dto.setCapacityUtilization(dto.calculateCapacityUtilization());
        
        // Calculate duration and average speed
        if (trip.getActualStartDate() != null && trip.getActualEndDate() != null) {
            long hours = ChronoUnit.HOURS.between(trip.getActualStartDate(), trip.getActualEndDate());
            dto.setDurationHours(hours);
            
            if (hours > 0 && trip.getDistance() != null) {
                dto.setAverageSpeed(trip.getDistance().divide(BigDecimal.valueOf(hours), 2, RoundingMode.HALF_UP));
            }
        }
        
        // Calculate balance amount
        if (trip.getTripCharges() != null && trip.getAdvanceAmount() != null) {
            dto.setBalanceAmount(trip.getTripCharges().subtract(trip.getAdvanceAmount()));
        }
        
        return dto;
    }
    
    @Override
    public Trip convertToEntity(TripDTO tripDTO) {
        if (tripDTO == null) {
            return null;
        }
        
        Trip trip = new Trip();
        trip.setId(tripDTO.getId());
        trip.setTripNumber(tripDTO.getTripNumber());
        trip.setSourceLocation(tripDTO.getSourceLocation());
        trip.setDestinationLocation(tripDTO.getDestinationLocation());
        trip.setPlannedStartDate(tripDTO.getPlannedStartDate());
        trip.setPlannedEndDate(tripDTO.getPlannedEndDate());
        trip.setActualStartDate(tripDTO.getActualStartDate());
        trip.setActualEndDate(tripDTO.getActualEndDate());
        trip.setDistance(tripDTO.getDistance());
        trip.setLoadWeight(tripDTO.getLoadWeight());
        trip.setLoadDescription(tripDTO.getLoadDescription());
        trip.setTripCharges(tripDTO.getTripCharges());
        trip.setAdvanceAmount(tripDTO.getAdvanceAmount());
        trip.setFuelConsumed(tripDTO.getFuelConsumed());
        trip.setFuelCost(tripDTO.getFuelCost());
        trip.setTollCharges(tripDTO.getTollCharges());
        trip.setOtherExpenses(tripDTO.getOtherExpenses());
        trip.setStatus(tripDTO.getStatus());
        trip.setRemarks(tripDTO.getRemarks());
        trip.setCreatedDate(tripDTO.getCreatedDate());
        
        // Set related entities
        if (tripDTO.getTruckId() != null) {
            Truck truck = truckRepository.findById(tripDTO.getTruckId()).orElse(null);
            trip.setTruck(truck);
        }
        
        if (tripDTO.getDriverId() != null) {
            Driver driver = driverRepository.findById(tripDTO.getDriverId()).orElse(null);
            trip.setDriver(driver);
        }
        
        if (tripDTO.getClientId() != null) {
            Client client = clientRepository.findById(tripDTO.getClientId()).orElse(null);
            trip.setClient(client);
        }
        
        return trip;
    }
    
    @Override
    public String generateTripNumber() {
        String prefix = "TR";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Find the last trip number for today
        String lastTripNumber = tripRepository.findLastTripNumberForDate(datePart);
        
        int sequence = 1;
        if (lastTripNumber != null && lastTripNumber.length() > 10) {
            try {
                sequence = Integer.parseInt(lastTripNumber.substring(10)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Could not parse sequence from trip number: {}", lastTripNumber);
            }
        }
        
        return String.format("%s%s%04d", prefix, datePart, sequence);
    }
    
    @Override
    public BigDecimal calculateDistance(String sourceLocation, String destinationLocation) {
        // This is a simplified distance calculation
        // In a real implementation, you would use a mapping service API
        
        if (!StringUtils.hasText(sourceLocation) || !StringUtils.hasText(destinationLocation)) {
            return BigDecimal.ZERO;
        }
        
        // Simple hash-based distance calculation for demo purposes
        int hash = (sourceLocation + destinationLocation).hashCode();
        return BigDecimal.valueOf(Math.abs(hash % 1000) + 50); // 50-1050 km range
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTripCount(String status) {
        if (StringUtils.hasText(status)) {
            return tripRepository.countByStatus(status);
        }
        return tripRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return tripRepository.calculateTotalRevenue(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        return tripRepository.calculateTotalExpenses(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getNetProfit(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = getTotalRevenue(startDate, endDate);
        BigDecimal expenses = getTotalExpenses(startDate, endDate);
        
        if (revenue == null) revenue = BigDecimal.ZERO;
        if (expenses == null) expenses = BigDecimal.ZERO;
        
        return revenue.subtract(expenses);
    }
    
    // ===============================================
    // PRIVATE HELPER METHODS
    // ===============================================
    
    private void updateTripFields(Trip existingTrip, TripDTO tripDTO) {
        existingTrip.setTripNumber(tripDTO.getTripNumber());
        existingTrip.setSourceLocation(tripDTO.getSourceLocation());
        existingTrip.setDestinationLocation(tripDTO.getDestinationLocation());
        existingTrip.setPlannedStartDate(tripDTO.getPlannedStartDate());
        existingTrip.setPlannedEndDate(tripDTO.getPlannedEndDate());
        existingTrip.setDistance(tripDTO.getDistance());
        existingTrip.setLoadWeight(tripDTO.getLoadWeight());
        existingTrip.setLoadDescription(tripDTO.getLoadDescription());
        existingTrip.setTripCharges(tripDTO.getTripCharges());
        existingTrip.setRemarks(tripDTO.getRemarks());
        
        // Update related entities if changed
        if (tripDTO.getTruckId() != null && 
            (existingTrip.getTruck() == null || !existingTrip.getTruck().getId().equals(tripDTO.getTruckId()))) {
            Truck truck = truckRepository.findById(tripDTO.getTruckId()).orElse(null);
            existingTrip.setTruck(truck);
        }
        
        if (tripDTO.getDriverId() != null && 
            (existingTrip.getDriver() == null || !existingTrip.getDriver().getId().equals(tripDTO.getDriverId()))) {
            Driver driver = driverRepository.findById(tripDTO.getDriverId()).orElse(null);
            existingTrip.setDriver(driver);
        }
        
        if (tripDTO.getClientId() != null && 
            (existingTrip.getClient() == null || !existingTrip.getClient().getId().equals(tripDTO.getClientId()))) {
            Client client = clientRepository.findById(tripDTO.getClientId()).orElse(null);
            existingTrip.setClient(client);
        }
    }
    
    private void addToRemarks(Trip trip, String newRemark) {
        String currentRemarks = trip.getRemarks() != null ? trip.getRemarks() : "";
        trip.setRemarks(currentRemarks + "\n" + newRemark);
    }
    
    private void updateTruckOdometer(Trip trip) {
        if (trip.getTruck() != null && trip.getDistance() != null) {
            try {
                Truck truck = trip.getTruck();
                BigDecimal currentReading = truck.getCurrentOdometerReading() != null ? 
                                          truck.getCurrentOdometerReading() : BigDecimal.ZERO;
                BigDecimal newReading = currentReading.add(trip.getDistance());
                truck.setCurrentOdometerReading(newReading);
                truckRepository.save(truck);
                
                logger.debug("Updated truck {} odometer from {} to {}", 
                           truck.getTruckNumber(), currentReading, newReading);
            } catch (Exception e) {
                logger.warn("Failed to update truck odometer for trip {}: {}", trip.getId(), e.getMessage());
            }
        }
    }
}
