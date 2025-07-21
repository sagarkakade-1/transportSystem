package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.ClientDTO;
import com.shivshakti.stms.entity.Client;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.exception.DuplicateResourceException;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.repository.ClientRepository;
import com.shivshakti.stms.service.ClientService;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ClientService interface
 * Provides comprehensive client management functionality with business logic
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    
    private final ClientRepository clientRepository;
    
    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        logger.info("Creating new client: {}", clientDTO.getName());
        
        // Validate client data
        validateClientForCreation(clientDTO);
        
        // Convert DTO to entity
        Client client = convertToEntity(clientDTO);
        
        // Save client
        Client savedClient = clientRepository.save(client);
        
        logger.info("Successfully created client with ID: {}", savedClient.getId());
        return convertToDTO(savedClient);
    }
    
    @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        logger.info("Updating client with ID: {}", id);
        
        // Validate client data
        validateClientForUpdate(id, clientDTO);
        
        // Find existing client
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        // Update fields
        updateClientFields(existingClient, clientDTO);
        
        // Save updated client
        Client updatedClient = clientRepository.save(existingClient);
        
        logger.info("Successfully updated client with ID: {}", id);
        return convertToDTO(updatedClient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDTO> getClientById(Long id) {
        logger.debug("Fetching client with ID: {}", id);
        
        return clientRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllActiveClients() {
        logger.debug("Fetching all active clients");
        
        return clientRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> getAllClients(Pageable pageable) {
        logger.debug("Fetching all clients with pagination: {}", pageable);
        
        return clientRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public void deleteClient(Long id) {
        logger.info("Soft deleting client with ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        // Check if client has pending builties
        if (client.getOutstandingBalance() != null && 
            client.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessValidationException("Cannot delete client with outstanding balance");
        }
        
        client.setIsActive(false);
        clientRepository.save(client);
        
        logger.info("Successfully deactivated client with ID: {}", id);
    }
    
    @Override
    public void activateClient(Long id) {
        logger.info("Activating client with ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        client.setIsActive(true);
        clientRepository.save(client);
        
        logger.info("Successfully activated client with ID: {}", id);
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> searchClients(String name, String companyName, String contactNumber, 
                                        String gstNumber, Boolean isActive, Pageable pageable) {
        logger.debug("Searching clients with criteria - name: {}, company: {}, contact: {}, gst: {}, active: {}", 
                    name, companyName, contactNumber, gstNumber, isActive);
        
        return clientRepository.searchClients(name, companyName, contactNumber, gstNumber, isActive, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDTO> findByContactNumber(String contactNumber) {
        logger.debug("Finding client by contact number: {}", contactNumber);
        
        return clientRepository.findByContactNumber(contactNumber)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDTO> findByGstNumber(String gstNumber) {
        logger.debug("Finding client by GST number: {}", gstNumber);
        
        return clientRepository.findByGstNumber(gstNumber)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientDTO> findByPanNumber(String panNumber) {
        logger.debug("Finding client by PAN number: {}", panNumber);
        
        return clientRepository.findByPanNumber(panNumber)
                .map(this::convertToDTO);
    }

    // ===============================================
    // CREDIT LIMIT MANAGEMENT
    // ===============================================
    
    @Override
    public ClientDTO updateCreditLimit(Long id, BigDecimal newCreditLimit) {
        logger.info("Updating credit limit for client ID: {} to {}", id, newCreditLimit);
        
        if (newCreditLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Credit limit cannot be negative");
        }
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        BigDecimal oldLimit = client.getCreditLimit();
        client.setCreditLimit(newCreditLimit);
        
        // Add audit trail
        String auditMessage = String.format("Credit limit updated from %s to %s on %s", 
                                           oldLimit, newCreditLimit, LocalDate.now());
        addToRemarks(client, auditMessage);
        
        Client updatedClient = clientRepository.save(client);
        
        logger.info("Successfully updated credit limit for client ID: {}", id);
        return convertToDTO(updatedClient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getClientsWithOutstandingBalance() {
        logger.debug("Fetching clients with outstanding balance");
        
        return clientRepository.findClientsWithOutstandingBalance()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getClientsExceedingCreditLimit() {
        logger.debug("Fetching clients exceeding credit limit");
        
        return clientRepository.findClientsExceedingCreditLimit()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getClientsWithGoodPaymentHistory(BigDecimal maxUtilizationRatio) {
        logger.debug("Fetching clients with good payment history (max utilization: {})", maxUtilizationRatio);
        
        return clientRepository.findClientsWithGoodPaymentHistory(maxUtilizationRatio)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalOutstandingBalance() {
        logger.debug("Calculating total outstanding balance");
        
        return clientRepository.calculateTotalOutstandingBalance();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalCreditLimit() {
        logger.debug("Calculating total credit limit");
        
        return clientRepository.calculateTotalCreditLimit();
    }

    // ===============================================
    // OUTSTANDING BALANCE MANAGEMENT
    // ===============================================
    
    @Override
    public ClientDTO updateOutstandingBalance(Long id, BigDecimal newBalance, String remarks) {
        logger.info("Updating outstanding balance for client ID: {} to {}", id, newBalance);
        
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Outstanding balance cannot be negative");
        }
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        BigDecimal oldBalance = client.getOutstandingBalance();
        client.setOutstandingBalance(newBalance);
        
        // Add audit trail
        String auditMessage = String.format("Outstanding balance updated from %s to %s on %s", 
                                           oldBalance, newBalance, LocalDate.now());
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(client, auditMessage);
        
        Client updatedClient = clientRepository.save(client);
        
        logger.info("Successfully updated outstanding balance for client ID: {}", id);
        return convertToDTO(updatedClient);
    }
    
    @Override
    public ClientDTO addToOutstandingBalance(Long id, BigDecimal amount, String remarks) {
        logger.info("Adding {} to outstanding balance for client ID: {}", amount, id);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Amount to add must be greater than zero");
        }
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        BigDecimal currentBalance = client.getOutstandingBalance() != null ? 
                                   client.getOutstandingBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.add(amount);
        
        // Check credit limit
        if (client.getCreditLimit() != null && 
            client.getCreditLimit().compareTo(BigDecimal.ZERO) > 0 &&
            newBalance.compareTo(client.getCreditLimit()) > 0) {
            logger.warn("Client ID: {} will exceed credit limit. New balance: {}, Credit limit: {}", 
                       id, newBalance, client.getCreditLimit());
        }
        
        client.setOutstandingBalance(newBalance);
        
        // Add audit trail
        String auditMessage = String.format("Added %s to outstanding balance on %s. New balance: %s", 
                                           amount, LocalDate.now(), newBalance);
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(client, auditMessage);
        
        Client updatedClient = clientRepository.save(client);
        
        logger.info("Successfully added to outstanding balance for client ID: {}", id);
        return convertToDTO(updatedClient);
    }
    
    @Override
    public ClientDTO reduceOutstandingBalance(Long id, BigDecimal amount, String remarks) {
        logger.info("Reducing outstanding balance by {} for client ID: {}", amount, id);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Amount to reduce must be greater than zero");
        }
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        BigDecimal currentBalance = client.getOutstandingBalance() != null ? 
                                   client.getOutstandingBalance() : BigDecimal.ZERO;
        
        if (currentBalance.compareTo(amount) < 0) {
            throw new BusinessValidationException("Reduction amount cannot exceed current outstanding balance");
        }
        
        BigDecimal newBalance = currentBalance.subtract(amount);
        client.setOutstandingBalance(newBalance);
        
        // Add audit trail
        String auditMessage = String.format("Reduced outstanding balance by %s on %s. New balance: %s", 
                                           amount, LocalDate.now(), newBalance);
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(client, auditMessage);
        
        Client updatedClient = clientRepository.save(client);
        
        logger.info("Successfully reduced outstanding balance for client ID: {}", id);
        return convertToDTO(updatedClient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canTakeAdditionalCredit(Long id, BigDecimal additionalAmount) {
        logger.debug("Checking if client ID: {} can take additional credit of {}", id, additionalAmount);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        if (client.getCreditLimit() == null || client.getCreditLimit().compareTo(BigDecimal.ZERO) == 0) {
            return false; // No credit limit set
        }
        
        BigDecimal currentBalance = client.getOutstandingBalance() != null ? 
                                   client.getOutstandingBalance() : BigDecimal.ZERO;
        BigDecimal totalAfterCredit = currentBalance.add(additionalAmount);
        
        return totalAfterCredit.compareTo(client.getCreditLimit()) <= 0;
    }

    // ===============================================
    // BUSINESS ANALYTICS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientBusinessSummary(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating client business summary from {} to {}", startDate, endDate);
        
        return clientRepository.getClientBusinessSummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Object[]> getTopClientsByBusinessValue(Pageable pageable) {
        logger.debug("Fetching top clients by business value");
        
        return clientRepository.findTopClientsByBusinessValue(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientPaymentStatistics() {
        logger.debug("Fetching client payment statistics");
        
        return clientRepository.getClientPaymentStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientAgingReport() {
        logger.debug("Generating client aging report");
        
        return clientRepository.getClientAgingReport();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getAgingSummary() {
        logger.debug("Fetching aging summary");
        
        return clientRepository.getAgingSummary();
    }

    // ===============================================
    // PAYMENT BEHAVIOR ANALYSIS
    // ===============================================
    
    @Override
    public ClientDTO analyzePaymentBehavior(Long id) {
        logger.info("Analyzing payment behavior for client ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        // Calculate payment behavior based on credit utilization and payment history
        String behavior = calculatePaymentBehavior(client);
        
        // Update client with payment behavior (if we had this field in entity)
        // For now, we'll just return the DTO with calculated behavior
        ClientDTO dto = convertToDTO(client);
        dto.setPaymentBehavior(behavior);
        
        logger.info("Payment behavior for client ID: {} is {}", id, behavior);
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getClientsByPaymentBehavior(String behavior) {
        logger.debug("Fetching clients with payment behavior: {}", behavior);
        
        // This would require additional logic to filter clients by payment behavior
        // For now, return all active clients and calculate behavior on the fly
        return getAllActiveClients().stream()
                .map(dto -> {
                    Client client = clientRepository.findById(dto.getId()).orElse(null);
                    if (client != null) {
                        dto.setPaymentBehavior(calculatePaymentBehavior(client));
                    }
                    return dto;
                })
                .filter(dto -> behavior.equals(dto.getPaymentBehavior()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public int calculateCreditScore(Long id) {
        logger.debug("Calculating credit score for client ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        
        int score = 100; // Start with perfect score
        
        // Reduce score based on credit utilization
        if (client.getCreditLimit() != null && client.getOutstandingBalance() != null) {
            double utilization = calculateCreditUtilization(client.getCreditLimit(), client.getOutstandingBalance());
            if (utilization > 80) {
                score -= 30;
            } else if (utilization > 60) {
                score -= 20;
            } else if (utilization > 40) {
                score -= 10;
            }
        }
        
        // Reduce score if exceeding credit limit
        if (client.getCreditLimit() != null && client.getOutstandingBalance() != null &&
            client.getOutstandingBalance().compareTo(client.getCreditLimit()) > 0) {
            score -= 40;
        }
        
        // Additional factors could be added here (payment history, overdue amounts, etc.)
        
        return Math.max(0, Math.min(100, score)); // Ensure score is between 0 and 100
    }

    // ===============================================
    // REPORTING
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyClientSummary(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating monthly client summary from {} to {}", startDate, endDate);
        
        return clientRepository.getMonthlyClientSummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getClientStatistics() {
        logger.debug("Fetching client statistics");
        
        return clientRepository.getClientStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> generateClientReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating client report from {} to {}", startDate, endDate);
        
        return getAllActiveClients().stream()
                .map(dto -> {
                    // Enhance DTO with additional metrics
                    Client client = clientRepository.findById(dto.getId()).orElse(null);
                    if (client != null) {
                        dto.setPaymentBehavior(calculatePaymentBehavior(client));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> generateCreditAnalysisReport() {
        logger.info("Generating credit analysis report");
        
        return getAllActiveClients().stream()
                .filter(dto -> dto.getCreditLimit() != null && dto.getCreditLimit().compareTo(BigDecimal.ZERO) > 0)
                .map(dto -> {
                    // Calculate additional credit metrics
                    dto.setCreditUtilization(BigDecimal.valueOf(dto.getCreditUtilizationPercentage()));
                    Client client = clientRepository.findById(dto.getId()).orElse(null);
                    if (client != null) {
                        dto.setPaymentBehavior(calculatePaymentBehavior(client));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    @Override
    public void validateClientForCreation(ClientDTO clientDTO) {
        logger.debug("Validating client data for creation");
        
        // Check contact number uniqueness
        if (!isContactNumberUnique(clientDTO.getContactNumber(), null)) {
            throw new DuplicateResourceException("Client", "contact number", clientDTO.getContactNumber());
        }
        
        // Check GST number uniqueness (if provided)
        if (StringUtils.hasText(clientDTO.getGstNumber()) && 
            !isGstNumberUnique(clientDTO.getGstNumber(), null)) {
            throw new DuplicateResourceException("Client", "GST number", clientDTO.getGstNumber());
        }
        
        // Check PAN number uniqueness (if provided)
        if (StringUtils.hasText(clientDTO.getPanNumber()) && 
            !isPanNumberUnique(clientDTO.getPanNumber(), null)) {
            throw new DuplicateResourceException("Client", "PAN number", clientDTO.getPanNumber());
        }
        
        // Validate credit limit
        if (clientDTO.getCreditLimit() != null && clientDTO.getCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Credit limit cannot be negative");
        }
        
        // Validate outstanding balance
        if (clientDTO.getOutstandingBalance() != null && clientDTO.getOutstandingBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Outstanding balance cannot be negative");
        }
    }
    
    @Override
    public void validateClientForUpdate(Long id, ClientDTO clientDTO) {
        logger.debug("Validating client data for update");
        
        // Check contact number uniqueness
        if (!isContactNumberUnique(clientDTO.getContactNumber(), id)) {
            throw new DuplicateResourceException("Client", "contact number", clientDTO.getContactNumber());
        }
        
        // Check GST number uniqueness (if provided)
        if (StringUtils.hasText(clientDTO.getGstNumber()) && 
            !isGstNumberUnique(clientDTO.getGstNumber(), id)) {
            throw new DuplicateResourceException("Client", "GST number", clientDTO.getGstNumber());
        }
        
        // Check PAN number uniqueness (if provided)
        if (StringUtils.hasText(clientDTO.getPanNumber()) && 
            !isPanNumberUnique(clientDTO.getPanNumber(), id)) {
            throw new DuplicateResourceException("Client", "PAN number", clientDTO.getPanNumber());
        }
        
        // Validate credit limit
        if (clientDTO.getCreditLimit() != null && clientDTO.getCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Credit limit cannot be negative");
        }
        
        // Validate outstanding balance
        if (clientDTO.getOutstandingBalance() != null && clientDTO.getOutstandingBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Outstanding balance cannot be negative");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContactNumberUnique(String contactNumber, Long excludeId) {
        if (excludeId != null) {
            return !clientRepository.existsByContactNumberAndIdNot(contactNumber, excludeId);
        }
        return clientRepository.findByContactNumber(contactNumber).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isGstNumberUnique(String gstNumber, Long excludeId) {
        if (!StringUtils.hasText(gstNumber)) {
            return true; // GST number is optional
        }
        
        if (excludeId != null) {
            return !clientRepository.existsByGstNumberAndIdNot(gstNumber, excludeId);
        }
        return clientRepository.findByGstNumber(gstNumber).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPanNumberUnique(String panNumber, Long excludeId) {
        if (!StringUtils.hasText(panNumber)) {
            return true; // PAN number is optional
        }
        
        if (excludeId != null) {
            return !clientRepository.existsByPanNumberAndIdNot(panNumber, excludeId);
        }
        return clientRepository.findByPanNumber(panNumber).isEmpty();
    }
    
    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    @Override
    public ClientDTO convertToDTO(Client client) {
        if (client == null) {
            return null;
        }
        
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setCompanyName(client.getCompanyName());
        dto.setContactNumber(client.getContactNumber());
        dto.setAlternateContactNumber(client.getAlternateContactNumber());
        dto.setEmail(client.getEmail());
        dto.setAddress(client.getAddress());
        dto.setGstNumber(client.getGstNumber());
        dto.setPanNumber(client.getPanNumber());
        dto.setCreditLimit(client.getCreditLimit());
        dto.setOutstandingBalance(client.getOutstandingBalance());
        dto.setContactPerson(client.getContactPerson());
        dto.setContactPersonNumber(client.getContactPersonNumber());
        dto.setPaymentTerms(client.getPaymentTerms());
        dto.setRemarks(client.getRemarks());
        dto.setIsActive(client.getIsActive());
        
        // Calculate credit utilization
        if (client.getCreditLimit() != null && client.getOutstandingBalance() != null) {
            dto.setCreditUtilization(calculateCreditUtilization(client.getCreditLimit(), client.getOutstandingBalance()));
        }
        
        return dto;
    }
    
    @Override
    public Client convertToEntity(ClientDTO clientDTO) {
        if (clientDTO == null) {
            return null;
        }
        
        Client client = new Client();
        client.setId(clientDTO.getId());
        client.setName(clientDTO.getName());
        client.setCompanyName(clientDTO.getCompanyName());
        client.setContactNumber(clientDTO.getContactNumber());
        client.setAlternateContactNumber(clientDTO.getAlternateContactNumber());
        client.setEmail(clientDTO.getEmail());
        client.setAddress(clientDTO.getAddress());
        client.setGstNumber(clientDTO.getGstNumber());
        client.setPanNumber(clientDTO.getPanNumber());
        client.setCreditLimit(clientDTO.getCreditLimit());
        client.setOutstandingBalance(clientDTO.getOutstandingBalance());
        client.setContactPerson(clientDTO.getContactPerson());
        client.setContactPersonNumber(clientDTO.getContactPersonNumber());
        client.setPaymentTerms(clientDTO.getPaymentTerms());
        client.setRemarks(clientDTO.getRemarks());
        client.setIsActive(clientDTO.getIsActive());
        
        return client;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getClientCount(Boolean isActive) {
        if (isActive != null && isActive) {
            return clientRepository.countByIsActiveTrue();
        }
        return clientRepository.count();
    }
    
    @Override
    public double calculateCreditUtilization(BigDecimal creditLimit, BigDecimal outstandingBalance) {
        if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) == 0 || outstandingBalance == null) {
            return 0.0;
        }
        return outstandingBalance.divide(creditLimit, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
    }
    
    // ===============================================
    // PRIVATE HELPER METHODS
    // ===============================================
    
    private void updateClientFields(Client existingClient, ClientDTO clientDTO) {
        existingClient.setName(clientDTO.getName());
        existingClient.setCompanyName(clientDTO.getCompanyName());
        existingClient.setContactNumber(clientDTO.getContactNumber());
        existingClient.setAlternateContactNumber(clientDTO.getAlternateContactNumber());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setAddress(clientDTO.getAddress());
        existingClient.setGstNumber(clientDTO.getGstNumber());
        existingClient.setPanNumber(clientDTO.getPanNumber());
        existingClient.setContactPerson(clientDTO.getContactPerson());
        existingClient.setContactPersonNumber(clientDTO.getContactPersonNumber());
        existingClient.setPaymentTerms(clientDTO.getPaymentTerms());
        existingClient.setRemarks(clientDTO.getRemarks());
        
        // Don't update credit limit and outstanding balance through general update
        // Use specific methods for these fields
    }
    
    private void addToRemarks(Client client, String newRemark) {
        String currentRemarks = client.getRemarks() != null ? client.getRemarks() : "";
        client.setRemarks(currentRemarks + "\n" + newRemark);
    }
    
    private String calculatePaymentBehavior(Client client) {
        if (client.getCreditLimit() == null || client.getCreditLimit().compareTo(BigDecimal.ZERO) == 0) {
            return "AVERAGE"; // No credit limit set
        }
        
        double utilization = calculateCreditUtilization(client.getCreditLimit(), client.getOutstandingBalance());
        
        // Determine behavior based on credit utilization
        if (utilization <= 30) {
            return "GOOD";
        } else if (utilization <= 70) {
            return "AVERAGE";
        } else {
            return "POOR";
        }
    }
}
