package com.shivshakti.stms.repository;

import com.shivshakti.stms.entity.TyreDetail;
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
 * Repository interface for TyreDetail entity
 * Provides CRUD operations and custom queries for tyre detail management
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Repository
public interface TyreDetailRepository extends JpaRepository<TyreDetail, Long> {

    // ===============================================
    // BASIC FINDER METHODS
    // ===============================================
    
    /**
     * Find tyre details by truck
     */
    List<TyreDetail> findByTruckId(Long truckId);
    
    /**
     * Find tyre details by maintenance
     */
    List<TyreDetail> findByMaintenanceId(Long maintenanceId);
    
    /**
     * Find tyre details by shop
     */
    List<TyreDetail> findByShopId(Long shopId);
    
    /**
     * Find active tyre details
     */
    List<TyreDetail> findByIsActiveTrue();
    
    /**
     * Find tyre details by company
     */
    List<TyreDetail> findByTyreCompanyIgnoreCase(String tyreCompany);
    
    /**
     * Find tyre details by size
     */
    List<TyreDetail> findByTyreSize(String tyreSize);
    
    /**
     * Find tyre details by position
     */
    List<TyreDetail> findByTyrePosition(TyreDetail.TyrePosition tyrePosition);
    
    /**
     * Find tyre details by purchase date range
     */
    List<TyreDetail> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);

    // ===============================================
    // TRUCK-WISE ANALYSIS
    // ===============================================
    
    /**
     * Find active tyres for truck
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.truck.id = :truckId AND td.isActive = true ORDER BY td.tyrePosition")
    List<TyreDetail> findActiveTyresForTruck(@Param("truckId") Long truckId);
    
    /**
     * Get truck tyre summary
     */
    @Query("SELECT td.truck, COUNT(td), COALESCE(SUM(td.purchasePrice), 0) " +
           "FROM TyreDetail td " +
           "WHERE td.isActive = true " +
           "GROUP BY td.truck " +
           "ORDER BY SUM(td.purchasePrice) DESC")
    List<Object[]> getTruckTyreSummary();
    
    /**
     * Find trucks with highest tyre costs
     */
    @Query("SELECT td.truck, COALESCE(SUM(td.purchasePrice), 0) as totalCost " +
           "FROM TyreDetail td " +
           "WHERE td.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY td.truck " +
           "ORDER BY totalCost DESC")
    Page<Object[]> findTrucksWithHighestTyreCosts(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 Pageable pageable);
    
    /**
     * Get tyre position distribution for truck
     */
    @Query("SELECT td.tyrePosition, COUNT(td) " +
           "FROM TyreDetail td " +
           "WHERE td.truck.id = :truckId AND td.isActive = true " +
           "GROUP BY td.tyrePosition " +
           "ORDER BY td.tyrePosition")
    List<Object[]> getTyrePositionDistributionForTruck(@Param("truckId") Long truckId);

    // ===============================================
    // WARRANTY MANAGEMENT
    // ===============================================
    
    /**
     * Find tyres with active warranty
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.warrantyExpiryDate > CURRENT_DATE AND td.isActive = true ORDER BY td.warrantyExpiryDate ASC")
    List<TyreDetail> findTyresWithActiveWarranty();
    
    /**
     * Find tyres with expired warranty
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.warrantyExpiryDate < CURRENT_DATE AND td.isActive = true ORDER BY td.warrantyExpiryDate DESC")
    List<TyreDetail> findTyresWithExpiredWarranty();
    
    /**
     * Find tyres with warranty expiring soon
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.warrantyExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + :days AND td.isActive = true ORDER BY td.warrantyExpiryDate ASC")
    List<TyreDetail> findTyresWithWarrantyExpiringSoon(@Param("days") int days);
    
    /**
     * Find tyres with warranty KM exceeded
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.isActive = true AND td.warrantyKm > 0 AND " +
           "td.mileageAtInstallation IS NOT NULL AND td.truck.currentMileage IS NOT NULL AND " +
           "(td.truck.currentMileage - td.mileageAtInstallation) > td.warrantyKm")
    List<TyreDetail> findTyresWithWarrantyKmExceeded();
    
    /**
     * Get warranty summary
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN td.warrantyExpiryDate > CURRENT_DATE THEN 1 END) as activeWarranties, " +
           "COUNT(CASE WHEN td.warrantyExpiryDate < CURRENT_DATE THEN 1 END) as expiredWarranties, " +
           "COUNT(CASE WHEN td.warrantyExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + 30 THEN 1 END) as expiringSoon " +
           "FROM TyreDetail td " +
           "WHERE td.isActive = true AND td.warrantyExpiryDate IS NOT NULL")
    Object[] getWarrantySummary();

    // ===============================================
    // COMPANY AND BRAND ANALYSIS
    // ===============================================
    
    /**
     * Get tyre company distribution
     */
    @Query("SELECT td.tyreCompany, COUNT(td), COALESCE(SUM(td.purchasePrice), 0) " +
           "FROM TyreDetail td " +
           "WHERE td.isActive = true " +
           "GROUP BY td.tyreCompany " +
           "ORDER BY COUNT(td) DESC")
    List<Object[]> getTyreCompanyDistribution();
    
    /**
     * Find most popular tyre companies
     */
    @Query("SELECT td.tyreCompany, COUNT(td) as tyreCount " +
           "FROM TyreDetail td " +
           "WHERE td.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY td.tyreCompany " +
           "ORDER BY tyreCount DESC")
    Page<Object[]> findMostPopularTyreCompanies(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               Pageable pageable);
    
    /**
     * Get company-wise average price
     */
    @Query("SELECT td.tyreCompany, COUNT(td), COALESCE(AVG(td.purchasePrice), 0), COALESCE(SUM(td.purchasePrice), 0) " +
           "FROM TyreDetail td " +
           "WHERE td.isActive = true " +
           "GROUP BY td.tyreCompany " +
           "ORDER BY AVG(td.purchasePrice) DESC")
    List<Object[]> getCompanyWiseAveragePrice();

    // ===============================================
    // SIZE AND POSITION ANALYSIS
    // ===============================================
    
    /**
     * Get tyre size distribution
     */
    @Query("SELECT td.tyreSize, COUNT(td), COALESCE(AVG(td.purchasePrice), 0) " +
           "FROM TyreDetail td " +
           "WHERE td.isActive = true " +
           "GROUP BY td.tyreSize " +
           "ORDER BY COUNT(td) DESC")
    List<Object[]> getTyreSizeDistribution();
    
    /**
     * Get tyre position analysis
     */
    @Query("SELECT td.tyrePosition, COUNT(td), COALESCE(SUM(td.purchasePrice), 0) " +
           "FROM TyreDetail td " +
           "WHERE td.isActive = true " +
           "GROUP BY td.tyrePosition " +
           "ORDER BY td.tyrePosition")
    List<Object[]> getTyrePositionAnalysis();
    
    /**
     * Find front tyres
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.tyrePosition IN ('FRONT_LEFT', 'FRONT_RIGHT') AND td.isActive = true")
    List<TyreDetail> findFrontTyres();
    
    /**
     * Find rear tyres
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.tyrePosition IN ('REAR_LEFT_OUTER', 'REAR_LEFT_INNER', 'REAR_RIGHT_OUTER', 'REAR_RIGHT_INNER') AND td.isActive = true")
    List<TyreDetail> findRearTyres();
    
    /**
     * Find spare tyres
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.tyrePosition = 'SPARE' AND td.isActive = true")
    List<TyreDetail> findSpareTyres();

    // ===============================================
    // COST ANALYSIS
    // ===============================================
    
    /**
     * Calculate total tyre cost for period
     */
    @Query("SELECT COALESCE(SUM(td.purchasePrice), 0) FROM TyreDetail td WHERE td.purchaseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalTyreCostForPeriod(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total GST for period
     */
    @Query("SELECT COALESCE(SUM(td.gstAmount), 0) FROM TyreDetail td WHERE td.purchaseDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalGSTForPeriod(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * Find high cost tyres
     */
    @Query("SELECT td FROM TyreDetail td WHERE td.purchasePrice > :threshold ORDER BY td.purchasePrice DESC")
    List<TyreDetail> findHighCostTyres(@Param("threshold") BigDecimal threshold);
    
    /**
     * Get cost trend analysis
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM td.purchaseDate) as year, " +
           "EXTRACT(MONTH FROM td.purchaseDate) as month, " +
           "COUNT(td) as tyreCount, " +
           "COALESCE(SUM(td.purchasePrice), 0) as totalCost, " +
           "COALESCE(AVG(td.purchasePrice), 0) as averageCost " +
           "FROM TyreDetail td " +
           "WHERE td.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM td.purchaseDate), EXTRACT(MONTH FROM td.purchaseDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getCostTrendAnalysis(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // ===============================================
    // SHOP-WISE ANALYSIS
    // ===============================================
    
    /**
     * Get shop-wise tyre purchase summary
     */
    @Query("SELECT td.shop, COUNT(td), COALESCE(SUM(td.purchasePrice), 0) " +
           "FROM TyreDetail td " +
           "WHERE td.shop IS NOT NULL AND td.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY td.shop " +
           "ORDER BY SUM(td.purchasePrice) DESC")
    List<Object[]> getShopWiseTyrePurchaseSummary(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
    
    /**
     * Find preferred tyre shops
     */
    @Query("SELECT td.shop, COUNT(td) as tyreCount, COALESCE(SUM(td.purchasePrice), 0) as totalAmount " +
           "FROM TyreDetail td " +
           "WHERE td.shop IS NOT NULL " +
           "GROUP BY td.shop " +
           "ORDER BY tyreCount DESC, totalAmount DESC")
    List<Object[]> findPreferredTyreShops();

    // ===============================================
    // SEARCH AND FILTER QUERIES
    // ===============================================
    
    /**
     * Search tyre details by multiple criteria
     */
    @Query("SELECT td FROM TyreDetail td WHERE " +
           "(:truckId IS NULL OR td.truck.id = :truckId) AND " +
           "(:shopId IS NULL OR td.shop.id = :shopId) AND " +
           "(:tyreCompany IS NULL OR LOWER(td.tyreCompany) LIKE LOWER(CONCAT('%', :tyreCompany, '%'))) AND " +
           "(:tyreSize IS NULL OR td.tyreSize = :tyreSize) AND " +
           "(:tyrePosition IS NULL OR td.tyrePosition = :tyrePosition) AND " +
           "(:startDate IS NULL OR td.purchaseDate >= :startDate) AND " +
           "(:endDate IS NULL OR td.purchaseDate <= :endDate) AND " +
           "(:minPrice IS NULL OR td.purchasePrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR td.purchasePrice <= :maxPrice) AND " +
           "(:isActive IS NULL OR td.isActive = :isActive)")
    Page<TyreDetail> searchTyreDetails(@Param("truckId") Long truckId,
                                      @Param("shopId") Long shopId,
                                      @Param("tyreCompany") String tyreCompany,
                                      @Param("tyreSize") String tyreSize,
                                      @Param("tyrePosition") TyreDetail.TyrePosition tyrePosition,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("minPrice") BigDecimal minPrice,
                                      @Param("maxPrice") BigDecimal maxPrice,
                                      @Param("isActive") Boolean isActive,
                                      Pageable pageable);
    
    /**
     * Find tyres by price range
     */
    List<TyreDetail> findByPurchasePriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find tyres by invoice number
     */
    Optional<TyreDetail> findByInvoiceNumber(String invoiceNumber);

    // ===============================================
    // REPORTING QUERIES
    // ===============================================
    
    /**
     * Get monthly tyre summary
     */
    @Query("SELECT " +
           "EXTRACT(YEAR FROM td.purchaseDate) as year, " +
           "EXTRACT(MONTH FROM td.purchaseDate) as month, " +
           "COUNT(td) as tyreCount, " +
           "COALESCE(SUM(td.purchasePrice), 0) as totalCost, " +
           "COALESCE(AVG(td.purchasePrice), 0) as averageCost, " +
           "COALESCE(SUM(td.gstAmount), 0) as totalGST " +
           "FROM TyreDetail td " +
           "WHERE td.purchaseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM td.purchaseDate), EXTRACT(MONTH FROM td.purchaseDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyTyreSummary(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    /**
     * Get tyre statistics
     */
    @Query("SELECT " +
           "COUNT(td) as totalTyres, " +
           "COUNT(CASE WHEN td.isActive = true THEN 1 END) as activeTyres, " +
           "COALESCE(SUM(td.purchasePrice), 0) as totalCost, " +
           "COALESCE(AVG(td.purchasePrice), 0) as averageCost, " +
           "COUNT(CASE WHEN td.warrantyExpiryDate > CURRENT_DATE THEN 1 END) as activeWarranties, " +
           "COUNT(DISTINCT td.tyreCompany) as uniqueCompanies " +
           "FROM TyreDetail td")
    Object[] getTyreStatistics();
    
    /**
     * Get tyre performance ranking
     */
    @Query("SELECT td.tyreCompany, td.tyreSize, " +
           "COUNT(td) as tyreCount, " +
           "COALESCE(AVG(td.purchasePrice), 0) as avgPrice, " +
           "COALESCE(AVG(td.warrantyMonths), 0) as avgWarrantyMonths " +
           "FROM TyreDetail td " +
           "WHERE td.isActive = true " +
           "GROUP BY td.tyreCompany, td.tyreSize " +
           "ORDER BY tyreCount DESC, avgPrice ASC")
    List<Object[]> getTyrePerformanceRanking();

    // ===============================================
    // VALIDATION QUERIES
    // ===============================================
    
    /**
     * Check if invoice number exists for different tyre
     */
    boolean existsByInvoiceNumberAndIdNot(String invoiceNumber, Long id);
    
    /**
     * Count active tyres
     */
    long countByIsActiveTrue();
    
    /**
     * Count tyres by company
     */
    long countByTyreCompanyIgnoreCase(String tyreCompany);
    
    /**
     * Count tyres by position
     */
    long countByTyrePosition(TyreDetail.TyrePosition tyrePosition);
    
    /**
     * Count tyres for truck
     */
    long countByTruckIdAndIsActiveTrue(Long truckId);
    
    /**
     * Count tyres with active warranty
     */
    @Query("SELECT COUNT(td) FROM TyreDetail td WHERE td.warrantyExpiryDate > CURRENT_DATE AND td.isActive = true")
    long countTyresWithActiveWarranty();
}

