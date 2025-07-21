package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.TruckDTO;
import com.shivshakti.stms.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Truck management
 * Provides comprehensive API endpoints for truck operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/trucks")
@CrossOrigin(origins = "*")
public class TruckController {

    private final TruckService truckService;

    @Autowired
    public TruckController(TruckService truckService) {
        this.truckService = truckService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<TruckDTO> createTruck(@Valid @RequestBody TruckDTO truckDTO) {
        TruckDTO createdTruck = truckService.createTruck(truckDTO);
        return new ResponseEntity<>(createdTruck, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TruckDTO> updateTruck(@PathVariable Long id, @Valid @RequestBody TruckDTO truckDTO) {
        TruckDTO updatedTruck = truckService.updateTruck(id, truckDTO);
        return ResponseEntity.ok(updatedTruck);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TruckDTO> getTruckById(@PathVariable Long id) {
        Optional<TruckDTO> truck = truckService.getTruckById(id);
        return truck.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<TruckDTO>> getAllTrucks(Pageable pageable) {
        Page<TruckDTO> trucks = truckService.getAllTrucks(pageable);
        return ResponseEntity.ok(trucks);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTruck(@PathVariable Long id) {
        truckService.deleteTruck(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<TruckDTO>> searchTrucks(
            @RequestParam(required = false) String truckNumber,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        Page<TruckDTO> trucks = truckService.searchTrucks(truckNumber, model, manufacturer, fuelType, status, isActive, pageable);
        return ResponseEntity.ok(trucks);
    }

    @GetMapping("/by-truck-number/{truckNumber}")
    public ResponseEntity<TruckDTO> getTruckByTruckNumber(@PathVariable String truckNumber) {
        Optional<TruckDTO> truck = truckService.findByTruckNumber(truckNumber);
        return truck.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<Page<TruckDTO>> getActiveTrucks(Pageable pageable) {
        Page<TruckDTO> trucks = truckService.getActiveTrucks(pageable);
        return ResponseEntity.ok(trucks);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<TruckDTO>> getTrucksByStatus(@PathVariable String status, Pageable pageable) {
        Page<TruckDTO> trucks = truckService.getTrucksByStatus(status, pageable);
        return ResponseEntity.ok(trucks);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TruckDTO>> getAvailableTrucks() {
        List<TruckDTO> trucks = truckService.getAvailableTrucks();
        return ResponseEntity.ok(trucks);
    }

    // ===============================================
    // DOCUMENT MANAGEMENT
    // ===============================================

    @GetMapping("/insurance-expiring")
    public ResponseEntity<List<TruckDTO>> getTrucksWithInsuranceExpiring(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TruckDTO> trucks = truckService.getTrucksWithInsuranceExpiringBetween(startDate, endDate);
        return ResponseEntity.ok(trucks);
    }

    @GetMapping("/permit-expiring")
    public ResponseEntity<List<TruckDTO>> getTrucksWithPermitExpiring(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TruckDTO> trucks = truckService.getTrucksWithPermitExpiringBetween(startDate, endDate);
        return ResponseEntity.ok(trucks);
    }

    @GetMapping("/document-alerts")
    public ResponseEntity<List<TruckDTO>> getDocumentExpiryAlerts(
            @RequestParam(defaultValue = "30") int alertDays) {
        List<TruckDTO> trucks = truckService.getDocumentExpiryAlerts(alertDays);
        return ResponseEntity.ok(trucks);
    }

    @PutMapping("/{id}/renew-insurance")
    public ResponseEntity<TruckDTO> renewInsurance(
            @PathVariable Long id,
            @RequestParam String insuranceNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam(required = false) String remarks) {
        TruckDTO updatedTruck = truckService.renewInsurance(id, insuranceNumber, expiryDate, remarks);
        return ResponseEntity.ok(updatedTruck);
    }

    // ===============================================
    // STATUS MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/status")
    public ResponseEntity<TruckDTO> updateTruckStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks) {
        TruckDTO updatedTruck = truckService.updateTruckStatus(id, status, remarks);
        return ResponseEntity.ok(updatedTruck);
    }

    @PutMapping("/{id}/odometer")
    public ResponseEntity<TruckDTO> updateOdometer(
            @PathVariable Long id,
            @RequestParam String currentOdometer,
            @RequestParam(required = false) String remarks) {
        TruckDTO updatedTruck = truckService.updateOdometer(id, currentOdometer, remarks);
        return ResponseEntity.ok(updatedTruck);
    }

    // ===============================================
    // AVAILABILITY MANAGEMENT
    // ===============================================

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkTruckAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long excludeTripId) {
        boolean isAvailable = truckService.isTruckAvailable(id, startDate, endDate, excludeTripId);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/available-for-period")
    public ResponseEntity<List<TruckDTO>> getAvailableTrucksForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TruckDTO> trucks = truckService.getAvailableTrucksForPeriod(startDate, endDate);
        return ResponseEntity.ok(trucks);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getTruckStatistics() {
        Object[] statistics = truckService.getTruckStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/performance-report")
    public ResponseEntity<List<Object[]>> getTruckPerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = truckService.getTruckPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/utilization-report")
    public ResponseEntity<List<Object[]>> getTruckUtilizationReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = truckService.getTruckUtilizationReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/maintenance-due")
    public ResponseEntity<List<TruckDTO>> getTrucksWithMaintenanceDue() {
        List<TruckDTO> trucks = truckService.getTrucksWithMaintenanceDue();
        return ResponseEntity.ok(trucks);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<TruckDTO>> generateTruckReport(
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive) {
        List<TruckDTO> trucks = truckService.generateTruckReport(fuelType, status, isActive);
        return ResponseEntity.ok(trucks);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-truck-number/{truckNumber}")
    public ResponseEntity<Boolean> validateTruckNumber(
            @PathVariable String truckNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = truckService.isTruckNumberUnique(truckNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-truck-number")
    public ResponseEntity<String> generateTruckNumber() {
        String truckNumber = truckService.generateTruckNumber();
        return ResponseEntity.ok(truckNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTruckCount(
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String status) {
        long count = truckService.getTruckCount(fuelType, status);
        return ResponseEntity.ok(count);
    }
}

