package com.shivshakti.stms.service;

import com.shivshakti.stms.dto.BuiltyDTO;
import com.shivshakti.stms.entity.Builty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Builty management
 * Defines business operations for builty-related functionality
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public interface BuiltyService {

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    /**
     * Create a new builty
     * @param builtyDTO Builty data transfer object
     * @return Created builty DTO
     */
    BuiltyDTO createBuilty(BuiltyDTO builtyDTO);
    
    /**
     * Update an existing builty
     * @param id Builty ID
     * @param builtyDTO Updated builty data
     * @return Updated builty DTO
     */
    BuiltyDTO updateBuilty(Long id, BuiltyDTO builtyDTO);
    
    /**
     * Get builty by ID
     * @param id Builty ID
     * @return Builty DTO if found
     */
    Optional<BuiltyDTO> getBuiltyById(Long id);
    
    /**
     * Get all builties with pagination
     * @param pageable Pagination information
     * @return Page of builty DTOs
     */
    Page<BuiltyDTO> getAllBuilties(Pageable pageable);
    
    /**
     * Delete a builty (only if not delivered)
     * @param id Builty ID
     */
    void deleteBuilty(Long id);

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    /**
     * Search builties by multiple criteria
     * @param builtyNumber Builty number (partial match)
     * @param tripId Trip ID
     * @param clientId Client ID
     * @param paymentStatus Payment status
     * @param deliveryStatus Delivery status
     * @param startDate Start date range
     * @param endDate End date range
     * @param pageable Pagination information
     * @return Page of matching builty DTOs
     */
    Page<BuiltyDTO> searchBuilties(String builtyNumber, Long tripId, Long clientId,
                                  String paymentStatus, String deliveryStatus,
                                  LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Find builty by builty number
     * @param builtyNumber Builty number
     * @return Builty DTO if found
     */
    Optional<BuiltyDTO> findByBuiltyNumber(String builtyNumber);
    
    /**
     * Get builties by payment status
     * @param paymentStatus Payment status
     * @param pageable Pagination information
     * @return Page of builties with specified payment status
     */
    Page<BuiltyDTO> getBuiltiesByPaymentStatus(String paymentStatus, Pageable pageable);
    
    /**
     * Get builties by delivery status
     * @param deliveryStatus Delivery status
     * @param pageable Pagination information
     * @return Page of builties with specified delivery status
     */
    Page<BuiltyDTO> getBuiltiesByDeliveryStatus(String deliveryStatus, Pageable pageable);
    
    /**
     * Get builties by trip
     * @param tripId Trip ID
     * @param pageable Pagination information
     * @return Page of builties for specified trip
     */
    Page<BuiltyDTO> getBuiltiesByTrip(Long tripId, Pageable pageable);
    
    /**
     * Get builties by client
     * @param clientId Client ID
     * @param pageable Pagination information
     * @return Page of builties for specified client
     */
    Page<BuiltyDTO> getBuiltiesByClient(Long clientId, Pageable pageable);

    // ===============================================
    // PAYMENT MANAGEMENT
    // ===============================================
    
    /**
     * Update payment status
     * @param id Builty ID
     * @param paymentStatus New payment status
     * @param remarks Payment remarks
     * @return Updated builty DTO
     */
    BuiltyDTO updatePaymentStatus(Long id, String paymentStatus, String remarks);
    
    /**
     * Add payment
     * @param id Builty ID
     * @param paymentAmount Payment amount
     * @param paymentDate Payment date
     * @param paymentMethod Payment method
     * @param remarks Payment remarks
     * @return Updated builty DTO
     */
    BuiltyDTO addPayment(Long id, BigDecimal paymentAmount, LocalDate paymentDate,
                        String paymentMethod, String remarks);
    
    /**
     * Get pending payments
     * @param pageable Pagination information
     * @return Page of builties with pending payments
     */
    Page<BuiltyDTO> getPendingPayments(Pageable pageable);
    
    /**
     * Get overdue payments
     * @param pageable Pagination information
     * @return Page of overdue builties
     */
    Page<BuiltyDTO> getOverduePayments(Pageable pageable);
    
    /**
     * Get partial payments
     * @param pageable Pagination information
     * @return Page of builties with partial payments
     */
    Page<BuiltyDTO> getPartialPayments(Pageable pageable);
    
    /**
     * Calculate total outstanding amount
     * @param clientId Client ID (optional)
     * @return Total outstanding amount
     */
    BigDecimal calculateOutstandingAmount(Long clientId);
    
    /**
     * Get payment summary for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Payment summary data
     */
    List<Object[]> getPaymentSummary(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // DELIVERY MANAGEMENT
    // ===============================================
    
    /**
     * Update delivery status
     * @param id Builty ID
     * @param deliveryStatus New delivery status
     * @param deliveryDate Delivery date
     * @param remarks Delivery remarks
     * @return Updated builty DTO
     */
    BuiltyDTO updateDeliveryStatus(Long id, String deliveryStatus, LocalDate deliveryDate, String remarks);
    
    /**
     * Mark as delivered
     * @param id Builty ID
     * @param deliveryDate Delivery date
     * @param receivedBy Person who received the goods
     * @param remarks Delivery remarks
     * @return Updated builty DTO
     */
    BuiltyDTO markAsDelivered(Long id, LocalDate deliveryDate, String receivedBy, String remarks);
    
    /**
     * Get pending deliveries
     * @param pageable Pagination information
     * @return Page of builties with pending deliveries
     */
    Page<BuiltyDTO> getPendingDeliveries(Pageable pageable);
    
    /**
     * Get in-transit builties
     * @param pageable Pagination information
     * @return Page of in-transit builties
     */
    Page<BuiltyDTO> getInTransitBuilties(Pageable pageable);
    
    /**
     * Get delivered builties
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination information
     * @return Page of delivered builties
     */
    Page<BuiltyDTO> getDeliveredBuilties(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Get delivery performance report
     * @param startDate Start date
     * @param endDate End date
     * @return Delivery performance data
     */
    List<Object[]> getDeliveryPerformanceReport(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // FINANCIAL OPERATIONS
    // ===============================================
    
    /**
     * Calculate GST amount
     * @param totalCharges Total charges
     * @param gstRate GST rate percentage
     * @return GST amount
     */
    BigDecimal calculateGSTAmount(BigDecimal totalCharges, BigDecimal gstRate);
    
    /**
     * Update freight charges
     * @param id Builty ID
     * @param newCharges New freight charges
     * @param reason Reason for change
     * @return Updated builty DTO
     */
    BuiltyDTO updateFreightCharges(Long id, BigDecimal newCharges, String reason);
    
    /**
     * Add additional charges
     * @param id Builty ID
     * @param chargeType Charge type (LOADING, UNLOADING, OTHER)
     * @param amount Charge amount
     * @param description Charge description
     * @return Updated builty DTO
     */
    BuiltyDTO addAdditionalCharges(Long id, String chargeType, BigDecimal amount, String description);
    
    /**
     * Get revenue report
     * @param startDate Start date
     * @param endDate End date
     * @return Revenue report data
     */
    List<Object[]> getRevenueReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get client-wise revenue
     * @param startDate Start date
     * @param endDate End date
     * @return Client-wise revenue data
     */
    List<Object[]> getClientWiseRevenue(LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate total revenue for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue
     */
    BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // REPORTING AND ANALYTICS
    // ===============================================
    
    /**
     * Get builty statistics
     * @return Builty statistics array
     */
    Object[] getBuiltyStatistics();
    
    /**
     * Get monthly builty summary
     * @param startDate Start date
     * @param endDate End date
     * @return Monthly summary data
     */
    List<Object[]> getMonthlyBuiltySummary(LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate builty report
     * @param startDate Start date
     * @param endDate End date
     * @param paymentStatus Payment status filter
     * @param deliveryStatus Delivery status filter
     * @return Builty report data
     */
    List<BuiltyDTO> generateBuiltyReport(LocalDate startDate, LocalDate endDate,
                                        String paymentStatus, String deliveryStatus);
    
    /**
     * Get aging report
     * @return Aging report data (30, 60, 90+ days)
     */
    List<Object[]> getAgingReport();
    
    /**
     * Get top clients by revenue
     * @param startDate Start date
     * @param endDate End date
     * @param limit Number of top clients
     * @return Top clients data
     */
    List<Object[]> getTopClientsByRevenue(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Get goods analysis report
     * @param startDate Start date
     * @param endDate End date
     * @return Goods analysis data
     */
    List<Object[]> getGoodsAnalysisReport(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    /**
     * Validate builty data for creation
     * @param builtyDTO Builty data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateBuiltyForCreation(BuiltyDTO builtyDTO);
    
    /**
     * Validate builty data for update
     * @param id Builty ID
     * @param builtyDTO Builty data to validate
     * @throws BusinessValidationException if validation fails
     */
    void validateBuiltyForUpdate(Long id, BuiltyDTO builtyDTO);
    
    /**
     * Check if builty number is unique
     * @param builtyNumber Builty number to check
     * @param excludeId ID to exclude from check (for updates)
     * @return true if unique, false otherwise
     */
    boolean isBuiltyNumberUnique(String builtyNumber, Long excludeId);
    
    /**
     * Validate payment amount
     * @param builtyId Builty ID
     * @param paymentAmount Payment amount
     * @return true if valid, false otherwise
     */
    boolean validatePaymentAmount(Long builtyId, BigDecimal paymentAmount);
    
    /**
     * Check if builty can be deleted
     * @param id Builty ID
     * @return true if can be deleted, false otherwise
     */
    boolean canDeleteBuilty(Long id);

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    /**
     * Convert Builty entity to DTO
     * @param builty Builty entity
     * @return Builty DTO
     */
    BuiltyDTO convertToDTO(Builty builty);
    
    /**
     * Convert BuiltyDTO to entity
     * @param builtyDTO Builty DTO
     * @return Builty entity
     */
    Builty convertToEntity(BuiltyDTO builtyDTO);
    
    /**
     * Generate unique builty number
     * @return Unique builty number
     */
    String generateBuiltyNumber();
    
    /**
     * Calculate payment due date
     * @param builtyDate Builty date
     * @param creditDays Credit days
     * @return Payment due date
     */
    LocalDate calculatePaymentDueDate(LocalDate builtyDate, int creditDays);
    
    /**
     * Get builty count by status
     * @param paymentStatus Payment status
     * @param deliveryStatus Delivery status
     * @return Count of builties
     */
    long getBuiltyCount(String paymentStatus, String deliveryStatus);
    
    /**
     * Send payment reminder
     * @param builtyId Builty ID
     * @return true if reminder sent successfully
     */
    boolean sendPaymentReminder(Long builtyId);
    
    /**
     * Generate invoice PDF
     * @param builtyId Builty ID
     * @return PDF file path
     */
    String generateInvoicePDF(Long builtyId);
}
