package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.MaintenanceDTO;
import com.shivshakti.stms.service.MaintenanceService;
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
 * REST Controller for Maintenance management
 * Provides comprehensive API endpoints for maintenance operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/maintenances")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @Autowired
    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<MaintenanceDTO> createMaintenance(@Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        MaintenanceDTO createdMaintenance = maintenanceService.createMaintenance(maintenanceDTO);
        return new ResponseEntity<>(createdMaintenance, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> updateMaintenance(@PathVariable Long id, @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        MaintenanceDTO updatedMaintenance = maintenanceService.updateMaintenance(id, maintenanceDTO);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> getMaintenanceById(@PathVariable Long id) {
        Optional<MaintenanceDTO> maintenance = maintenanceService.getMaintenanceById(id);
        return maintenance.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<MaintenanceDTO>> getAllMaintenances(Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getAllMaintenances(pageable);
        return ResponseEntity.ok(maintenances);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<MaintenanceDTO>> searchMaintenances(
            @RequestParam(required = false) String maintenanceNumber,
            @RequestParam(required = false) Long truckId,
            @RequestParam(required = false) String maintenanceType,
            @RequestParam(required = false) String serviceCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.searchMaintenances(maintenanceNumber, truckId, 
                                                                                  maintenanceType, serviceCategory, 
                                                                                  status, priority, startDate, endDate, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/by-maintenance-number/{maintenanceNumber}")
    public ResponseEntity<MaintenanceDTO> getMaintenanceByMaintenanceNumber(@PathVariable String maintenanceNumber) {
        Optional<MaintenanceDTO> maintenance = maintenanceService.findByMaintenanceNumber(maintenanceNumber);
        return maintenance.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-truck/{truckId}")
    public ResponseEntity<Page<MaintenanceDTO>> getMaintenancesByTruck(@PathVariable Long truckId, Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getMaintenancesByTruck(truckId, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/by-type/{maintenanceType}")
    public ResponseEntity<Page<MaintenanceDTO>> getMaintenancesByType(@PathVariable String maintenanceType, Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getMaintenancesByType(maintenanceType, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/by-category/{serviceCategory}")
    public ResponseEntity<Page<MaintenanceDTO>> getMaintenancesByCategory(@PathVariable String serviceCategory, Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getMaintenancesByCategory(serviceCategory, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<MaintenanceDTO>> getMaintenancesByStatus(@PathVariable String status, Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getMaintenancesByStatus(status, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/by-priority/{priority}")
    public ResponseEntity<Page<MaintenanceDTO>> getMaintenancesByPriority(@PathVariable String priority, Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getMaintenancesByPriority(priority, pageable);
        return ResponseEntity.ok(maintenances);
    }

    // ===============================================
    // SCHEDULING MANAGEMENT
    // ===============================================

    @GetMapping("/scheduled")
    public ResponseEntity<Page<MaintenanceDTO>> getScheduledMaintenances(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getScheduledMaintenances(fromDate, toDate, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<MaintenanceDTO>> getOverdueMaintenances(Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getOverdueMaintenances(pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<MaintenanceDTO>> getUpcomingMaintenances(
            @RequestParam(defaultValue = "7") int days,
            Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getUpcomingMaintenances(days, pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/due-for-alert")
    public ResponseEntity<List<MaintenanceDTO>> getMaintenancesDueForAlert(
            @RequestParam(defaultValue = "7") int alertDays) {
        List<MaintenanceDTO> maintenances = maintenanceService.getMaintenancesDueForAlert(alertDays);
        return ResponseEntity.ok(maintenances);
    }

    @PutMapping("/{id}/schedule")
    public ResponseEntity<MaintenanceDTO> scheduleMaintenance(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduledDate,
            @RequestParam(required = false) String serviceProvider,
            @RequestParam(required = false) String remarks) {
        MaintenanceDTO updatedMaintenance = maintenanceService.scheduleMaintenance(id, scheduledDate, serviceProvider, remarks);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<MaintenanceDTO> rescheduleMaintenance(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newScheduledDate,
            @RequestParam String rescheduleReason) {
        MaintenanceDTO updatedMaintenance = maintenanceService.rescheduleMaintenance(id, newScheduledDate, rescheduleReason);
        return ResponseEntity.ok(updatedMaintenance);
    }

    // ===============================================
    // STATUS MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/start")
    public ResponseEntity<MaintenanceDTO> startMaintenance(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate actualStartDate,
            @RequestParam(required = false) String remarks) {
        MaintenanceDTO updatedMaintenance = maintenanceService.startMaintenance(id, actualStartDate, remarks);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<MaintenanceDTO> completeMaintenance(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedDate,
            @RequestParam(required = false) String laborCost,
            @RequestParam(required = false) String partsCost,
            @RequestParam(required = false) String otherCharges,
            @RequestParam(required = false) String workDescription,
            @RequestParam(required = false) String remarks) {
        MaintenanceDTO updatedMaintenance = maintenanceService.completeMaintenance(id, completedDate, laborCost, 
                                                                                   partsCost, otherCharges, workDescription, remarks);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<MaintenanceDTO> cancelMaintenance(
            @PathVariable Long id,
            @RequestParam String cancellationReason) {
        MaintenanceDTO updatedMaintenance = maintenanceService.cancelMaintenance(id, cancellationReason);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<MaintenanceDTO> updateMaintenanceStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks) {
        MaintenanceDTO updatedMaintenance = maintenanceService.updateMaintenanceStatus(id, status, remarks);
        return ResponseEntity.ok(updatedMaintenance);
    }

    // ===============================================
    // RECURRING MAINTENANCE
    // ===============================================

    @GetMapping("/recurring")
    public ResponseEntity<Page<MaintenanceDTO>> getRecurringMaintenances(Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getRecurringMaintenances(pageable);
        return ResponseEntity.ok(maintenances);
    }

    @GetMapping("/recurring-due")
    public ResponseEntity<List<MaintenanceDTO>> getRecurringMaintenancesDue() {
        List<MaintenanceDTO> maintenances = maintenanceService.getRecurringMaintenancesDue();
        return ResponseEntity.ok(maintenances);
    }

    @PutMapping("/{id}/recurring")
    public ResponseEntity<MaintenanceDTO> updateRecurringSettings(
            @PathVariable Long id,
            @RequestParam Boolean isRecurring,
            @RequestParam(required = false) String recurringFrequency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextServiceDate) {
        MaintenanceDTO updatedMaintenance = maintenanceService.updateRecurringSettings(id, isRecurring, recurringFrequency, nextServiceDate);
        return ResponseEntity.ok(updatedMaintenance);
    }

    // ===============================================
    // COST MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/costs")
    public ResponseEntity<MaintenanceDTO> updateMaintenanceCosts(
            @PathVariable Long id,
            @RequestParam(required = false) String laborCost,
            @RequestParam(required = false) String partsCost,
            @RequestParam(required = false) String otherCharges,
            @RequestParam(required = false) String gstAmount,
            @RequestParam(required = false) String remarks) {
        MaintenanceDTO updatedMaintenance = maintenanceService.updateMaintenanceCosts(id, laborCost, partsCost, 
                                                                                      otherCharges, gstAmount, remarks);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @GetMapping("/total-cost")
    public ResponseEntity<String> getTotalMaintenanceCost(
            @RequestParam Long truckId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String totalCost = maintenanceService.getTotalMaintenanceCost(truckId, startDate, endDate);
        return ResponseEntity.ok(totalCost);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getMaintenanceStatistics() {
        Object[] statistics = maintenanceService.getMaintenanceStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/by-type-report")
    public ResponseEntity<List<Object[]>> getMaintenanceByTypeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = maintenanceService.getMaintenanceByTypeReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/by-category-report")
    public ResponseEntity<List<Object[]>> getMaintenanceByCategoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = maintenanceService.getMaintenanceByCategoryReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/truck-wise-cost")
    public ResponseEntity<List<Object[]>> getTruckWiseMaintenanceCost(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = maintenanceService.getTruckWiseMaintenanceCost(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<List<Object[]>> getMonthlyMaintenanceSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = maintenanceService.getMonthlyMaintenanceSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/cost-summary")
    public ResponseEntity<List<Object[]>> getMaintenanceCostSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = maintenanceService.getMaintenanceCostSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/truck-history/{truckId}")
    public ResponseEntity<List<Object[]>> getTruckMaintenanceHistory(@PathVariable Long truckId) {
        List<Object[]> history = maintenanceService.getTruckMaintenanceHistory(truckId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/service-provider-performance")
    public ResponseEntity<List<Object[]>> getServiceProviderPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> performance = maintenanceService.getServiceProviderPerformance(startDate, endDate);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/efficiency-report")
    public ResponseEntity<List<Object[]>> getMaintenanceEfficiencyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = maintenanceService.getMaintenanceEfficiencyReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<MaintenanceDTO>> generateMaintenanceReport(
            @RequestParam(required = false) String maintenanceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MaintenanceDTO> maintenances = maintenanceService.generateMaintenanceReport(maintenanceType, status, startDate, endDate);
        return ResponseEntity.ok(maintenances);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-maintenance-number/{maintenanceNumber}")
    public ResponseEntity<Boolean> validateMaintenanceNumber(
            @PathVariable String maintenanceNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = maintenanceService.isMaintenanceNumberUnique(maintenanceNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-maintenance-number")
    public ResponseEntity<String> generateMaintenanceNumber() {
        String maintenanceNumber = maintenanceService.generateMaintenanceNumber();
        return ResponseEntity.ok(maintenanceNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getMaintenanceCount(
            @RequestParam(required = false) String maintenanceType,
            @RequestParam(required = false) String status) {
        long count = maintenanceService.getMaintenanceCount(maintenanceType, status);
        return ResponseEntity.ok(count);
    }
}

