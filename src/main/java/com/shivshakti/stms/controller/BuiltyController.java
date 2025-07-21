package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.BuiltyDTO;
import com.shivshakti.stms.service.BuiltyService;
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
 * REST Controller for Builty management
 * Provides comprehensive API endpoints for builty operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/builties")
@CrossOrigin(origins = "*")
public class BuiltyController {

    private final BuiltyService builtyService;

    @Autowired
    public BuiltyController(BuiltyService builtyService) {
        this.builtyService = builtyService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<BuiltyDTO> createBuilty(@Valid @RequestBody BuiltyDTO builtyDTO) {
        BuiltyDTO createdBuilty = builtyService.createBuilty(builtyDTO);
        return new ResponseEntity<>(createdBuilty, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuiltyDTO> updateBuilty(@PathVariable Long id, @Valid @RequestBody BuiltyDTO builtyDTO) {
        BuiltyDTO updatedBuilty = builtyService.updateBuilty(id, builtyDTO);
        return ResponseEntity.ok(updatedBuilty);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuiltyDTO> getBuiltyById(@PathVariable Long id) {
        Optional<BuiltyDTO> builty = builtyService.getBuiltyById(id);
        return builty.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<BuiltyDTO>> getAllBuilties(Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.getAllBuilties(pageable);
        return ResponseEntity.ok(builties);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilty(@PathVariable Long id) {
        builtyService.deleteBuilty(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<BuiltyDTO>> searchBuilties(
            @RequestParam(required = false) String builtyNumber,
            @RequestParam(required = false) Long tripId,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String consignorName,
            @RequestParam(required = false) String consigneeName,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String deliveryStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.searchBuilties(builtyNumber, tripId, clientId, 
                                                               consignorName, consigneeName, paymentStatus, 
                                                               deliveryStatus, startDate, endDate, pageable);
        return ResponseEntity.ok(builties);
    }

    @GetMapping("/by-builty-number/{builtyNumber}")
    public ResponseEntity<BuiltyDTO> getBuiltyByBuiltyNumber(@PathVariable String builtyNumber) {
        Optional<BuiltyDTO> builty = builtyService.findByBuiltyNumber(builtyNumber);
        return builty.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-trip/{tripId}")
    public ResponseEntity<Page<BuiltyDTO>> getBuiltiesByTrip(@PathVariable Long tripId, Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.getBuiltiesByTrip(tripId, pageable);
        return ResponseEntity.ok(builties);
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Page<BuiltyDTO>> getBuiltiesByClient(@PathVariable Long clientId, Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.getBuiltiesByClient(clientId, pageable);
        return ResponseEntity.ok(builties);
    }

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================

    @GetMapping("/by-payment-status/{paymentStatus}")
    public ResponseEntity<Page<BuiltyDTO>> getBuiltiesByPaymentStatus(@PathVariable String paymentStatus, Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.getBuiltiesByPaymentStatus(paymentStatus, pageable);
        return ResponseEntity.ok(builties);
    }

    @GetMapping("/overdue-payments")
    public ResponseEntity<Page<BuiltyDTO>> getOverduePayments(Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.getOverduePayments(pageable);
        return ResponseEntity.ok(builties);
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<BuiltyDTO> recordPayment(
            @PathVariable Long id,
            @RequestParam String paymentAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String remarks) {
        BuiltyDTO updatedBuilty = builtyService.recordPayment(id, paymentAmount, paymentDate, 
                                                             paymentMethod, referenceNumber, remarks);
        return ResponseEntity.ok(updatedBuilty);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<BuiltyDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus,
            @RequestParam(required = false) String remarks) {
        BuiltyDTO updatedBuilty = builtyService.updatePaymentStatus(id, paymentStatus, remarks);
        return ResponseEntity.ok(updatedBuilty);
    }

    // ===============================================
    // DELIVERY MANAGEMENT
    // ===============================================

    @GetMapping("/by-delivery-status/{deliveryStatus}")
    public ResponseEntity<Page<BuiltyDTO>> getBuiltiesByDeliveryStatus(@PathVariable String deliveryStatus, Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.getBuiltiesByDeliveryStatus(deliveryStatus, pageable);
        return ResponseEntity.ok(builties);
    }

    @GetMapping("/pending-deliveries")
    public ResponseEntity<Page<BuiltyDTO>> getPendingDeliveries(Pageable pageable) {
        Page<BuiltyDTO> builties = builtyService.getPendingDeliveries(pageable);
        return ResponseEntity.ok(builties);
    }

    @PutMapping("/{id}/delivery")
    public ResponseEntity<BuiltyDTO> recordDelivery(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate,
            @RequestParam(required = false) String deliveredBy,
            @RequestParam(required = false) String receivedBy,
            @RequestParam(required = false) String remarks) {
        BuiltyDTO updatedBuilty = builtyService.recordDelivery(id, deliveryDate, deliveredBy, receivedBy, remarks);
        return ResponseEntity.ok(updatedBuilty);
    }

    @PutMapping("/{id}/delivery-status")
    public ResponseEntity<BuiltyDTO> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam String deliveryStatus,
            @RequestParam(required = false) String remarks) {
        BuiltyDTO updatedBuilty = builtyService.updateDeliveryStatus(id, deliveryStatus, remarks);
        return ResponseEntity.ok(updatedBuilty);
    }

    // ===============================================
    // FINANCIAL OPERATIONS
    // ===============================================

    @PutMapping("/{id}/charges")
    public ResponseEntity<BuiltyDTO> updateCharges(
            @PathVariable Long id,
            @RequestParam(required = false) String freightCharges,
            @RequestParam(required = false) String loadingCharges,
            @RequestParam(required = false) String unloadingCharges,
            @RequestParam(required = false) String otherCharges,
            @RequestParam(required = false) String remarks) {
        BuiltyDTO updatedBuilty = builtyService.updateCharges(id, freightCharges, loadingCharges, 
                                                             unloadingCharges, otherCharges, remarks);
        return ResponseEntity.ok(updatedBuilty);
    }

    @PutMapping("/{id}/advance")
    public ResponseEntity<BuiltyDTO> updateAdvanceAmount(
            @PathVariable Long id,
            @RequestParam String advanceAmount,
            @RequestParam(required = false) String remarks) {
        BuiltyDTO updatedBuilty = builtyService.updateAdvanceAmount(id, advanceAmount, remarks);
        return ResponseEntity.ok(updatedBuilty);
    }

    @GetMapping("/total-pending-amount")
    public ResponseEntity<String> getTotalPendingAmount() {
        String totalAmount = builtyService.getTotalPendingAmount();
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/total-received-amount")
    public ResponseEntity<String> getTotalReceivedAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String totalAmount = builtyService.getTotalReceivedAmount(startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getBuiltyStatistics() {
        Object[] statistics = builtyService.getBuiltyStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/payment-summary")
    public ResponseEntity<List<Object[]>> getPaymentSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = builtyService.getPaymentSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/client-wise-report")
    public ResponseEntity<List<Object[]>> getClientWiseBuiltyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = builtyService.getClientWiseBuiltyReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/daily-report")
    public ResponseEntity<List<Object[]>> getDailyBuiltyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = builtyService.getDailyBuiltyReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly-report")
    public ResponseEntity<List<Object[]>> getMonthlyBuiltyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = builtyService.getMonthlyBuiltyReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<BuiltyDTO>> generateBuiltyReport(
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String deliveryStatus,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<BuiltyDTO> builties = builtyService.generateBuiltyReport(paymentStatus, deliveryStatus, 
                                                                     clientId, startDate, endDate);
        return ResponseEntity.ok(builties);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-builty-number/{builtyNumber}")
    public ResponseEntity<Boolean> validateBuiltyNumber(
            @PathVariable String builtyNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = builtyService.isBuiltyNumberUnique(builtyNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-builty-number")
    public ResponseEntity<String> generateBuiltyNumber() {
        String builtyNumber = builtyService.generateBuiltyNumber();
        return ResponseEntity.ok(builtyNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getBuiltyCount(
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String deliveryStatus,
            @RequestParam(required = false) Long clientId) {
        long count = builtyService.getBuiltyCount(paymentStatus, deliveryStatus, clientId);
        return ResponseEntity.ok(count);
    }
}

