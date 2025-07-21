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
 * Provides data access operations for client management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Basic queries
    Optional<Client> findByClientNumber(String clientNumber);
    Optional<Client> findByGstNumber(String gstNumber);
    Optional<Client> findByPanNumber(String panNumber);
    Page<Client> findByIsActive(Boolean isActive, Pageable pageable);
    Page<Client> findByClientType(String clientType, Pageable pageable);
    
    // Search queries
    @Query("SELECT c FROM Client c WHERE " +
           "(:clientNumber IS NULL OR c.clientNumber LIKE %:clientNumber%) AND " +
           "(:name IS NULL OR c.name LIKE %:name%) AND " +
           "(:companyName IS NULL OR c.companyName LIKE %:companyName%) AND " +
           "(:phone IS NULL OR c.phone LIKE %:phone%) AND " +
           "(:clientType IS NULL OR c.clientType = :clientType) AND " +
           "(:isActive IS NULL OR c.isActive = :isActive)")
    Page<Client> searchClients(@Param("clientNumber") String clientNumber,
                              @Param("name") String name,
                              @Param("companyName") String companyName,
                              @Param("phone") String phone,
                              @Param("clientType") String clientType,
                              @Param("isActive") Boolean isActive,
                              Pageable pageable);
    
    // Credit management queries
    @Query("SELECT c FROM Client c WHERE c.outstandingAmount > :amount")
    Page<Client> findClientsWithOutstandingAmountGreaterThan(@Param("amount") BigDecimal amount, Pageable pageable);
    
    @Query("SELECT c FROM Client c WHERE c.outstandingAmount > c.creditLimit AND c.isActive = true")
    List<Client> findClientsExceedingCreditLimit();
    
    @Query("SELECT c FROM Client c WHERE c.outstandingAmount > 0 AND c.isActive = true")
    Page<Client> findClientsWithOutstandingAmount(Pageable pageable);
    
    // Financial queries
    @Query("SELECT c.id, c.name, c.outstandingAmount, c.creditLimit, " +
           "SUM(CASE WHEN i.paymentStatus = 'PENDING' THEN i.amount ELSE 0 END) " +
           "FROM Client c LEFT JOIN c.incomes i " +
           "WHERE c.isActive = true " +
           "GROUP BY c.id, c.name, c.outstandingAmount, c.creditLimit")
    List<Object[]> getClientFinancialSummary();
    
    @Query("SELECT c.id, c.name, COUNT(t), SUM(t.tripCharges), AVG(t.tripCharges) " +
           "FROM Client c LEFT JOIN c.trips t " +
           "WHERE t.actualStartDate BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED' " +
           "GROUP BY c.id, c.name")
    List<Object[]> getClientBusinessReport(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    // Statistics queries
    @Query("SELECT COUNT(c), " +
           "COUNT(CASE WHEN c.isActive = true THEN 1 END), " +
           "SUM(c.outstandingAmount), " +
           "AVG(c.creditLimit) " +
           "FROM Client c")
    Object[] getClientStatistics();
    
    // Utility queries
    boolean existsByClientNumberAndIdNot(String clientNumber, Long id);
    boolean existsByGstNumberAndIdNot(String gstNumber, Long id);
    boolean existsByPanNumberAndIdNot(String panNumber, Long id);
    
    @Query("SELECT c.clientNumber FROM Client c WHERE c.clientNumber LIKE :pattern ORDER BY c.clientNumber DESC")
    List<String> findLastClientNumberForDate(@Param("pattern") String pattern);
}

