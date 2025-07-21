package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.TripDTO;
import com.shivshakti.stms.entity.Trip;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.repository.TripRepository;
import com.shivshakti.stms.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for Trip management
 * Provides comprehensive business logic for trip operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public TripDTO createTrip(TripDTO tripDTO) {
        validateTripData(tripDTO);
        
        if (tripDTO.getTripNumber() == null || tripDTO.getTripNumber().isEmpty()) {
            tripDTO.setTripNumber(generateTripNumber());
        }
        
        Trip trip = convertToEntity(tripDTO);
        trip.setStatus("PLANNED");
        trip.setCreatedDate(LocalDateTime.now());
        
        Trip savedTrip = tripRepository.save(trip);
        return convertToDTO(savedTrip);
    }

    @Override
    public TripDTO updateTrip(Long id, TripDTO tripDTO) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        validateTripData(tripDTO);
        updateTripFields(existingTrip, tripDTO);
        existingTrip.setModifiedDate(LocalDateTime.now());
        
        Trip updatedTrip = tripRepository.save(existingTrip);
        return convertToDTO(updatedTrip);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TripDTO> getTripById(Long id) {
        return tripRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getAllTrips(Pageable pageable) {
        return tripRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public void deleteTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        if ("RUNNING".equals(trip.getStatus())) {
            throw new BusinessValidationException("Cannot delete a running trip");
        }
        
        tripRepository.delete(trip);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> searchTrips(String tripNumber, Long truckId, Long driverId, Long clientId,
                                    String sourceLocation, String destinationLocation, String status,
                                    LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return tripRepository.searchTrips(tripNumber, truckId, driverId, clientId, 
                                         sourceLocation, destinationLocation, status, 
                                         startDate, endDate, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TripDTO> findByTripNumber(String tripNumber) {
        return tripRepository.findByTripNumber(tripNumber)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByStatus(String status, Pageable pageable) {
        return tripRepository.findByStatus(status, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByTruck(Long truckId, Pageable pageable) {
        return tripRepository.findByTruckId(truckId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByDriver(Long driverId, Pageable pageable) {
        return tripRepository.findByDriverId(driverId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TripDTO> getTripsByClient(Long clientId, Pageable pageable) {
        return tripRepository.findByClientId(clientId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public TripDTO startTrip(Long id, LocalDateTime actualStartTime, String startingOdometer, String remarks) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        if (!"PLANNED".equals(trip.getStatus())) {
            throw new BusinessValidationException("Only planned trips can be started");
        }
        
        trip.setStatus("RUNNING");
        trip.setActualStartDate(actualStartTime != null ? actualStartTime : LocalDateTime.now());
        if (startingOdometer != null) {
            trip.setStartingOdometer(new BigDecimal(startingOdometer));
        }
        if (remarks != null) {
            trip.setRemarks(remarks);
        }
        trip.setModifiedDate(LocalDateTime.now());
        
        Trip updatedTrip = tripRepository.save(trip);
        return convertToDTO(updatedTrip);
    }

    @Override
    public TripDTO completeTrip(Long id, LocalDateTime actualEndTime, String endingOdometer,
                               String actualDistance, String fuelConsumed, String remarks) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        if (!"RUNNING".equals(trip.getStatus())) {
            throw new BusinessValidationException("Only running trips can be completed");
        }
        
        trip.setStatus("COMPLETED");
        trip.setActualEndDate(actualEndTime != null ? actualEndTime : LocalDateTime.now());
        
        if (endingOdometer != null) {
            trip.setEndingOdometer(new BigDecimal(endingOdometer));
        }
        if (actualDistance != null) {
            trip.setDistance(new BigDecimal(actualDistance));
        }
        if (fuelConsumed != null) {
            trip.setFuelConsumed(new BigDecimal(fuelConsumed));
        }
        if (remarks != null) {
            trip.setRemarks(remarks);
        }
        trip.setModifiedDate(LocalDateTime.now());
        
        Trip updatedTrip = tripRepository.save(trip);
        return convertToDTO(updatedTrip);
    }

    @Override
    public TripDTO cancelTrip(Long id, String cancellationReason, String remarks) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        if ("COMPLETED".equals(trip.getStatus())) {
            throw new BusinessValidationException("Cannot cancel a completed trip");
        }
        
        trip.setStatus("CANCELLED");
        trip.setCancellationReason(cancellationReason);
        if (remarks != null) {
            trip.setRemarks(remarks);
        }
        trip.setModifiedDate(LocalDateTime.now());
        
        Trip updatedTrip = tripRepository.save(trip);
        return convertToDTO(updatedTrip);
    }

    @Override
    public TripDTO updateTripStatus(Long id, String status, String remarks) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        validateStatusTransition(trip.getStatus(), status);
        
        trip.setStatus(status);
        if (remarks != null) {
            trip.setRemarks(remarks);
        }
        trip.setModifiedDate(LocalDateTime.now());
        
        Trip updatedTrip = tripRepository.save(trip);
        return convertToDTO(updatedTrip);
    }

    @Override
    public TripDTO updateTripCharges(Long id, String tripCharges, String advanceAmount, String remarks) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        if (tripCharges != null) {
            trip.setTripCharges(new BigDecimal(tripCharges));
        }
        if (advanceAmount != null) {
            trip.setAdvanceAmount(new BigDecimal(advanceAmount));
        }
        if (remarks != null) {
            trip.setRemarks(remarks);
        }
        trip.setModifiedDate(LocalDateTime.now());
        
        Trip updatedTrip = tripRepository.save(trip);
        return convertToDTO(updatedTrip);
    }

    @Override
    public TripDTO updateTripExpenses(Long id, String fuelCost, String tollCharges, String otherExpenses, String remarks) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", id));
        
        if (fuelCost != null) {
            trip.setFuelCost(new BigDecimal(fuelCost));
        }
        if (tollCharges != null) {
            trip.setTollCharges(new BigDecimal(tollCharges));
        }
        if (otherExpenses != null) {
            trip.setOtherExpenses(new BigDecimal(otherExpenses));
        }
        if (remarks != null) {
            trip.setRemarks(remarks);
        }
        trip.setModifiedDate(LocalDateTime.now());
        
        Trip updatedTrip = tripRepository.save(trip);
        return convertToDTO(updatedTrip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getRunningTrips() {
        return tripRepository.findRunningTrips().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getOverdueTrips() {
        return tripRepository.findOverdueTrips(LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getUpcomingTrips(int days) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(days);
        return tripRepository.findUpcomingTrips(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getDelayedTrips() {
        return tripRepository.findDelayedTrips().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object[] getTripStatistics() {
        return tripRepository.getTripStatistics();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTripPerformanceReport(LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.getDailyTripReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getRouteAnalysis(LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.getRoutePerformanceReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDailyTripReport(LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.getDailyTripReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyTripReport(LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.getMonthlyTripReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getProfitabilityAnalysis(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalRevenue = tripRepository.calculateTotalRevenue(startDate, endDate);
        BigDecimal totalExpenses = tripRepository.calculateTotalExpenses(startDate, endDate);
        
        // Return profitability data
        return List.of(new Object[]{
            "Total Revenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
            "Total Expenses", totalExpenses != null ? totalExpenses : BigDecimal.ZERO,
            "Profit", totalRevenue != null && totalExpenses != null ? 
                     totalRevenue.subtract(totalExpenses) : BigDecimal.ZERO
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> generateTripReport(String status, Long truckId, Long driverId, 
                                           LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.searchTrips(null, truckId, driverId, null, null, null, 
                                         status, startDate, endDate, Pageable.unpaged())
                .getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
    public String generateTripNumber() {
        String datePrefix = "TR" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        List<String> existingNumbers = tripRepository.findLastTripNumberForDate(datePrefix + "%");
        
        int nextNumber = 1;
        if (!existingNumbers.isEmpty()) {
            String lastNumber = existingNumbers.get(0);
            String numberPart = lastNumber.substring(datePrefix.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        
        return datePrefix + String.format("%04d", nextNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTripCount(String status, Long truckId, Long driverId) {
        if (status != null && truckId != null && driverId != null) {
            return tripRepository.searchTrips(null, truckId, driverId, null, null, null, 
                                             status, null, null, Pageable.unpaged()).getTotalElements();
        } else if (status != null) {
            return tripRepository.findByStatus(status, Pageable.unpaged()).getTotalElements();
        } else if (truckId != null) {
            return tripRepository.findByTruckId(truckId, Pageable.unpaged()).getTotalElements();
        } else if (driverId != null) {
            return tripRepository.findByDriverId(driverId, Pageable.unpaged()).getTotalElements();
        }
        return tripRepository.count();
    }

    // Private helper methods
    private void validateTripData(TripDTO tripDTO) {
        if (tripDTO.getSourceLocation() == null || tripDTO.getSourceLocation().trim().isEmpty()) {
            throw new BusinessValidationException("Source location is required");
        }
        if (tripDTO.getDestinationLocation() == null || tripDTO.getDestinationLocation().trim().isEmpty()) {
            throw new BusinessValidationException("Destination location is required");
        }
        if (tripDTO.getTruckId() == null) {
            throw new BusinessValidationException("Truck is required");
        }
        if (tripDTO.getDriverId() == null) {
            throw new BusinessValidationException("Driver is required");
        }
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        if ("COMPLETED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new BusinessValidationException("Cannot change status of completed or cancelled trip");
        }
    }

    private Trip convertToEntity(TripDTO dto) {
        Trip trip = new Trip();
        trip.setTripNumber(dto.getTripNumber());
        trip.setSourceLocation(dto.getSourceLocation());
        trip.setDestinationLocation(dto.getDestinationLocation());
        trip.setPlannedStartDate(dto.getPlannedStartDate());
        trip.setPlannedEndDate(dto.getPlannedEndDate());
        trip.setDistance(dto.getDistance() != null ? new BigDecimal(dto.getDistance()) : null);
        trip.setTripCharges(dto.getTripCharges() != null ? new BigDecimal(dto.getTripCharges()) : null);
        trip.setRemarks(dto.getRemarks());
        // Set relationships based on IDs
        // Note: In a complete implementation, you would fetch and set the actual entities
        return trip;
    }

    private TripDTO convertToDTO(Trip trip) {
        TripDTO dto = new TripDTO();
        dto.setId(trip.getId());
        dto.setTripNumber(trip.getTripNumber());
        dto.setSourceLocation(trip.getSourceLocation());
        dto.setDestinationLocation(trip.getDestinationLocation());
        dto.setPlannedStartDate(trip.getPlannedStartDate());
        dto.setPlannedEndDate(trip.getPlannedEndDate());
        dto.setActualStartDate(trip.getActualStartDate());
        dto.setActualEndDate(trip.getActualEndDate());
        dto.setDistance(trip.getDistance() != null ? trip.getDistance().toString() : null);
        dto.setTripCharges(trip.getTripCharges() != null ? trip.getTripCharges().toString() : null);
        dto.setAdvanceAmount(trip.getAdvanceAmount() != null ? trip.getAdvanceAmount().toString() : null);
        dto.setFuelCost(trip.getFuelCost() != null ? trip.getFuelCost().toString() : null);
        dto.setTollCharges(trip.getTollCharges() != null ? trip.getTollCharges().toString() : null);
        dto.setOtherExpenses(trip.getOtherExpenses() != null ? trip.getOtherExpenses().toString() : null);
        dto.setStatus(trip.getStatus());
        dto.setRemarks(trip.getRemarks());
        dto.setCreatedDate(trip.getCreatedDate());
        dto.setModifiedDate(trip.getModifiedDate());
        // Set relationship IDs
        if (trip.getTruck() != null) {
            dto.setTruckId(trip.getTruck().getId());
        }
        if (trip.getDriver() != null) {
            dto.setDriverId(trip.getDriver().getId());
        }
        if (trip.getClient() != null) {
            dto.setClientId(trip.getClient().getId());
        }
        return dto;
    }

    private void updateTripFields(Trip trip, TripDTO dto) {
        if (dto.getSourceLocation() != null) {
            trip.setSourceLocation(dto.getSourceLocation());
        }
        if (dto.getDestinationLocation() != null) {
            trip.setDestinationLocation(dto.getDestinationLocation());
        }
        if (dto.getPlannedStartDate() != null) {
            trip.setPlannedStartDate(dto.getPlannedStartDate());
        }
        if (dto.getPlannedEndDate() != null) {
            trip.setPlannedEndDate(dto.getPlannedEndDate());
        }
        if (dto.getDistance() != null) {
            trip.setDistance(new BigDecimal(dto.getDistance()));
        }
        if (dto.getTripCharges() != null) {
            trip.setTripCharges(new BigDecimal(dto.getTripCharges()));
        }
        if (dto.getRemarks() != null) {
            trip.setRemarks(dto.getRemarks());
        }
    }
}

