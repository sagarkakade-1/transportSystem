package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.DriverDTO;
import com.shivshakti.stms.service.DriverService;
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
 * REST Controller for Driver management
 * Provides comprehensive API endpoints for driver operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO) {
        DriverDTO createdDriver = driverService.createDriver(driverDTO);
        return new ResponseEntity<>(createdDriver, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverDTO driverDTO) {
        DriverDTO updatedDriver = driverService.updateDriver(id, driverDTO);
        return ResponseEntity.ok(updatedDriver);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id) {
        Optional<DriverDTO> driver = driverService.getDriverById(id);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<DriverDTO>> getAllDrivers(Pageable pageable) {
        Page<DriverDTO> drivers = driverService.getAllDrivers(pageable);
        return ResponseEntity.ok(drivers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<DriverDTO>> searchDrivers(
            @RequestParam(required = false) String driverNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String licenseType,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        Page<DriverDTO> drivers = driverService.searchDrivers(driverNumber, name, phone, licenseType, isActive, pageable);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/by-driver-number/{driverNumber}")
    public ResponseEntity<DriverDTO> getDriverByDriverNumber(@PathVariable String driverNumber) {
        Optional<DriverDTO> driver = driverService.findByDriverNumber(driverNumber);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-license-number/{licenseNumber}")
    public ResponseEntity<DriverDTO> getDriverByLicenseNumber(@PathVariable String licenseNumber) {
        Optional<DriverDTO> driver = driverService.findByLicenseNumber(licenseNumber);
        return driver.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<Page<DriverDTO>> getActiveDrivers(Pageable pageable) {
        Page<DriverDTO> drivers = driverService.getActiveDrivers(pageable);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/by-license-type/{licenseType}")
    public ResponseEntity<Page<DriverDTO>> getDriversByLicenseType(@PathVariable String licenseType, Pageable pageable) {
        Page<DriverDTO> drivers = driverService.getDriversByLicenseType(licenseType, pageable);
        return ResponseEntity.ok(drivers);
    }

    // ===============================================
    // LICENSE MANAGEMENT
    // ===============================================

    @GetMapping("/license-expiring")
    public ResponseEntity<List<DriverDTO>> getDriversWithLicenseExpiring(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DriverDTO> drivers = driverService.getDriversWithLicenseExpiringBetween(startDate, endDate);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/license-expired")
    public ResponseEntity<List<DriverDTO>> getDriversWithExpiredLicense() {
        List<DriverDTO> drivers = driverService.getDriversWithExpiredLicense();
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/{id}/renew-license")
    public ResponseEntity<DriverDTO> renewDriverLicense(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newExpiryDate,
            @RequestParam(required = false) String remarks) {
        DriverDTO updatedDriver = driverService.renewDriverLicense(id, newExpiryDate, remarks);
        return ResponseEntity.ok(updatedDriver);
    }

    // ===============================================
    // AVAILABILITY MANAGEMENT
    // ===============================================

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkDriverAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long excludeTripId) {
        boolean isAvailable = driverService.isDriverAvailable(id, startDate, endDate, excludeTripId);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DriverDTO>> getAvailableDrivers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DriverDTO> drivers = driverService.getAvailableDrivers(startDate, endDate);
        return ResponseEntity.ok(drivers);
    }

    // ===============================================
    // FINANCIAL MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/advance")
    public ResponseEntity<DriverDTO> updateAdvanceAmount(
            @PathVariable Long id,
            @RequestParam String advanceAmount,
            @RequestParam(required = false) String remarks) {
        DriverDTO updatedDriver = driverService.updateAdvanceAmount(id, advanceAmount, remarks);
        return ResponseEntity.ok(updatedDriver);
    }

    @PutMapping("/{id}/salary")
    public ResponseEntity<DriverDTO> updateSalary(
            @PathVariable Long id,
            @RequestParam String salary,
            @RequestParam(required = false) String remarks) {
        DriverDTO updatedDriver = driverService.updateSalary(id, salary, remarks);
        return ResponseEntity.ok(updatedDriver);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getDriverStatistics() {
        Object[] statistics = driverService.getDriverStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/performance-report")
    public ResponseEntity<List<Object[]>> getDriverPerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = driverService.getDriverPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/license-expiry-report")
    public ResponseEntity<List<DriverDTO>> getLicenseExpiryReport(
            @RequestParam(defaultValue = "30") int days) {
        List<DriverDTO> drivers = driverService.getLicenseExpiryReport(days);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<DriverDTO>> generateDriverReport(
            @RequestParam(required = false) String licenseType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireEndDate) {
        List<DriverDTO> drivers = driverService.generateDriverReport(licenseType, isActive, hireStartDate, hireEndDate);
        return ResponseEntity.ok(drivers);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-driver-number/{driverNumber}")
    public ResponseEntity<Boolean> validateDriverNumber(
            @PathVariable String driverNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = driverService.isDriverNumberUnique(driverNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/validate-license-number/{licenseNumber}")
    public ResponseEntity<Boolean> validateLicenseNumber(
            @PathVariable String licenseNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = driverService.isLicenseNumberUnique(licenseNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-driver-number")
    public ResponseEntity<String> generateDriverNumber() {
        String driverNumber = driverService.generateDriverNumber();
        return ResponseEntity.ok(driverNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getDriverCount(
            @RequestParam(required = false) String licenseType,
            @RequestParam(required = false) Boolean isActive) {
        long count = driverService.getDriverCount(licenseType, isActive);
        return ResponseEntity.ok(count);
    }
}

