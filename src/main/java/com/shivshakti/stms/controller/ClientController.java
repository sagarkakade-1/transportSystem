package com.shivshakti.stms.controller;

import com.shivshakti.stms.dto.ClientDTO;
import com.shivshakti.stms.service.ClientService;
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
 * REST Controller for Client management
 * Provides comprehensive API endpoints for client operations
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO createdClient = clientService.createClient(clientDTO);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO updatedClient = clientService.updateClient(id, clientDTO);
        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        Optional<ClientDTO> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ClientDTO>> getAllClients(Pageable pageable) {
        Page<ClientDTO> clients = clientService.getAllClients(pageable);
        return ResponseEntity.ok(clients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================

    @GetMapping("/search")
    public ResponseEntity<Page<ClientDTO>> searchClients(
            @RequestParam(required = false) String clientNumber,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String clientType,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable) {
        Page<ClientDTO> clients = clientService.searchClients(clientNumber, name, companyName, phone, clientType, isActive, pageable);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/by-client-number/{clientNumber}")
    public ResponseEntity<ClientDTO> getClientByClientNumber(@PathVariable String clientNumber) {
        Optional<ClientDTO> client = clientService.findByClientNumber(clientNumber);
        return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-gst-number/{gstNumber}")
    public ResponseEntity<ClientDTO> getClientByGstNumber(@PathVariable String gstNumber) {
        Optional<ClientDTO> client = clientService.findByGstNumber(gstNumber);
        return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<Page<ClientDTO>> getActiveClients(Pageable pageable) {
        Page<ClientDTO> clients = clientService.getActiveClients(pageable);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/by-type/{clientType}")
    public ResponseEntity<Page<ClientDTO>> getClientsByType(@PathVariable String clientType, Pageable pageable) {
        Page<ClientDTO> clients = clientService.getClientsByType(clientType, pageable);
        return ResponseEntity.ok(clients);
    }

    // ===============================================
    // CREDIT MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/credit-limit")
    public ResponseEntity<ClientDTO> updateCreditLimit(
            @PathVariable Long id,
            @RequestParam String creditLimit,
            @RequestParam(required = false) String remarks) {
        ClientDTO updatedClient = clientService.updateCreditLimit(id, creditLimit, remarks);
        return ResponseEntity.ok(updatedClient);
    }

    @PutMapping("/{id}/outstanding-amount")
    public ResponseEntity<ClientDTO> updateOutstandingAmount(
            @PathVariable Long id,
            @RequestParam String outstandingAmount,
            @RequestParam(required = false) String remarks) {
        ClientDTO updatedClient = clientService.updateOutstandingAmount(id, outstandingAmount, remarks);
        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/credit-exceeded")
    public ResponseEntity<List<ClientDTO>> getClientsExceedingCreditLimit() {
        List<ClientDTO> clients = clientService.getClientsExceedingCreditLimit();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/with-outstanding")
    public ResponseEntity<Page<ClientDTO>> getClientsWithOutstandingAmount(Pageable pageable) {
        Page<ClientDTO> clients = clientService.getClientsWithOutstandingAmount(pageable);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}/credit-analysis")
    public ResponseEntity<Object[]> getCreditAnalysis(@PathVariable Long id) {
        Object[] analysis = clientService.getCreditAnalysis(id);
        return ResponseEntity.ok(analysis);
    }

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================

    @PutMapping("/{id}/payment")
    public ResponseEntity<ClientDTO> recordPayment(
            @PathVariable Long id,
            @RequestParam String paymentAmount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String remarks) {
        ClientDTO updatedClient = clientService.recordPayment(id, paymentAmount, paymentDate, paymentMethod, referenceNumber, remarks);
        return ResponseEntity.ok(updatedClient);
    }

    @GetMapping("/{id}/payment-history")
    public ResponseEntity<List<Object[]>> getPaymentHistory(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> history = clientService.getPaymentHistory(id, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================

    @GetMapping("/statistics")
    public ResponseEntity<Object[]> getClientStatistics() {
        Object[] statistics = clientService.getClientStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/financial-summary")
    public ResponseEntity<List<Object[]>> getClientFinancialSummary() {
        List<Object[]> summary = clientService.getClientFinancialSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/business-report")
    public ResponseEntity<List<Object[]>> getClientBusinessReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = clientService.getClientBusinessReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/outstanding-report")
    public ResponseEntity<List<ClientDTO>> getOutstandingReport() {
        List<ClientDTO> clients = clientService.getOutstandingReport();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/generate-report")
    public ResponseEntity<List<ClientDTO>> generateClientReport(
            @RequestParam(required = false) String clientType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationEndDate) {
        List<ClientDTO> clients = clientService.generateClientReport(clientType, isActive, registrationStartDate, registrationEndDate);
        return ResponseEntity.ok(clients);
    }

    // ===============================================
    // UTILITY OPERATIONS
    // ===============================================

    @GetMapping("/validate-client-number/{clientNumber}")
    public ResponseEntity<Boolean> validateClientNumber(
            @PathVariable String clientNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = clientService.isClientNumberUnique(clientNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/validate-gst-number/{gstNumber}")
    public ResponseEntity<Boolean> validateGstNumber(
            @PathVariable String gstNumber,
            @RequestParam(required = false) Long excludeId) {
        boolean isUnique = clientService.isGstNumberUnique(gstNumber, excludeId);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/generate-client-number")
    public ResponseEntity<String> generateClientNumber() {
        String clientNumber = clientService.generateClientNumber();
        return ResponseEntity.ok(clientNumber);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getClientCount(
            @RequestParam(required = false) String clientType,
            @RequestParam(required = false) Boolean isActive) {
        long count = clientService.getClientCount(clientType, isActive);
        return ResponseEntity.ok(count);
    }
}

