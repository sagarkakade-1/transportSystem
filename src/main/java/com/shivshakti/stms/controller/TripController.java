package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.TripDTO;
import com.shivshakti.stms.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Trip management
 * Provides comprehensive API endpoints for trip operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<TripDTO> createTrip(@Valid @RequestBody TripDTO tripDTO) {
        TripDTO createdTrip = tripService.createTrip(tripDTO);
        return new ResponseEntity<>(createdTrip, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripDTO> updateTrip(@PathVariable Long id, @Valid @RequestBody TripDTO tripDTO) {
        TripDTO updatedTrip = tripService.updateTrip(id, tripDTO);
        return ResponseEntity.ok(updatedTrip);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDTO> getTripById(@PathVariable Long id) {
        Optional<TripDTO> trip = tripService.getTripById(id);
        return trip.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<TripDTO>> getAllTrips(Pageable pageable) {
        Page<TripDTO> trips = tripService.getAllTrips(pageable);
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<TripDTO>> searchTrips(
            @RequestParam(required = false) String tripNumber,
            @RequestParam(required = false) Long truckId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String sourceLocation,
            @RequestParam(required = false) String destinationLocation,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        Page<TripDTO> trips = tripService.searchTrips(tripNumber, truckId, driverId, clientId, 
                                                     sourceLocation, destinationLocation, status, 
                                                     startDate, endDate, pageable);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/by-trip-number/{tripNumber}")
    public ResponseEntity<TripDTO> getTripByTripNumber(@PathVariable String tripNumber) {
        Optional<TripDTO> trip = tripService.findByTripNumber(tripNumber);
        return trip.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<TripDTO>> getTripsByStatus(@PathVariable String status, Pageable pageable) {
        Page<TripDTO> trips = tripService.getTripsByStatus(status, pageable);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/by-truck/{truckId}")
    public ResponseEntity<Page<TripDTO>> getTripsByTruck(@PathVariable Long truckId, Pageable pageable) {
        Page<TripDTO> trips = tripService.getTripsByTruck(truckId, pageable);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/by-driver/{driverId}")
    public ResponseEntity<Page<TripDTO>> getTripsByDriver(@PathVariable Long driverId, Pageable pageable) {
        Page<TripDTO> trips = tripService.getTripsByDriver(driverId, pageable);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Page<TripDTO>> getTripsByClient(@PathVariable Long clientId, Pageable pageable) {
        Page<TripDTO> trips = tripService.getTripsByClient(clientId, pageable);
        return ResponseEntity.ok(trips);
    }

    // ===============================================
    // TRIP LIFECYCLE MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/start")
    public ResponseEntity<TripDTO> startTrip(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualStartTime,
            @RequestParam(required = false) String startingOdometer,
            @RequestParam(required = false) String remarks) {
        TripDTO updatedTrip = tripService.startTrip(id, actualStartTime, startingOdometer, remarks);
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TripDTO> completeTrip(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime actualEndTime,
            @RequestParam(required = false) String endingOdometer,
            @RequestParam(required = false) String actualDistance,
            @RequestParam(required = false) String fuelConsumed,
            @RequestParam(required = false) String remarks) {
        TripDTO updatedTrip = tripService.completeTrip(id, actualEndTime, endingOdometer, 
                                                      actualDistance, fuelConsumed, remarks);
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TripDTO> cancelTrip(
            @PathVariable Long id,
            @RequestParam String cancellationReason,
            @RequestParam(required = false) String remarks) {
        TripDTO updatedTrip = tripService.cancelTrip(id, cancellationReason, remarks);
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TripDTO> updateTripStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks) {
        TripDTO updatedTrip = tripService.updateTripStatus(id, status, remarks);
        return ResponseEntity.ok(updatedTrip);
    }

    // ===============================================
    // FINANCIAL MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/charges")
    public ResponseEntity<TripDTO> updateTripCharges(
            @PathVariable Long id,
            @RequestParam String tripCharges,
            @RequestParam(required = false) String advanceAmount,
            @RequestParam(required = false) String remarks) {
        TripDTO updatedTrip = tripService.updateTripCharges(id, tripCharges, advanceAmount, remarks);
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/{id}/expenses")
    public ResponseEntity<TripDTO> updateTripExpenses(
            @PathVariable Long id,
            @RequestParam(required = false) String fuelCost,
            @RequestParam(required = false) String tollCharges,
            @RequestParam(required = false) String otherExpenses,
            @RequestParam(required = false) String remarks) {
        TripDTO updatedTrip = tripService.updateTripExpenses(id, fuelCost, tollCharges, otherExpenses, remarks);
        return ResponseEntity.ok(updatedTrip);
    }

    // ===============================================
    // OPERATIONAL QUERIES
    // ===============================================

    @GetMapping("/running")
    public ResponseEntity<List<TripDTO>> getRunningTrips() {
        List<TripDTO> trips = tripService.getRunningTrips();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TripDTO>> getOverdueTrips() {
        List<TripDTO> trips = tripService.getOverdueTrips();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TripDTO>> getUpcomingTrips(
            @RequestParam(defaultValue = "7") int days) {
        List<TripDTO> trips = tripService.getUpcomingTrips(days);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/delayed")
    public ResponseEntity<List<TripDTO>> getDelayedTrips() {
        List<TripDTO> trips = tripService.getDelayedTrips();
        return ResponseEntity.ok(trips);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getTripStatistics() {
        Object[] statistics = tripService.getTripStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/performance-report")
    public ResponseEntity<List<Object[]>> getTripPerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> report = tripService.getTripPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/route-analysis")
    public ResponseEntity<List<Object[]>> getRouteAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> analysis = tripService.getRouteAnalysis(startDate, endDate);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/daily-report")
    public ResponseEntity<List<Object[]>> getDailyTripReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> report = tripService.getDailyTripReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly-report")
    public ResponseEntity<List<Object[]>> getMonthlyTripReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> report = tripService.getMonthlyTripReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/profitability-analysis")
    public ResponseEntity<List<Object[]>> getProfitabilityAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> analysis = tripService.getProfitabilityAnalysis(startDate, endDate);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<TripDTO>> generateTripReport(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long truckId,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<TripDTO> trips = tripService.generateTripReport(status, truckId, driverId, startDate, endDate);
        return ResponseEntity.ok(trips);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-trip-number/{tripNumber}")
    public ResponseEntity<Boolean> validateTripNumber(
            @PathVariable String tripNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = tripService.isTripNumberUnique(tripNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-trip-number")
    public ResponseEntity<String> generateTripNumber() {
        String tripNumber = tripService.generateTripNumber();
        return ResponseEntity.ok(tripNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTripCount(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long truckId,
            @RequestParam(required = false) Long driverId) {
        long count = tripService.getTripCount(status, truckId, driverId);
        return ResponseEntity.ok(count);
    }
}

