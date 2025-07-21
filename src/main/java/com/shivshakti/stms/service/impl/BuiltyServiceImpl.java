package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.BuiltyDTO;
import com.shivshakti.stms.entity.Builty;
import com.shivshakti.stms.entity.Trip;
import com.shivshakti.stms.entity.Client;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.exception.DuplicateResourceException;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.repository.BuiltyRepository;
import com.shivshakti.stms.repository.TripRepository;
import com.shivshakti.stms.repository.ClientRepository;
import com.shivshakti.stms.service.BuiltyService;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BuiltyService interface
 * Provides comprehensive builty management functionality with business logic
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class BuiltyServiceImpl implements BuiltyService {

    private static final Logger logger = LoggerFactory.getLogger(BuiltyServiceImpl.class);
    
    private final BuiltyRepository builtyRepository;
    private final TripRepository tripRepository;
    private final ClientRepository clientRepository;
    
    @Autowired
    public BuiltyServiceImpl(BuiltyRepository builtyRepository, TripRepository tripRepository,
                            ClientRepository clientRepository) {
        this.builtyRepository = builtyRepository;
        this.tripRepository = tripRepository;
        this.clientRepository = clientRepository;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    @Override
    public BuiltyDTO createBuilty(BuiltyDTO builtyDTO) {
        logger.info("Creating new builty: {}", builtyDTO.getBuiltyNumber());
        
        // Validate builty data
        validateBuiltyForCreation(builtyDTO);
        
        // Generate builty number if not provided
        if (!StringUtils.hasText(builtyDTO.getBuiltyNumber())) {
            builtyDTO.setBuiltyNumber(generateBuiltyNumber());
        }
        
        // Convert DTO to entity
        Builty builty = convertToEntity(builtyDTO);
        
        // Set default values
        if (builty.getBuiltyDate() == null) {
            builty.setBuiltyDate(LocalDate.now());
        }
        
        // Calculate payment due date if not provided
        if (builty.getPaymentDueDate() == null) {
            builty.setPaymentDueDate(calculatePaymentDueDate(builty.getBuiltyDate(), 30)); // 30 days default
        }
        
        // Save builty
        Builty savedBuilty = builtyRepository.save(builty);
        
        logger.info("Successfully created builty with ID: {}", savedBuilty.getId());
        return convertToDTO(savedBuilty);
    }
    
    @Override
    public BuiltyDTO updateBuilty(Long id, BuiltyDTO builtyDTO) {
        logger.info("Updating builty with ID: {}", id);
        
        // Validate builty data
        validateBuiltyForUpdate(id, builtyDTO);
        
        // Find existing builty
        Builty existingBuilty = builtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Builty", id));
        
        // Check if builty can be updated
        if ("DELIVERED".equals(existingBuilty.getDeliveryStatus()) && "PAID".equals(existingBuilty.getPaymentStatus())) {
            throw new BusinessValidationException("Cannot update delivered and paid builty");
        }
        
        // Update fields
        updateBuiltyFields(existingBuilty, builtyDTO);
        
        // Save updated builty
        Builty updatedBuilty = builtyRepository.save(existingBuilty);
        
        logger.info("Successfully updated builty with ID: {}", id);
        return convertToDTO(updatedBuilty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BuiltyDTO> getBuiltyById(Long id) {
        logger.debug("Fetching builty with ID: {}", id);
        
        return builtyRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getAllBuilties(Pageable pageable) {
        logger.debug("Fetching all builties with pagination: {}", pageable);
        
        return builtyRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public void deleteBuilty(Long id) {
        logger.info("Deleting builty with ID: {}", id);
        
        if (!canDeleteBuilty(id)) {
            throw new BusinessValidationException("Cannot delete builty that is delivered or has payments");
        }
        
        builtyRepository.deleteById(id);
        
        logger.info("Successfully deleted builty with ID: {}", id);
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> searchBuilties(String builtyNumber, Long tripId, Long clientId,
                                         String paymentStatus, String deliveryStatus,
                                         LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.debug("Searching builties with criteria");
        
        return builtyRepository.searchBuilties(builtyNumber, tripId, clientId, paymentStatus,
                                              deliveryStatus, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<BuiltyDTO> findByBuiltyNumber(String builtyNumber) {
        logger.debug("Finding builty by builty number: {}", builtyNumber);
        
        return builtyRepository.findByBuiltyNumber(builtyNumber)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getBuiltiesByPaymentStatus(String paymentStatus, Pageable pageable) {
        logger.debug("Fetching builties by payment status: {}", paymentStatus);
        
        return builtyRepository.findByPaymentStatus(paymentStatus, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getBuiltiesByDeliveryStatus(String deliveryStatus, Pageable pageable) {
        logger.debug("Fetching builties by delivery status: {}", deliveryStatus);
        
        return builtyRepository.findByDeliveryStatus(deliveryStatus, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getBuiltiesByTrip(Long tripId, Pageable pageable) {
        logger.debug("Fetching builties by trip ID: {}", tripId);
        
        return builtyRepository.findByTripId(tripId, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getBuiltiesByClient(Long clientId, Pageable pageable) {
        logger.debug("Fetching builties by client ID: {}", clientId);
        
        return builtyRepository.findByClientId(clientId, pageable)
                .map(this::convertToDTO);
    }

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================
    
    @Override
    public BuiltyDTO updatePaymentStatus(Long id, String paymentStatus, String remarks) {
        logger.info("Updating payment status for builty ID: {} to {}", id, paymentStatus);
        
        Builty builty = builtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Builty", id));
        
        String oldStatus = builty.getPaymentStatus();
        builty.setPaymentStatus(paymentStatus);
        
        // Add audit trail
        String auditMessage = String.format("Payment status updated from %s to %s on %s", 
                                           oldStatus, paymentStatus, LocalDate.now());
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(builty, auditMessage);
        
        Builty updatedBuilty = builtyRepository.save(builty);
        
        logger.info("Successfully updated payment status for builty ID: {}", id);
        return convertToDTO(updatedBuilty);
    }
    
    @Override
    public BuiltyDTO addPayment(Long id, BigDecimal paymentAmount, LocalDate paymentDate,
                               String paymentMethod, String remarks) {
        logger.info("Adding payment of {} for builty ID: {}", paymentAmount, id);
        
        if (!validatePaymentAmount(id, paymentAmount)) {
            throw new BusinessValidationException("Invalid payment amount");
        }
        
        Builty builty = builtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Builty", id));
        
        BigDecimal currentAdvance = builty.getAdvanceAmount() != null ? 
                                   builty.getAdvanceAmount() : BigDecimal.ZERO;
        BigDecimal newAdvance = currentAdvance.add(paymentAmount);
        builty.setAdvanceAmount(newAdvance);
        
        // Update payment status based on amount
        BigDecimal totalAmount = calculateTotalAmount(builty);
        if (newAdvance.compareTo(totalAmount) >= 0) {
            builty.setPaymentStatus("PAID");
        } else if (newAdvance.compareTo(BigDecimal.ZERO) > 0) {
            builty.setPaymentStatus("PARTIAL");
        }
        
        // Add audit trail
        String auditMessage = String.format("Payment of %s received on %s via %s. Total paid: %s", 
                                           paymentAmount, paymentDate, paymentMethod, newAdvance);
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(builty, auditMessage);
        
        Builty updatedBuilty = builtyRepository.save(builty);
        
        logger.info("Successfully added payment for builty ID: {}", id);
        return convertToDTO(updatedBuilty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getPendingPayments(Pageable pageable) {
        return builtyRepository.findByPaymentStatus("PENDING", pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getOverduePayments(Pageable pageable) {
        return builtyRepository.findOverduePayments(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getPartialPayments(Pageable pageable) {
        return builtyRepository.findByPaymentStatus("PARTIAL", pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOutstandingAmount(Long clientId) {
        return builtyRepository.calculateOutstandingAmount(clientId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPaymentSummary(LocalDate startDate, LocalDate endDate) {
        return builtyRepository.getPaymentSummary(startDate, endDate);
    }

    // ===============================================
    // DELIVERY MANAGEMENT
    // ===============================================
    
    @Override
    public BuiltyDTO updateDeliveryStatus(Long id, String deliveryStatus, LocalDate deliveryDate, String remarks) {
        logger.info("Updating delivery status for builty ID: {} to {}", id, deliveryStatus);
        
        Builty builty = builtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Builty", id));
        
        String oldStatus = builty.getDeliveryStatus();
        builty.setDeliveryStatus(deliveryStatus);
        
        if (deliveryDate != null) {
            builty.setDeliveryDate(deliveryDate);
        }
        
        // Add audit trail
        String auditMessage = String.format("Delivery status updated from %s to %s on %s", 
                                           oldStatus, deliveryStatus, LocalDate.now());
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(builty, auditMessage);
        
        Builty updatedBuilty = builtyRepository.save(builty);
        
        logger.info("Successfully updated delivery status for builty ID: {}", id);
        return convertToDTO(updatedBuilty);
    }
    
    @Override
    public BuiltyDTO markAsDelivered(Long id, LocalDate deliveryDate, String receivedBy, String remarks) {
        logger.info("Marking builty ID: {} as delivered", id);
        
        Builty builty = builtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Builty", id));
        
        builty.setDeliveryStatus("DELIVERED");
        builty.setDeliveryDate(deliveryDate);
        
        // Add audit trail
        String auditMessage = String.format("Goods delivered on %s. Received by: %s", 
                                           deliveryDate, receivedBy);
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(builty, auditMessage);
        
        Builty updatedBuilty = builtyRepository.save(builty);
        
        logger.info("Successfully marked builty ID: {} as delivered", id);
        return convertToDTO(updatedBuilty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getPendingDeliveries(Pageable pageable) {
        return builtyRepository.findByDeliveryStatus("PENDING", pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getInTransitBuilties(Pageable pageable) {
        return builtyRepository.findByDeliveryStatus("IN_TRANSIT", pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BuiltyDTO> getDeliveredBuilties(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return builtyRepository.findDeliveredBuilties(startDate, endDate, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDeliveryPerformanceReport(LocalDate startDate, LocalDate endDate) {
        return builtyRepository.getDeliveryPerformanceReport(startDate, endDate);
    }

    // ===============================================
    // FINANCIAL OPERATIONS
    // ===============================================
    
    @Override
    public BigDecimal calculateGSTAmount(BigDecimal totalCharges, BigDecimal gstRate) {
        if (totalCharges == null || gstRate == null) {
            return BigDecimal.ZERO;
        }
        return totalCharges.multiply(gstRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
    }
    
    @Override
    public BuiltyDTO updateFreightCharges(Long id, BigDecimal newCharges, String reason) {
        logger.info("Updating freight charges for builty ID: {} to {}", id, newCharges);
        
        if (newCharges.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Freight charges must be greater than zero");
        }
        
        Builty builty = builtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Builty", id));
        
        BigDecimal oldCharges = builty.getFreightCharges();
        builty.setFreightCharges(newCharges);
        
        // Add audit trail
        String auditMessage = String.format("Freight charges updated from %s to %s on %s", 
                                           oldCharges, newCharges, LocalDate.now());
        if (StringUtils.hasText(reason)) {
            auditMessage += " - Reason: " + reason;
        }
        addToRemarks(builty, auditMessage);
        
        Builty updatedBuilty = builtyRepository.save(builty);
        
        logger.info("Successfully updated freight charges for builty ID: {}", id);
        return convertToDTO(updatedBuilty);
    }
    
    @Override
    public BuiltyDTO addAdditionalCharges(Long id, String chargeType, BigDecimal amount, String description) {
        logger.info("Adding {} charges of {} for builty ID: {}", chargeType, amount, id);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Charge amount must be greater than zero");
        }
        
        Builty builty = builtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Builty", id));
        
        // Add charges based on type
        switch (chargeType.toUpperCase()) {
            case "LOADING":
                BigDecimal currentLoading = builty.getLoadingCharges() != null ? 
                                          builty.getLoadingCharges() : BigDecimal.ZERO;
                builty.setLoadingCharges(currentLoading.add(amount));
                break;
            case "UNLOADING":
                BigDecimal currentUnloading = builty.getUnloadingCharges() != null ? 
                                             builty.getUnloadingCharges() : BigDecimal.ZERO;
                builty.setUnloadingCharges(currentUnloading.add(amount));
                break;
            case "OTHER":
                BigDecimal currentOther = builty.getOtherCharges() != null ? 
                                        builty.getOtherCharges() : BigDecimal.ZERO;
                builty.setOtherCharges(currentOther.add(amount));
                break;
            default:
                throw new BusinessValidationException("Invalid charge type: " + chargeType);
        }
        
        // Add audit trail
        String auditMessage = String.format("%s charges of %s added on %s", 
                                           chargeType, amount, LocalDate.now());
        if (StringUtils.hasText(description)) {
            auditMessage += " - " + description;
        }
        addToRemarks(builty, auditMessage);
        
        Builty updatedBuilty = builtyRepository.save(builty);
        
        logger.info("Successfully added {} charges for builty ID: {}", chargeType, id);
        return convertToDTO(updatedBuilty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getRevenueReport(LocalDate startDate, LocalDate endDate) {
        return builtyRepository.getRevenueReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientWiseRevenue(LocalDate startDate, LocalDate endDate) {
        return builtyRepository.getClientWiseRevenue(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return builtyRepository.calculateTotalRevenue(startDate, endDate);
    }

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getBuiltyStatistics() {
        return builtyRepository.getBuiltyStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyBuiltySummary(LocalDate startDate, LocalDate endDate) {
        return builtyRepository.getMonthlyBuiltySummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BuiltyDTO> generateBuiltyReport(LocalDate startDate, LocalDate endDate,
                                               String paymentStatus, String deliveryStatus) {
        return builtyRepository.findBuiltiesForReport(startDate, endDate, paymentStatus, deliveryStatus)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAgingReport() {
        return builtyRepository.getAgingReport();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopClientsByRevenue(LocalDate startDate, LocalDate endDate, int limit) {
        return builtyRepository.getTopClientsByRevenue(startDate, endDate, limit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getGoodsAnalysisReport(LocalDate startDate, LocalDate endDate) {
        return builtyRepository.getGoodsAnalysisReport(startDate, endDate);
    }

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    @Override
    public void validateBuiltyForCreation(BuiltyDTO builtyDTO) {
        logger.debug("Validating builty data for creation");
        
        // Check builty number uniqueness
        if (StringUtils.hasText(builtyDTO.getBuiltyNumber()) && 
            !isBuiltyNumberUnique(builtyDTO.getBuiltyNumber(), null)) {
            throw new DuplicateResourceException("Builty", "builty number", builtyDTO.getBuiltyNumber());
        }
        
        // Validate trip exists
        if (builtyDTO.getTripId() != null) {
            Trip trip = tripRepository.findById(builtyDTO.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip", builtyDTO.getTripId()));
            
            if (!"COMPLETED".equals(trip.getStatus())) {
                throw new BusinessValidationException("Can only create builty for completed trips");
            }
        }
        
        // Validate client exists and is active
        if (builtyDTO.getClientId() != null) {
            Client client = clientRepository.findById(builtyDTO.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", builtyDTO.getClientId()));
            
            if (!client.getIsActive()) {
                throw new BusinessValidationException("Cannot create builty for inactive client");
            }
        }
        
        // Validate freight charges
        if (builtyDTO.getFreightCharges().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Freight charges must be greater than zero");
        }
        
        // Validate goods weight
        if (builtyDTO.getGoodsWeight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Goods weight must be greater than zero");
        }
        
        // Validate number of packages
        if (builtyDTO.getNumberOfPackages() <= 0) {
            throw new BusinessValidationException("Number of packages must be greater than zero");
        }
    }
    
    @Override
    public void validateBuiltyForUpdate(Long id, BuiltyDTO builtyDTO) {
        logger.debug("Validating builty data for update");
        
        // Check builty number uniqueness
        if (StringUtils.hasText(builtyDTO.getBuiltyNumber()) && 
            !isBuiltyNumberUnique(builtyDTO.getBuiltyNumber(), id)) {
            throw new DuplicateResourceException("Builty", "builty number", builtyDTO.getBuiltyNumber());
        }
        
        // Similar validations as creation
        validateBuiltyForCreation(builtyDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isBuiltyNumberUnique(String builtyNumber, Long excludeId) {
        if (excludeId != null) {
            return !builtyRepository.existsByBuiltyNumberAndIdNot(builtyNumber, excludeId);
        }
        return builtyRepository.findByBuiltyNumber(builtyNumber).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validatePaymentAmount(Long builtyId, BigDecimal paymentAmount) {
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        Builty builty = builtyRepository.findById(builtyId).orElse(null);
        if (builty == null) {
            return false;
        }
        
        BigDecimal totalAmount = calculateTotalAmount(builty);
        BigDecimal currentAdvance = builty.getAdvanceAmount() != null ? 
                                   builty.getAdvanceAmount() : BigDecimal.ZERO;
        BigDecimal remainingAmount = totalAmount.subtract(currentAdvance);
        
        return paymentAmount.compareTo(remainingAmount) <= 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canDeleteBuilty(Long id) {
        Builty builty = builtyRepository.findById(id).orElse(null);
        if (builty == null) {
            return false;
        }
        
        // Cannot delete if delivered or has payments
        return !"DELIVERED".equals(builty.getDeliveryStatus()) && 
               (builty.getAdvanceAmount() == null || builty.getAdvanceAmount().compareTo(BigDecimal.ZERO) == 0);
    }

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    @Override
    public BuiltyDTO convertToDTO(Builty builty) {
        if (builty == null) {
            return null;
        }
        
        BuiltyDTO dto = new BuiltyDTO();
        dto.setId(builty.getId());
        dto.setBuiltyNumber(builty.getBuiltyNumber());
        dto.setTripId(builty.getTrip() != null ? builty.getTrip().getId() : null);
        dto.setClientId(builty.getClient() != null ? builty.getClient().getId() : null);
        dto.setConsignorName(builty.getConsignorName());
        dto.setConsignorAddress(builty.getConsignorAddress());
        dto.setConsignorPhone(builty.getConsignorPhone());
        dto.setConsigneeName(builty.getConsigneeName());
        dto.setConsigneeAddress(builty.getConsigneeAddress());
        dto.setConsigneePhone(builty.getConsigneePhone());
        dto.setGoodsDescription(builty.getGoodsDescription());
        dto.setGoodsWeight(builty.getGoodsWeight());
        dto.setGoodsValue(builty.getGoodsValue());
        dto.setNumberOfPackages(builty.getNumberOfPackages());
        dto.setPackageType(builty.getPackageType());
        dto.setFreightCharges(builty.getFreightCharges());
        dto.setLoadingCharges(builty.getLoadingCharges());
        dto.setUnloadingCharges(builty.getUnloadingCharges());
        dto.setOtherCharges(builty.getOtherCharges());
        dto.setGstAmount(builty.getGstAmount());
        dto.setAdvanceAmount(builty.getAdvanceAmount());
        dto.setPaymentStatus(builty.getPaymentStatus());
        dto.setDeliveryStatus(builty.getDeliveryStatus());
        dto.setBuiltyDate(builty.getBuiltyDate());
        dto.setDeliveryDate(builty.getDeliveryDate());
        dto.setPaymentDueDate(builty.getPaymentDueDate());
        dto.setRemarks(builty.getRemarks());
        dto.setSpecialInstructions(builty.getSpecialInstructions());
        
        // Set related entity information
        if (builty.getTrip() != null) {
            dto.setTripNumber(builty.getTrip().getTripNumber());
            if (builty.getTrip().getTruck() != null) {
                dto.setTruckNumber(builty.getTrip().getTruck().getTruckNumber());
            }
            if (builty.getTrip().getDriver() != null) {
                dto.setDriverName(builty.getTrip().getDriver().getName());
            }
        }
        
        if (builty.getClient() != null) {
            dto.setClientName(builty.getClient().getName());
        }
        
        // Calculate derived fields
        dto.setTotalCharges(dto.calculateTotalCharges());
        dto.setTotalAmount(dto.calculateTotalAmount());
        dto.setBalanceAmount(dto.calculateBalanceAmount());
        
        // Calculate overdue information
        if (builty.getPaymentDueDate() != null && !"PAID".equals(builty.getPaymentStatus())) {
            LocalDate now = LocalDate.now();
            if (now.isAfter(builty.getPaymentDueDate())) {
                dto.setIsOverdue(true);
                dto.setDaysOverdue((int) ChronoUnit.DAYS.between(builty.getPaymentDueDate(), now));
            } else {
                dto.setIsOverdue(false);
                dto.setDaysOverdue(0);
            }
        }
        
        return dto;
    }
    
    @Override
    public Builty convertToEntity(BuiltyDTO builtyDTO) {
        if (builtyDTO == null) {
            return null;
        }
        
        Builty builty = new Builty();
        builty.setId(builtyDTO.getId());
        builty.setBuiltyNumber(builtyDTO.getBuiltyNumber());
        builty.setConsignorName(builtyDTO.getConsignorName());
        builty.setConsignorAddress(builtyDTO.getConsignorAddress());
        builty.setConsignorPhone(builtyDTO.getConsignorPhone());
        builty.setConsigneeName(builtyDTO.getConsigneeName());
        builty.setConsigneeAddress(builtyDTO.getConsigneeAddress());
        builty.setConsigneePhone(builtyDTO.getConsigneePhone());
        builty.setGoodsDescription(builtyDTO.getGoodsDescription());
        builty.setGoodsWeight(builtyDTO.getGoodsWeight());
        builty.setGoodsValue(builtyDTO.getGoodsValue());
        builty.setNumberOfPackages(builtyDTO.getNumberOfPackages());
        builty.setPackageType(builtyDTO.getPackageType());
        builty.setFreightCharges(builtyDTO.getFreightCharges());
        builty.setLoadingCharges(builtyDTO.getLoadingCharges());
        builty.setUnloadingCharges(builtyDTO.getUnloadingCharges());
        builty.setOtherCharges(builtyDTO.getOtherCharges());
        builty.setGstAmount(builtyDTO.getGstAmount());
        builty.setAdvanceAmount(builtyDTO.getAdvanceAmount());
        builty.setPaymentStatus(builtyDTO.getPaymentStatus());
        builty.setDeliveryStatus(builtyDTO.getDeliveryStatus());
        builty.setBuiltyDate(builtyDTO.getBuiltyDate());
        builty.setDeliveryDate(builtyDTO.getDeliveryDate());
        builty.setPaymentDueDate(builtyDTO.getPaymentDueDate());
        builty.setRemarks(builtyDTO.getRemarks());
        builty.setSpecialInstructions(builtyDTO.getSpecialInstructions());
        
        // Set related entities
        if (builtyDTO.getTripId() != null) {
            Trip trip = tripRepository.findById(builtyDTO.getTripId()).orElse(null);
            builty.setTrip(trip);
        }
        
        if (builtyDTO.getClientId() != null) {
            Client client = clientRepository.findById(builtyDTO.getClientId()).orElse(null);
            builty.setClient(client);
        }
        
        return builty;
    }
    
    @Override
    public String generateBuiltyNumber() {
        String prefix = "BL";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Find the last builty number for today
        String lastBuiltyNumber = builtyRepository.findLastBuiltyNumberForDate(datePart);
        
        int sequence = 1;
        if (lastBuiltyNumber != null && lastBuiltyNumber.length() > 10) {
            try {
                sequence = Integer.parseInt(lastBuiltyNumber.substring(10)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Could not parse sequence from builty number: {}", lastBuiltyNumber);
            }
        }
        
        return String.format("%s%s%04d", prefix, datePart, sequence);
    }
    
    @Override
    public LocalDate calculatePaymentDueDate(LocalDate builtyDate, int creditDays) {
        return builtyDate.plusDays(creditDays);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getBuiltyCount(String paymentStatus, String deliveryStatus) {
        return builtyRepository.countByPaymentStatusAndDeliveryStatus(paymentStatus, deliveryStatus);
    }
    
    @Override
    public boolean sendPaymentReminder(Long builtyId) {
        // Implementation for sending payment reminder
        // This would typically integrate with email/SMS service
        logger.info("Sending payment reminder for builty ID: {}", builtyId);
        return true; // Placeholder
    }
    
    @Override
    public String generateInvoicePDF(Long builtyId) {
        // Implementation for generating invoice PDF
        // This would typically use a PDF generation library
        logger.info("Generating invoice PDF for builty ID: {}", builtyId);
        return "/invoices/builty_" + builtyId + ".pdf"; // Placeholder
    }
    
    // ===============================================
    // PRIVATE HELPER METHODS
    // ===============================================
    
    private void updateBuiltyFields(Builty existingBuilty, BuiltyDTO builtyDTO) {
        existingBuilty.setBuiltyNumber(builtyDTO.getBuiltyNumber());
        existingBuilty.setConsignorName(builtyDTO.getConsignorName());
        existingBuilty.setConsignorAddress(builtyDTO.getConsignorAddress());
        existingBuilty.setConsignorPhone(builtyDTO.getConsignorPhone());
        existingBuilty.setConsigneeName(builtyDTO.getConsigneeName());
        existingBuilty.setConsigneeAddress(builtyDTO.getConsigneeAddress());
        existingBuilty.setConsigneePhone(builtyDTO.getConsigneePhone());
        existingBuilty.setGoodsDescription(builtyDTO.getGoodsDescription());
        existingBuilty.setGoodsWeight(builtyDTO.getGoodsWeight());
        existingBuilty.setGoodsValue(builtyDTO.getGoodsValue());
        existingBuilty.setNumberOfPackages(builtyDTO.getNumberOfPackages());
        existingBuilty.setPackageType(builtyDTO.getPackageType());
        existingBuilty.setFreightCharges(builtyDTO.getFreightCharges());
        existingBuilty.setLoadingCharges(builtyDTO.getLoadingCharges());
        existingBuilty.setUnloadingCharges(builtyDTO.getUnloadingCharges());
        existingBuilty.setOtherCharges(builtyDTO.getOtherCharges());
        existingBuilty.setSpecialInstructions(builtyDTO.getSpecialInstructions());
        
        // Update related entities if changed
        if (builtyDTO.getTripId() != null && 
            (existingBuilty.getTrip() == null || !existingBuilty.getTrip().getId().equals(builtyDTO.getTripId()))) {
            Trip trip = tripRepository.findById(builtyDTO.getTripId()).orElse(null);
            existingBuilty.setTrip(trip);
        }
        
        if (builtyDTO.getClientId() != null && 
            (existingBuilty.getClient() == null || !existingBuilty.getClient().getId().equals(builtyDTO.getClientId()))) {
            Client client = clientRepository.findById(builtyDTO.getClientId()).orElse(null);
            existingBuilty.setClient(client);
        }
    }
    
    private void addToRemarks(Builty builty, String newRemark) {
        String currentRemarks = builty.getRemarks() != null ? builty.getRemarks() : "";
        builty.setRemarks(currentRemarks + "\n" + newRemark);
    }
    
    private BigDecimal calculateTotalAmount(Builty builty) {
        BigDecimal total = builty.getFreightCharges() != null ? builty.getFreightCharges() : BigDecimal.ZERO;
        if (builty.getLoadingCharges() != null) total = total.add(builty.getLoadingCharges());
        if (builty.getUnloadingCharges() != null) total = total.add(builty.getUnloadingCharges());
        if (builty.getOtherCharges() != null) total = total.add(builty.getOtherCharges());
        if (builty.getGstAmount() != null) total = total.add(builty.getGstAmount());
        return total;
    }
}
