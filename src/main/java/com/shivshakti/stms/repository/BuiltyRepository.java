package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Builty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Builty entity
 * Provides data access operations for builty management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface BuiltyRepository extends JpaRepository<Builty, Long> {

    // Basic queries
    Optional<Builty> findByBuiltyNumber(String builtyNumber);
    Page<Builty> findByTripId(Long tripId, Pageable pageable);
    Page<Builty> findByClientId(Long clientId, Pageable pageable);
    Page<Builty> findByPaymentStatus(String paymentStatus, Pageable pageable);
    Page<Builty> findByDeliveryStatus(String deliveryStatus, Pageable pageable);
    
    // Search queries
    @Query("SELECT b FROM Builty b WHERE " +
           "(:builtyNumber IS NULL OR b.builtyNumber LIKE %:builtyNumber%) AND " +
           "(:tripId IS NULL OR b.trip.id = :tripId) AND " +
           "(:clientId IS NULL OR b.client.id = :clientId) AND " +
           "(:consignorName IS NULL OR b.consignorName LIKE %:consignorName%) AND " +
           "(:consigneeName IS NULL OR b.consigneeName LIKE %:consigneeName%) AND " +
           "(:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus) AND " +
           "(:deliveryStatus IS NULL OR b.deliveryStatus = :deliveryStatus) AND " +
           "(:startDate IS NULL OR b.builtyDate >= :startDate) AND " +
           "(:endDate IS NULL OR b.builtyDate <= :endDate)")
    Page<Builty> searchBuilties(@Param("builtyNumber") String builtyNumber,
                               @Param("tripId") Long tripId,
                               @Param("clientId") Long clientId,
                               @Param("consignorName") String consignorName,
                               @Param("consigneeName") String consigneeName,
                               @Param("paymentStatus") String paymentStatus,
                               @Param("deliveryStatus") String deliveryStatus,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate,
                               Pageable pageable);
    
    // Payment queries
    @Query("SELECT b FROM Builty b WHERE b.paymentStatus = 'PENDING' AND b.paymentDueDate <= :date")
    Page<Builty> findOverduePayments(@Param("date") LocalDate date, Pageable pageable);
    
    @Query("SELECT SUM(b.freightCharges + b.loadingCharges + b.unloadingCharges + b.otherCharges + b.gstAmount - b.advanceAmount) " +
           "FROM Builty b WHERE b.paymentStatus = 'PENDING'")
    BigDecimal calculateTotalPendingAmount();
    
    @Query("SELECT SUM(b.freightCharges + b.loadingCharges + b.unloadingCharges + b.otherCharges + b.gstAmount) " +
           "FROM Builty b WHERE b.paymentStatus = 'PAID' AND b.builtyDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalReceivedAmount(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    // Delivery queries
    @Query("SELECT b FROM Builty b WHERE b.deliveryStatus = 'PENDING'")
    Page<Builty> findPendingDeliveries(Pageable pageable);
    
    @Query("SELECT b FROM Builty b WHERE b.deliveryStatus = 'DELIVERED' AND b.deliveryDate BETWEEN :startDate AND :endDate")
    Page<Builty> findDeliveriesBetween(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      Pageable pageable);
    
    // Financial reports
    @Query("SELECT b.paymentStatus, COUNT(b), SUM(b.freightCharges + b.loadingCharges + b.unloadingCharges + b.otherCharges + b.gstAmount) " +
           "FROM Builty b WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY b.paymentStatus")
    List<Object[]> getPaymentSummary(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c.name, COUNT(b), SUM(b.freightCharges + b.loadingCharges + b.unloadingCharges + b.otherCharges + b.gstAmount) " +
           "FROM Builty b JOIN b.client c WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c.id, c.name")
    List<Object[]> getClientWiseBuiltyReport(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT DATE(b.builtyDate), COUNT(b), SUM(b.freightCharges + b.loadingCharges + b.unloadingCharges + b.otherCharges + b.gstAmount) " +
           "FROM Builty b WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(b.builtyDate) ORDER BY DATE(b.builtyDate)")
    List<Object[]> getDailyBuiltyReport(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT MONTH(b.builtyDate), YEAR(b.builtyDate), COUNT(b), SUM(b.freightCharges + b.loadingCharges + b.unloadingCharges + b.otherCharges + b.gstAmount) " +
           "FROM Builty b WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(b.builtyDate), MONTH(b.builtyDate) " +
           "ORDER BY YEAR(b.builtyDate), MONTH(b.builtyDate)")
    List<Object[]> getMonthlyBuiltyReport(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    // Statistics queries
    @Query("SELECT COUNT(b), " +
           "COUNT(CASE WHEN b.paymentStatus = 'PENDING' THEN 1 END), " +
           "COUNT(CASE WHEN b.paymentStatus = 'PAID' THEN 1 END), " +
           "COUNT(CASE WHEN b.deliveryStatus = 'PENDING' THEN 1 END), " +
           "COUNT(CASE WHEN b.deliveryStatus = 'DELIVERED' THEN 1 END) " +
           "FROM Builty b")
    Object[] getBuiltyStatistics();
    
    // Utility queries
    boolean existsByBuiltyNumberAndIdNot(String builtyNumber, Long id);
    
    @Query("SELECT b.builtyNumber FROM Builty b WHERE b.builtyNumber LIKE :pattern ORDER BY b.builtyNumber DESC")
    List<String> findLastBuiltyNumberForDate(@Param("pattern") String pattern);
}

