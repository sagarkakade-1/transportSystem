package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Client;
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
 * Repository interface for Client entity
 * Provides CRUD operations and custom queries for client management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find client by contact number
     */
    Optional<Client> findByContactNumber(String contactNumber);
    
    /**
     * Find client by GST number
     */
    Optional<Client> findByGstNumber(String gstNumber);
    
    /**
     * Find client by PAN number
     */
    Optional<Client> findByPanNumber(String panNumber);
    
    /**
     * Find all active clients
     */
    List<Client> findByIsActiveTrue();
    
    /**
     * Find clients by name containing (case insensitive)
     */
    List<Client> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find clients by company name containing (case insensitive)
     */
    List<Client> findByCompanyNameContainingIgnoreCase(String companyName);

    // ===============================================
    // CREDIT LIMIT AND OUTSTANDING QUERIES
    // ===============================================
    
    /**
     * Find clients with outstanding balance
     */
    @Query("SELECT c FROM Client c WHERE c.outstandingBalance > 0 AND c.isActive = true")
    List<Client> findClientsWithOutstandingBalance();
    
    /**
     * Find clients who exceeded credit limit
     */
    @Query("SELECT c FROM Client c WHERE c.outstandingBalance > c.creditLimit AND c.creditLimit > 0 AND c.isActive = true")
    List<Client> findClientsExceedingCreditLimit();
    
    /**
     * Find clients with outstanding balance greater than amount
     */
    List<Client> findByOutstandingBalanceGreaterThan(BigDecimal amount);
    
    /**
     * Find clients with credit limit greater than amount
     */
    List<Client> findByCreditLimitGreaterThan(BigDecimal amount);
    
    /**
     * Calculate total outstanding balance for all clients
     */
    @Query("SELECT COALESCE(SUM(c.outstandingBalance), 0) FROM Client c WHERE c.isActive = true")
    BigDecimal calculateTotalOutstandingBalance();
    
    /**
     * Calculate total credit limit for all clients
     */
    @Query("SELECT COALESCE(SUM(c.creditLimit), 0) FROM Client c WHERE c.isActive = true")
    BigDecimal calculateTotalCreditLimit();

    // ===============================================
    // BUSINESS RELATIONSHIP QUERIES
    // ===============================================
    
    /**
     * Find clients with active builties
     */
    @Query("SELECT DISTINCT c FROM Client c JOIN c.builties b WHERE b.paymentStatus != 'PAID' AND c.isActive = true")
    List<Client> findClientsWithPendingBuilties();
    
    /**
     * Find top clients by total business value
     */
    @Query("SELECT c, COALESCE(SUM(b.totalCharges), 0) as totalBusiness FROM Client c " +
           "LEFT JOIN c.builties b " +
           "WHERE c.isActive = true " +
           "GROUP BY c " +
           "ORDER BY totalBusiness DESC")
    Page<Object[]> findTopClientsByBusinessValue(Pageable pageable);
    
    /**
     * Get client business summary for date range
     */
    @Query("SELECT c, COUNT(b), COALESCE(SUM(b.totalCharges), 0), COALESCE(SUM(b.balanceAmount), 0) " +
           "FROM Client c LEFT JOIN c.builties b " +
           "WHERE b.builtyDate BETWEEN :startDate AND :endDate " +
           "GROUP BY c " +
           "ORDER BY SUM(b.totalCharges) DESC")
    List<Object[]> getClientBusinessSummary(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    // ===============================================
    // PAYMENT ANALYSIS QUERIES
    // ===============================================
    
    /**
     * Find clients with good payment history (low outstanding ratio)
     */
    @Query("SELECT c FROM Client c WHERE " +
           "c.creditLimit > 0 AND " +
           "(c.outstandingBalance / c.creditLimit) < :ratio AND " +
           "c.isActive = true")
    List<Client> findClientsWithGoodPaymentHistory(@Param("ratio") BigDecimal ratio);
    
    /**
     * Get client payment statistics
     */
    @Query("SELECT c.id, c.name, " +
           "COUNT(p) as totalPayments, " +
           "COALESCE(SUM(p.amount), 0) as totalPaid, " +
           "c.outstandingBalance, " +
           "c.creditLimit " +
           "FROM Client c LEFT JOIN c.payments p " +
           "WHERE c.isActive = true " +
           "GROUP BY c.id, c.name, c.outstandingBalance, c.creditLimit " +
           "ORDER BY totalPaid DESC")
    List<Object[]> getClientPaymentStatistics();

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search clients by multiple criteria
     */
    @Query("SELECT c FROM Client c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:companyName IS NULL OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND " +
           "(:contactNumber IS NULL OR c.contactNumber = :contactNumber) AND " +
           "(:gstNumber IS NULL OR c.gstNumber = :gstNumber) AND " +
           "(:isActive IS NULL OR c.isActive = :isActive)")
    Page<Client> searchClients(@Param("name") String name,
                              @Param("companyName") String companyName,
                              @Param("contactNumber") String contactNumber,
                              @Param("gstNumber") String gstNumber,
                              @Param("isActive") Boolean isActive,
                              Pageable pageable);
    
    /**
     * Find clients by outstanding balance range
     */
    List<Client> findByOutstandingBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance);
    
    /**
     * Find clients by credit limit range
     */
    List<Client> findByCreditLimitBetween(BigDecimal minLimit, BigDecimal maxLimit);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly client summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM c.createdDate) as year, " +
           "EXTRACT(MONTH FROM c.createdDate) as month, " +
           "COUNT(c) as clientCount, " +
           "COUNT(CASE WHEN c.isActive = true THEN 1 END) as activeCount, " +
           "COALESCE(SUM(c.creditLimit), 0) as totalCreditLimit, " +
           "COALESCE(SUM(c.outstandingBalance), 0) as totalOutstanding " +
           "FROM Client c " +
           "WHERE c.createdDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM c.createdDate), EXTRACT(MONTH FROM c.createdDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyClientSummary(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * Get client statistics
     */
    @Query("SELECT " +
           "COUNT(c) as totalClients, " +
           "COUNT(CASE WHEN c.isActive = true THEN 1 END) as activeClients, " +
           "COUNT(CASE WHEN c.outstandingBalance > 0 THEN 1 END) as clientsWithOutstanding, " +
           "COUNT(CASE WHEN c.outstandingBalance > c.creditLimit AND c.creditLimit > 0 THEN 1 END) as clientsExceedingLimit, " +
           "COALESCE(AVG(c.creditLimit), 0) as averageCreditLimit, " +
           "COALESCE(SUM(c.outstandingBalance), 0) as totalOutstanding " +
           "FROM Client c")
    Object[] getClientStatistics();
    
    /**
     * Get client aging report
     */
    @Query("SELECT c, " +
           "COALESCE(SUM(CASE WHEN b.builtyDate >= CURRENT_DATE - 30 THEN b.balanceAmount ELSE 0 END), 0) as current, " +
           "COALESCE(SUM(CASE WHEN b.builtyDate BETWEEN CURRENT_DATE - 60 AND CURRENT_DATE - 31 THEN b.balanceAmount ELSE 0 END), 0) as days30, " +
           "COALESCE(SUM(CASE WHEN b.builtyDate BETWEEN CURRENT_DATE - 90 AND CURRENT_DATE - 61 THEN b.balanceAmount ELSE 0 END), 0) as days60, " +
           "COALESCE(SUM(CASE WHEN b.builtyDate < CURRENT_DATE - 90 THEN b.balanceAmount ELSE 0 END), 0) as days90Plus " +
           "FROM Client c LEFT JOIN c.builties b " +
           "WHERE c.isActive = true AND (b.paymentStatus != 'PAID' OR b IS NULL) " +
           "GROUP BY c " +
           "HAVING (current + days30 + days60 + days90Plus) > 0 " +
           "ORDER BY (current + days30 + days60 + days90Plus) DESC")
    List<Object[]> getClientAgingReport();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if contact number exists for different client
     */
    boolean existsByContactNumberAndIdNot(String contactNumber, Long id);
    
    /**
     * Check if GST number exists for different client
     */
    boolean existsByGstNumberAndIdNot(String gstNumber, Long id);
    
    /**
     * Check if PAN number exists for different client
     */
    boolean existsByPanNumberAndIdNot(String panNumber, Long id);
    
    /**
     * Count active clients
     */
    long countByIsActiveTrue();
    
    /**
     * Count clients with outstanding balance
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.outstandingBalance > 0 AND c.isActive = true")
    long countClientsWithOutstandingBalance();
    
    /**
     * Count clients exceeding credit limit
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.outstandingBalance > c.creditLimit AND c.creditLimit > 0 AND c.isActive = true")
    long countClientsExceedingCreditLimit();
}

