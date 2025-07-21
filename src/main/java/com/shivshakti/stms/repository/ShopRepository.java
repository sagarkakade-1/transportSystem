package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.Shop;
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
 * Repository interface for Shop entity
 * Provides CRUD operations and custom queries for shop management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find shop by name
     */
    Optional<Shop> findByNameIgnoreCase(String name);
    
    /**
     * Find all active shops
     */
    List<Shop> findByIsActiveTrue();
    
    /**
     * Find shops by type
     */
    List<Shop> findByShopType(Shop.ShopType shopType);
    
    /**
     * Find shops by name containing (case insensitive)
     */
    List<Shop> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find shops by contact number
     */
    Optional<Shop> findByContactNumber(String contactNumber);
    
    /**
     * Find shops by GST number
     */
    Optional<Shop> findByGstNumber(String gstNumber);

    // ===============================================
    // SHOP TYPE QUERIES
    // ===============================================
    
    /**
     * Find fuel stations
     */
    @Query("SELECT s FROM Shop s WHERE s.shopType = 'FUEL' AND s.isActive = true")
    List<Shop> findFuelStations();
    
    /**
     * Find repair shops
     */
    @Query("SELECT s FROM Shop s WHERE s.shopType = 'REPAIR' AND s.isActive = true")
    List<Shop> findRepairShops();
    
    /**
     * Find tyre shops
     */
    @Query("SELECT s FROM Shop s WHERE s.shopType = 'TYRE' AND s.isActive = true")
    List<Shop> findTyreShops();
    
    /**
     * Find general service shops
     */
    @Query("SELECT s FROM Shop s WHERE s.shopType = 'GENERAL' AND s.isActive = true")
    List<Shop> findGeneralServiceShops();
    
    /**
     * Get shop count by type
     */
    @Query("SELECT s.shopType, COUNT(s) FROM Shop s WHERE s.isActive = true GROUP BY s.shopType")
    List<Object[]> getShopCountByType();

    // ===============================================
    // BUSINESS RELATIONSHIP QUERIES
    // ===============================================
    
    /**
     * Find shops with expenses
     */
    @Query("SELECT DISTINCT s FROM Shop s JOIN s.expenses e WHERE s.isActive = true")
    List<Shop> findShopsWithExpenses();
    
    /**
     * Find shops with maintenance records
     */
    @Query("SELECT DISTINCT s FROM Shop s JOIN s.maintenanceRecords m WHERE s.isActive = true")
    List<Shop> findShopsWithMaintenanceRecords();
    
    /**
     * Find preferred shops by transaction volume
     */
    @Query("SELECT s, COUNT(e) as transactionCount, COALESCE(SUM(e.amount), 0) as totalAmount " +
           "FROM Shop s LEFT JOIN s.expenses e " +
           "WHERE s.isActive = true " +
           "GROUP BY s " +
           "ORDER BY transactionCount DESC, totalAmount DESC")
    List<Object[]> findPreferredShopsByVolume();
    
    /**
     * Get shop business summary for date range
     */
    @Query("SELECT s, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM Shop s LEFT JOIN s.expenses e " +
           "WHERE e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getShopBusinessSummary(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // ===============================================
    // EXPENSE ANALYSIS QUERIES
    // ===============================================
    
    /**
     * Find shops with high expense volume
     */
    @Query("SELECT s, COALESCE(SUM(e.amount), 0) as totalExpense " +
           "FROM Shop s LEFT JOIN s.expenses e " +
           "WHERE e.expenseDate >= :startDate AND s.isActive = true " +
           "GROUP BY s " +
           "HAVING SUM(e.amount) > :threshold " +
           "ORDER BY totalExpense DESC")
    List<Object[]> findShopsWithHighExpenseVolume(@Param("startDate") LocalDate startDate,
                                                 @Param("threshold") BigDecimal threshold);
    
    /**
     * Get shop expense breakdown by category
     */
    @Query("SELECT s, e.category.categoryName, COUNT(e), COALESCE(SUM(e.amount), 0) " +
           "FROM Shop s JOIN s.expenses e " +
           "WHERE s.id = :shopId AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s, e.category.categoryName " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getShopExpenseBreakdown(@Param("shopId") Long shopId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * Find most expensive shops by average transaction value
     */
    @Query("SELECT s, COUNT(e), COALESCE(AVG(e.amount), 0) as avgTransaction " +
           "FROM Shop s JOIN s.expenses e " +
           "WHERE s.isActive = true " +
           "GROUP BY s " +
           "HAVING COUNT(e) >= :minTransactions " +
           "ORDER BY avgTransaction DESC")
    Page<Object[]> findMostExpensiveShops(@Param("minTransactions") long minTransactions,
                                         Pageable pageable);

    // ===============================================
    // MAINTENANCE ANALYSIS QUERIES
    // ===============================================
    
    /**
     * Get shop maintenance summary
     */
    @Query("SELECT s, COUNT(m), COALESCE(SUM(m.totalCost), 0) " +
           "FROM Shop s LEFT JOIN s.maintenanceRecords m " +
           "WHERE m.maintenanceDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s " +
           "ORDER BY SUM(m.totalCost) DESC")
    List<Object[]> getShopMaintenanceSummary(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    /**
     * Find shops by maintenance type
     */
    @Query("SELECT DISTINCT s FROM Shop s JOIN s.maintenanceRecords m " +
           "WHERE m.maintenanceType = :maintenanceType AND s.isActive = true")
    List<Shop> findShopsByMaintenanceType(@Param("maintenanceType") String maintenanceType);

    // ===============================================
    // TYRE MANAGEMENT QUERIES
    // ===============================================
    
    /**
     * Get shop tyre purchase summary
     */
    @Query("SELECT s, COUNT(t), COALESCE(SUM(t.purchasePrice), 0) " +
           "FROM Shop s LEFT JOIN s.tyreDetails t " +
           "WHERE t.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s " +
           "ORDER BY SUM(t.purchasePrice) DESC")
    List<Object[]> getShopTyrePurchaseSummary(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * Find tyre shops by company preference
     */
    @Query("SELECT s, t.tyreCompany, COUNT(t), COALESCE(SUM(t.purchasePrice), 0) " +
           "FROM Shop s JOIN s.tyreDetails t " +
           "WHERE s.shopType = 'TYRE' AND s.isActive = true " +
           "GROUP BY s, t.tyreCompany " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> findTyreShopsByCompanyPreference();

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search shops by multiple criteria
     */
    @Query("SELECT s FROM Shop s WHERE " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:shopType IS NULL OR s.shopType = :shopType) AND " +
           "(:contactPerson IS NULL OR LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :contactPerson, '%'))) AND " +
           "(:contactNumber IS NULL OR s.contactNumber = :contactNumber) AND " +
           "(:gstNumber IS NULL OR s.gstNumber = :gstNumber) AND " +
           "(:isActive IS NULL OR s.isActive = :isActive)")
    Page<Shop> searchShops(@Param("name") String name,
                          @Param("shopType") Shop.ShopType shopType,
                          @Param("contactPerson") String contactPerson,
                          @Param("contactNumber") String contactNumber,
                          @Param("gstNumber") String gstNumber,
                          @Param("isActive") Boolean isActive,
                          Pageable pageable);
    
    /**
     * Find shops by address containing
     */
    List<Shop> findByAddressContainingIgnoreCase(String address);
    
    /**
     * Find shops by contact person
     */
    List<Shop> findByContactPersonContainingIgnoreCase(String contactPerson);

    // ===============================================
    // PERFORMANCE ANALYSIS
    // ===============================================
    
    /**
     * Get shop performance metrics
     */
    @Query("SELECT s, " +
           "COUNT(DISTINCT e) as expenseCount, " +
           "COUNT(DISTINCT m) as maintenanceCount, " +
           "COUNT(DISTINCT t) as tyreCount, " +
           "COALESCE(SUM(e.amount), 0) as totalExpenseAmount, " +
           "COALESCE(SUM(m.totalCost), 0) as totalMaintenanceAmount, " +
           "COALESCE(SUM(t.purchasePrice), 0) as totalTyreAmount " +
           "FROM Shop s " +
           "LEFT JOIN s.expenses e " +
           "LEFT JOIN s.maintenanceRecords m " +
           "LEFT JOIN s.tyreDetails t " +
           "WHERE s.isActive = true " +
           "GROUP BY s " +
           "ORDER BY (COALESCE(SUM(e.amount), 0) + COALESCE(SUM(m.totalCost), 0) + COALESCE(SUM(t.purchasePrice), 0)) DESC")
    List<Object[]> getShopPerformanceMetrics();
    
    /**
     * Find most reliable shops (by transaction frequency)
     */
    @Query("SELECT s, COUNT(e) as transactionCount " +
           "FROM Shop s JOIN s.expenses e " +
           "WHERE s.isActive = true " +
           "GROUP BY s " +
           "ORDER BY transactionCount DESC")
    Page<Object[]> findMostReliableShops(Pageable pageable);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get shop statistics
     */
    @Query("SELECT " +
           "COUNT(s) as totalShops, " +
           "COUNT(CASE WHEN s.isActive = true THEN 1 END) as activeShops, " +
           "COUNT(CASE WHEN s.shopType = 'FUEL' THEN 1 END) as fuelStations, " +
           "COUNT(CASE WHEN s.shopType = 'REPAIR' THEN 1 END) as repairShops, " +
           "COUNT(CASE WHEN s.shopType = 'TYRE' THEN 1 END) as tyreShops, " +
           "COUNT(CASE WHEN s.shopType = 'GENERAL' THEN 1 END) as generalShops " +
           "FROM Shop s")
    Object[] getShopStatistics();
    
    /**
     * Get monthly shop summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM s.createdDate) as year, " +
           "EXTRACT(MONTH FROM s.createdDate) as month, " +
           "COUNT(s) as shopCount, " +
           "COUNT(CASE WHEN s.isActive = true THEN 1 END) as activeCount " +
           "FROM Shop s " +
           "WHERE s.createdDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM s.createdDate), EXTRACT(MONTH FROM s.createdDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyShopSummary(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    /**
     * Get shop business volume ranking
     */
    @Query("SELECT s, " +
           "(COALESCE(SUM(e.amount), 0) + COALESCE(SUM(m.totalCost), 0) + COALESCE(SUM(t.purchasePrice), 0)) as totalBusiness " +
           "FROM Shop s " +
           "LEFT JOIN s.expenses e " +
           "LEFT JOIN s.maintenanceRecords m " +
           "LEFT JOIN s.tyreDetails t " +
           "WHERE s.isActive = true " +
           "GROUP BY s " +
           "ORDER BY totalBusiness DESC")
    List<Object[]> getShopBusinessVolumeRanking();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if shop name exists for different shop
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    
    /**
     * Check if contact number exists for different shop
     */
    boolean existsByContactNumberAndIdNot(String contactNumber, Long id);
    
    /**
     * Check if GST number exists for different shop
     */
    boolean existsByGstNumberAndIdNot(String gstNumber, Long id);
    
    /**
     * Count active shops
     */
    long countByIsActiveTrue();
    
    /**
     * Count shops by type
     */
    long countByShopType(Shop.ShopType shopType);
    
    /**
     * Count shops with business relationships
     */
    @Query("SELECT COUNT(DISTINCT s) FROM Shop s WHERE s.isActive = true AND " +
           "(EXISTS (SELECT 1 FROM s.expenses) OR EXISTS (SELECT 1 FROM s.maintenanceRecords) OR EXISTS (SELECT 1 FROM s.tyreDetails))")
    long countShopsWithBusinessRelationships();
}

