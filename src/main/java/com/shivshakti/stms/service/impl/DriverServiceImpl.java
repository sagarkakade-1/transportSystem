package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.DriverDTO;
import com.shivshakti.stms.entity.Driver;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.exception.DuplicateResourceException;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.repository.DriverRepository;
import com.shivshakti.stms.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of DriverService interface
 * Provides comprehensive driver management functionality with business logic
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private static final Logger logger = LoggerFactory.getLogger(DriverServiceImpl.class);
    
    private final DriverRepository driverRepository;
    
    @Autowired
    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    @Override
    public DriverDTO createDriver(DriverDTO driverDTO) {
        logger.info("Creating new driver: {}", driverDTO.getName());
        
        // Validate driver data
        validateDriverForCreation(driverDTO);
        
        // Convert DTO to entity
        Driver driver = convertToEntity(driverDTO);
        
        // Set default values
        if (driver.getJoiningDate() == null) {
            driver.setJoiningDate(LocalDate.now());
        }
        
        // Save driver
        Driver savedDriver = driverRepository.save(driver);
        
        logger.info("Successfully created driver with ID: {}", savedDriver.getId());
        return convertToDTO(savedDriver);
    }
    
    @Override
    public DriverDTO updateDriver(Long id, DriverDTO driverDTO) {
        logger.info("Updating driver with ID: {}", id);
        
        // Validate driver data
        validateDriverForUpdate(id, driverDTO);
        
        // Find existing driver
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        
        // Update fields
        updateDriverFields(existingDriver, driverDTO);
        
        // Save updated driver
        Driver updatedDriver = driverRepository.save(existingDriver);
        
        logger.info("Successfully updated driver with ID: {}", id);
        return convertToDTO(updatedDriver);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<DriverDTO> getDriverById(Long id) {
        logger.debug("Fetching driver with ID: {}", id);
        
        return driverRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getAllActiveDrivers() {
        logger.debug("Fetching all active drivers");
        
        return driverRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DriverDTO> getAllDrivers(Pageable pageable) {
        logger.debug("Fetching all drivers with pagination: {}", pageable);
        
        return driverRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public void deleteDriver(Long id) {
        logger.info("Soft deleting driver with ID: {}", id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        
        // Check if driver has active trips
        if (isDriverAvailable(id)) {
            driver.setIsActive(false);
            driverRepository.save(driver);
            logger.info("Successfully deactivated driver with ID: {}", id);
        } else {
            throw new BusinessValidationException("Cannot delete driver with active trips");
        }
    }
    
    @Override
    public void activateDriver(Long id) {
        logger.info("Activating driver with ID: {}", id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        
        driver.setIsActive(true);
        driverRepository.save(driver);
        
        logger.info("Successfully activated driver with ID: {}", id);
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Page<DriverDTO> searchDrivers(String name, String licenseNumber, 
                                        String contactNumber, Boolean isActive, 
                                        Pageable pageable) {
        logger.debug("Searching drivers with criteria - name: {}, license: {}, contact: {}, active: {}", 
                    name, licenseNumber, contactNumber, isActive);
        
        return driverRepository.searchDrivers(name, licenseNumber, contactNumber, isActive, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<DriverDTO> findByLicenseNumber(String licenseNumber) {
        logger.debug("Finding driver by license number: {}", licenseNumber);
        
        return driverRepository.findByLicenseNumber(licenseNumber)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<DriverDTO> findByContactNumber(String contactNumber) {
        logger.debug("Finding driver by contact number: {}", contactNumber);
        
        return driverRepository.findByContactNumber(contactNumber)
                .map(this::convertToDTO);
    }

    // ===============================================
    // LICENSE MANAGEMENT
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getDriversWithExpiredLicenses() {
        logger.debug("Fetching drivers with expired licenses");
        
        return driverRepository.findDriversWithExpiredLicenses()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getDriversWithLicensesExpiringSoon(int days) {
        logger.debug("Fetching drivers with licenses expiring in {} days", days);
        
        return driverRepository.findDriversWithLicensesExpiringInDays(days)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DriverDTO updateLicenseInfo(Long id, String licenseNumber, LocalDate expiryDate) {
        logger.info("Updating license info for driver ID: {}", id);
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        
        // Validate license number uniqueness
        if (!isLicenseNumberUnique(licenseNumber, id)) {
            throw new DuplicateResourceException("Driver", "license number", licenseNumber);
        }
        
        // Validate expiry date
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new BusinessValidationException("License expiry date cannot be in the past");
        }
        
        driver.setLicenseNumber(licenseNumber);
        driver.setLicenseExpiryDate(expiryDate);
        
        Driver updatedDriver = driverRepository.save(driver);
        
        logger.info("Successfully updated license info for driver ID: {}", id);
        return convertToDTO(updatedDriver);
    }

    // ===============================================
    // SALARY AND ADVANCE MANAGEMENT
    // ===============================================
    
    @Override
    public DriverDTO updateSalary(Long id, BigDecimal newSalary) {
        logger.info("Updating salary for driver ID: {} to {}", id, newSalary);
        
        if (newSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Salary must be greater than zero");
        }
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        
        driver.setSalary(newSalary);
        Driver updatedDriver = driverRepository.save(driver);
        
        logger.info("Successfully updated salary for driver ID: {}", id);
        return convertToDTO(updatedDriver);
    }
    
    @Override
    public DriverDTO addAdvancePayment(Long id, BigDecimal advanceAmount, String remarks) {
        logger.info("Adding advance payment of {} for driver ID: {}", advanceAmount, id);
        
        if (advanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Advance amount must be greater than zero");
        }
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        
        BigDecimal currentAdvance = driver.getAdvancePaid() != null ? 
                                   driver.getAdvancePaid() : BigDecimal.ZERO;
        driver.setAdvancePaid(currentAdvance.add(advanceAmount));
        
        if (StringUtils.hasText(remarks)) {
            String currentRemarks = driver.getRemarks() != null ? driver.getRemarks() : "";
            driver.setRemarks(currentRemarks + "\n" + LocalDate.now() + ": Advance added - " + remarks);
        }
        
        Driver updatedDriver = driverRepository.save(driver);
        
        logger.info("Successfully added advance payment for driver ID: {}", id);
        return convertToDTO(updatedDriver);
    }
    
    @Override
    public DriverDTO deductAdvance(Long id, BigDecimal deductionAmount, String remarks) {
        logger.info("Deducting advance of {} for driver ID: {}", deductionAmount, id);
        
        if (deductionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Deduction amount must be greater than zero");
        }
        
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        
        BigDecimal currentAdvance = driver.getAdvancePaid() != null ? 
                                   driver.getAdvancePaid() : BigDecimal.ZERO;
        
        if (currentAdvance.compareTo(deductionAmount) < 0) {
            throw new BusinessValidationException("Deduction amount cannot exceed current advance");
        }
        
        driver.setAdvancePaid(currentAdvance.subtract(deductionAmount));
        
        if (StringUtils.hasText(remarks)) {
            String currentRemarks = driver.getRemarks() != null ? driver.getRemarks() : "";
            driver.setRemarks(currentRemarks + "\n" + LocalDate.now() + ": Advance deducted - " + remarks);
        }
        
        Driver updatedDriver = driverRepository.save(driver);
        
        logger.info("Successfully deducted advance for driver ID: {}", id);
        return convertToDTO(updatedDriver);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getDriversWithOutstandingAdvances() {
        logger.debug("Fetching drivers with outstanding advances");
        
        return driverRepository.findDriversWithOutstandingAdvances()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalSalaryExpense() {
        logger.debug("Calculating total salary expense");
        
        return driverRepository.calculateTotalSalaryExpense();
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalOutstandingAdvances() {
        logger.debug("Calculating total outstanding advances");
        
        return driverRepository.calculateTotalOutstandingAdvances();
    }

    // ===============================================
    // AVAILABILITY AND ASSIGNMENT
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getAvailableDrivers() {
        logger.debug("Fetching available drivers");
        
        return driverRepository.findAvailableDrivers()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> getDriversWithActiveTrips() {
        logger.debug("Fetching drivers with active trips");
        
        return driverRepository.findDriversWithActiveTrips()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isDriverAvailable(Long id) {
        logger.debug("Checking availability for driver ID: {}", id);
        
        return driverRepository.findAvailableDrivers()
                .stream()
                .anyMatch(driver -> driver.getId().equals(id));
    }

    // ===============================================
    // PERFORMANCE ANALYTICS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDriverPerformanceSummary() {
        logger.debug("Fetching driver performance summary");
        
        return driverRepository.getDriverPerformanceSummary();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<DriverDTO> getTopPerformingDrivers(Pageable pageable) {
        logger.debug("Fetching top performing drivers");
        
        return driverRepository.findTopPerformingDrivers(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDriverTripCounts(LocalDate startDate, LocalDate endDate) {
        logger.debug("Fetching driver trip counts from {} to {}", startDate, endDate);
        
        return driverRepository.getDriverTripCounts(startDate, endDate);
    }

    // ===============================================
    // REPORTING
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyDriverSummary(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating monthly driver summary from {} to {}", startDate, endDate);
        
        return driverRepository.getMonthlyDriverSummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getDriverStatistics() {
        logger.debug("Fetching driver statistics");
        
        return driverRepository.getDriverStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DriverDTO> generateDriverReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating driver report from {} to {}", startDate, endDate);
        
        return driverRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    @Override
    public void validateDriverForCreation(DriverDTO driverDTO) {
        logger.debug("Validating driver data for creation");
        
        // Check license number uniqueness
        if (!isLicenseNumberUnique(driverDTO.getLicenseNumber(), null)) {
            throw new DuplicateResourceException("Driver", "license number", driverDTO.getLicenseNumber());
        }
        
        // Check contact number uniqueness
        if (!isContactNumberUnique(driverDTO.getContactNumber(), null)) {
            throw new DuplicateResourceException("Driver", "contact number", driverDTO.getContactNumber());
        }
        
        // Validate license expiry date
        if (driverDTO.getLicenseExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessValidationException("License expiry date cannot be in the past");
        }
        
        // Validate salary
        if (driverDTO.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Salary must be greater than zero");
        }
    }
    
    @Override
    public void validateDriverForUpdate(Long id, DriverDTO driverDTO) {
        logger.debug("Validating driver data for update");
        
        // Check license number uniqueness
        if (!isLicenseNumberUnique(driverDTO.getLicenseNumber(), id)) {
            throw new DuplicateResourceException("Driver", "license number", driverDTO.getLicenseNumber());
        }
        
        // Check contact number uniqueness
        if (!isContactNumberUnique(driverDTO.getContactNumber(), id)) {
            throw new DuplicateResourceException("Driver", "contact number", driverDTO.getContactNumber());
        }
        
        // Validate license expiry date
        if (driverDTO.getLicenseExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessValidationException("License expiry date cannot be in the past");
        }
        
        // Validate salary
        if (driverDTO.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Salary must be greater than zero");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isLicenseNumberUnique(String licenseNumber, Long excludeId) {
        if (excludeId != null) {
            return !driverRepository.existsByLicenseNumberAndIdNot(licenseNumber, excludeId);
        }
        return driverRepository.findByLicenseNumber(licenseNumber).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isContactNumberUnique(String contactNumber, Long excludeId) {
        if (excludeId != null) {
            return !driverRepository.existsByContactNumberAndIdNot(contactNumber, excludeId);
        }
        return driverRepository.findByContactNumber(contactNumber).isEmpty();
    }

    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    @Override
    public DriverDTO convertToDTO(Driver driver) {
        if (driver == null) {
            return null;
        }
        
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setName(driver.getName());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setLicenseExpiryDate(driver.getLicenseExpiryDate());
        dto.setContactNumber(driver.getContactNumber());
        dto.setAlternateContactNumber(driver.getAlternateContactNumber());
        dto.setAddress(driver.getAddress());
        dto.setDateOfBirth(driver.getDateOfBirth());
        dto.setSalary(driver.getSalary());
        dto.setAdvancePaid(driver.getAdvancePaid());
        dto.setJoiningDate(driver.getJoiningDate());
        dto.setEmergencyContactName(driver.getEmergencyContactName());
        dto.setEmergencyContactNumber(driver.getEmergencyContactNumber());
        dto.setBloodGroup(driver.getBloodGroup());
        dto.setRemarks(driver.getRemarks());
        dto.setIsActive(driver.getIsActive());
        
        return dto;
    }
    
    @Override
    public Driver convertToEntity(DriverDTO driverDTO) {
        if (driverDTO == null) {
            return null;
        }
        
        Driver driver = new Driver();
        driver.setId(driverDTO.getId());
        driver.setName(driverDTO.getName());
        driver.setLicenseNumber(driverDTO.getLicenseNumber());
        driver.setLicenseExpiryDate(driverDTO.getLicenseExpiryDate());
        driver.setContactNumber(driverDTO.getContactNumber());
        driver.setAlternateContactNumber(driverDTO.getAlternateContactNumber());
        driver.setAddress(driverDTO.getAddress());
        driver.setDateOfBirth(driverDTO.getDateOfBirth());
        driver.setSalary(driverDTO.getSalary());
        driver.setAdvancePaid(driverDTO.getAdvancePaid());
        driver.setJoiningDate(driverDTO.getJoiningDate());
        driver.setEmergencyContactName(driverDTO.getEmergencyContactName());
        driver.setEmergencyContactNumber(driverDTO.getEmergencyContactNumber());
        driver.setBloodGroup(driverDTO.getBloodGroup());
        driver.setRemarks(driverDTO.getRemarks());
        driver.setIsActive(driverDTO.getIsActive());
        
        return driver;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getDriverCount(Boolean isActive) {
        if (isActive != null && isActive) {
            return driverRepository.countByIsActiveTrue();
        }
        return driverRepository.count();
    }
    
    // ===============================================
    // PRIVATE HELPER METHODS
    // ===============================================
    
    private void updateDriverFields(Driver existingDriver, DriverDTO driverDTO) {
        existingDriver.setName(driverDTO.getName());
        existingDriver.setLicenseNumber(driverDTO.getLicenseNumber());
        existingDriver.setLicenseExpiryDate(driverDTO.getLicenseExpiryDate());
        existingDriver.setContactNumber(driverDTO.getContactNumber());
        existingDriver.setAlternateContactNumber(driverDTO.getAlternateContactNumber());
        existingDriver.setAddress(driverDTO.getAddress());
        existingDriver.setDateOfBirth(driverDTO.getDateOfBirth());
        existingDriver.setSalary(driverDTO.getSalary());
        existingDriver.setEmergencyContactName(driverDTO.getEmergencyContactName());
        existingDriver.setEmergencyContactNumber(driverDTO.getEmergencyContactNumber());
        existingDriver.setBloodGroup(driverDTO.getBloodGroup());
        existingDriver.setRemarks(driverDTO.getRemarks());
        
        // Don't update advance paid through general update - use specific methods
        // Don't update joining date through general update
        // Don't update isActive through general update - use activate/deactivate methods
    }
}
