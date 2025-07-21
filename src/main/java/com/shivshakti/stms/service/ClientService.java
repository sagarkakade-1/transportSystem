package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.ClientDTO;
import com.shivshakti.stms.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Client management
 * Defines business operations for client-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface ClientService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    /**
     * Create a new client
     * @param clientDTO Client data transfer object
     * @return Created client DTO
     */
    ClientDTO createClient(ClientDTO clientDTO);
    
    /**
     * Update an existing client
     * @param id Client ID
     * @param clientDTO Updated client data
     * @return Updated client DTO
     */
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    
    /**
     * Get client by ID
     * @param id Client ID
     * @return Client DTO if found
     */
    Optional<ClientDTO> getClientById(Long id);
    
    /**
     * Get all active clients
     * @return List of active client DTOs
     */
    List<ClientDTO> getAllActiveClients();
    
    /**
     * Get all clients with pagination
     * @param pageable Pagination information
     * @return Page of client DTOs
     */
    Page<ClientDTO> getAllClients(Pageable pageable);
    
    /**
     * Soft delete a client (mark as inactive)
     * @param id Client ID
     */
    void deleteClient(Long id);
    
    /**
     * Activate a client
     * @param id Client ID
     */
    void activateClient(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    /**
     * Search clients by multiple criteria
     * @param name Client name (partial match)
     * @param companyName Company name (partial match)
     * @param contactNumber Contact number
     * @param gstNumber GST number
     * @param isActive Active status
     * @param pageable Pagination information
     * @return Page of matching client DTOs
     */
    Page<ClientDTO> searchClients(String name, String companyName, String contactNumber, 
                                 String gstNumber, Boolean isActive, Pageable pageable);
    
    /**
     * Find client by contact number
     * @param contactNumber Contact number
     * @return Client DTO if found
     */
    Optional<ClientDTO> findByContactNumber(String contactNumber);
    
    /**
     * Find client by GST number
     * @param gstNumber GST number
     * @return Client DTO if found
     */
    Optional<ClientDTO> findByGstNumber(String gstNumber);
    
    /**
     * Find client by PAN number
     * @param panNumber PAN number
     * @return Client DTO if found
     */
    Optional<ClientDTO> findByPanNumber(String panNumber);

    // ===============================================
    // CREDIT LIMIT MANAGEMENT
    // ===============================================
    
    /**
     * Update client credit limit
     * @param id Client ID
     * @param newCreditLimit New credit limit
     * @return Updated client DTO
     */
    ClientDTO updateCreditLimit(Long id, BigDecimal newCreditLimit);
    
    /**
     * Get clients with outstanding balance
     * @return List of clients with outstanding balance
     */
    List<ClientDTO> getClientsWithOutstandingBalance();
    
    /**
     * Get clients exceeding credit limit
     * @return List of clients exceeding credit limit
     */
    List<ClientDTO> getClientsExceedingCreditLimit();
    
    /**
     * Get clients with good payment history
     * @param maxUtilizationRatio Maximum credit utilization ratio (0.0 to 1.0)
     * @return List of clients with good payment history
     */
    List<ClientDTO> getClientsWithGoodPaymentHistory(BigDecimal maxUtilizationRatio);
    
    /**
     * Calculate total outstanding balance for all clients
     * @return Total outstanding balance
     */
    BigDecimal calculateTotalOutstandingBalance();
    
    /**
     * Calculate total credit limit for all clients
     * @return Total credit limit
     */
    BigDecimal calculateTotalCreditLimit();

    // ===============================================
    // OUTSTANDING BALANCE MANAGEMENT
    // ===============================================
    
    /**
     * Update client outstanding balance
     * @param id Client ID
     * @param newBalance New outstanding balance
     * @param remarks Remarks for the update
     * @return Updated client DTO
     */
    ClientDTO updateOutstandingBalance(Long id, BigDecimal newBalance, String remarks);
    
    /**
     * Add to client outstanding balance
     * @param id Client ID
     * @param amount Amount to add
     * @param remarks Remarks for the addition
     * @return Updated client DTO
     */
    ClientDTO addToOutstandingBalance(Long id, BigDecimal amount, String remarks);
    
    /**
     * Reduce client outstanding balance
     * @param id Client ID
     * @param amount Amount to reduce
     * @param remarks Remarks for the reduction
     * @return Updated client DTO
     */
    ClientDTO reduceOutstandingBalance(Long id, BigDecimal amount, String remarks);
    
    /**
     * Check if client can take additional credit
     * @param id Client ID
     * @param additionalAmount Additional amount requested
     * @return true if client can take additional credit, false otherwise
     */
    boolean canTakeAdditionalCredit(Long id, BigDecimal additionalAmount);

    // ===============================================
    // BUSINESS ANALYTICS
    // ===============================================
    
    /**
     * Get client business summary for date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of client business summary data
     */
    List<Object[]> getClientBusinessSummary(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get top clients by business value
     * @param pageable Pagination information
     * @return Page of top clients by business value
     */
    Page<Object[]> getTopClientsByBusinessValue(Pageable pageable);
    
    /**
     * Get client payment statistics
     * @return List of client payment statistics
     */
    List<Object[]> getClientPaymentStatistics();
    
    /**
     * Get client aging report
     * @return List of client aging data
     */
    List<Object[]> getClientAgingReport();
    
    /**
     * Get aging summary
     * @return Aging summary data
     */
    Object[] getAgingSummary();

    // ===============================================
    // PAYMENT BEHAVIOR ANALYSIS
    // ===============================================
    
    /**
     * Analyze and update client payment behavior
     * @param id Client ID
     * @return Updated client DTO with payment behavior
     */
    ClientDTO analyzePaymentBehavior(Long id);
    
    /**
     * Get clients by payment behavior
     * @param behavior Payment behavior (GOOD, AVERAGE, POOR)
     * @return List of clients with specified payment behavior
     */
    List<ClientDTO> getClientsByPaymentBehavior(String behavior);
    
    /**
     * Calculate client credit score
     * @param id Client ID
     * @return Credit score (0-100)
     */
    int calculateCreditScore(Long id);

    // ===============================================
    // REPORTING
    // ===============================================
    
    /**
     * Get monthly client summary
     * @param startDate Start date
     * @param endDate End date
     * @return List of monthly summary data
     */
    List<Object[]> getMonthlyClientSummary(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get client statistics
     * @return Client statistics array
     */
    Object[] getClientStatistics();
    
    /**
     * Generate client report for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Client report data
     */
    List<ClientDTO> generateClientReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate credit analysis report
     * @return Credit analysis report data
     */
    List<ClientDTO> generateCreditAnalysisReport();

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    /**
     * Validate client data for creation
     * @param clientDTO Client data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateClientForCreation(ClientDTO clientDTO);
    
    /**
     * Validate client data for update
     * @param id Client ID
     * @param clientDTO Client data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateClientForUpdate(Long id, ClientDTO clientDTO);
    
    /**
     * Check if contact number is unique
     * @param contactNumber Contact number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isContactNumberUnique(String contactNumber, Long excludeId);
    
    /**
     * Check if GST number is unique
     * @param gstNumber GST number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isGstNumberUnique(String gstNumber, Long excludeId);
    
    /**
     * Check if PAN number is unique
     * @param panNumber PAN number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isPanNumberUnique(String panNumber, Long excludeId);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    /**
     * Convert Client entity to DTO
     * @param client Client entity
     * @return Client DTO
     */
    ClientDTO convertToDTO(Client client);
    
    /**
     * Convert ClientDTO to entity
     * @param clientDTO Client DTO
     * @return Client entity
     */
    Client convertToEntity(ClientDTO clientDTO);
    
    /**
     * Get client count by status
     * @param isActive Active status
     * @return Count of clients
     */
    long getClientCount(Boolean isActive);
    
    /**
     * Calculate credit utilization percentage
     * @param creditLimit Credit limit
     * @param outstandingBalance Outstanding balance
     * @return Credit utilization percentage
     */
    double calculateCreditUtilization(BigDecimal creditLimit, BigDecimal outstandingBalance);
}
