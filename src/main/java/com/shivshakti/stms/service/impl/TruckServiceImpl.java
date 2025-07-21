package com.shivshakti.stms.service.impl;

import com.shivshakti.stms.dto.TruckDTO;
import com.shivshakti.stms.entity.Truck;
import com.shivshakti.stms.exception.BusinessValidationException;
import com.shivshakti.stms.exception.DuplicateResourceException;
import com.shivshakti.stms.exception.ResourceNotFoundException;
import com.shivshakti.stms.repository.TruckRepository;
import com.shivshakti.stms.service.TruckService;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TruckService interface
 * Provides comprehensive truck management functionality with business logic
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class TruckServiceImpl implements TruckService {

    private static final Logger logger = LoggerFactory.getLogger(TruckServiceImpl.class);
    
    private final TruckRepository truckRepository;
    
    @Autowired
    public TruckServiceImpl(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    // ===============================================
    // CRUD OPERATIONS
    // ===============================================
    
    @Override
    public TruckDTO createTruck(TruckDTO truckDTO) {
        logger.info("Creating new truck: {}", truckDTO.getTruckNumber());
        
        // Validate truck data
        validateTruckForCreation(truckDTO);
        
        // Convert DTO to entity
        Truck truck = convertToEntity(truckDTO);
        
        // Set default values
        if (truck.getCurrentOdometerReading() == null) {
            truck.setCurrentOdometerReading(BigDecimal.ZERO);
        }
        
        // Save truck
        Truck savedTruck = truckRepository.save(truck);
        
        logger.info("Successfully created truck with ID: {}", savedTruck.getId());
        return convertToDTO(savedTruck);
    }
    
    @Override
    public TruckDTO updateTruck(Long id, TruckDTO truckDTO) {
        logger.info("Updating truck with ID: {}", id);
        
        // Validate truck data
        validateTruckForUpdate(id, truckDTO);
        
        // Find existing truck
        Truck existingTruck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck", id));
        
        // Update fields
        updateTruckFields(existingTruck, truckDTO);
        
        // Save updated truck
        Truck updatedTruck = truckRepository.save(existingTruck);
        
        logger.info("Successfully updated truck with ID: {}", id);
        return convertToDTO(updatedTruck);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TruckDTO> getTruckById(Long id) {
        logger.debug("Fetching truck with ID: {}", id);
        
        return truckRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getAllActiveTrucks() {
        logger.debug("Fetching all active trucks");
        
        return truckRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TruckDTO> getAllTrucks(Pageable pageable) {
        logger.debug("Fetching all trucks with pagination: {}", pageable);
        
        return truckRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public void deleteTruck(Long id) {
        logger.info("Soft deleting truck with ID: {}", id);
        
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck", id));
        
        // Check if truck has active trips
        if (!isTruckAvailable(id)) {
            throw new BusinessValidationException("Cannot delete truck with active trips");
        }
        
        truck.setIsActive(false);
        truckRepository.save(truck);
        
        logger.info("Successfully deactivated truck with ID: {}", id);
    }
    
    @Override
    public void activateTruck(Long id) {
        logger.info("Activating truck with ID: {}", id);
        
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck", id));
        
        // Check if truck has valid documents
        validateTruckDocuments(truck);
        
        truck.setIsActive(true);
        truckRepository.save(truck);
        
        logger.info("Successfully activated truck with ID: {}", id);
    }

    // ===============================================
    // SEARCH AND FILTER OPERATIONS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public Page<TruckDTO> searchTrucks(String truckNumber, String model, String fuelType, 
                                      Boolean isActive, Pageable pageable) {
        logger.debug("Searching trucks with criteria - number: {}, model: {}, fuel: {}, active: {}", 
                    truckNumber, model, fuelType, isActive);
        
        return truckRepository.searchTrucks(truckNumber, model, fuelType, isActive, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TruckDTO> findByTruckNumber(String truckNumber) {
        logger.debug("Finding truck by truck number: {}", truckNumber);
        
        return truckRepository.findByTruckNumber(truckNumber)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> findByCapacityRange(BigDecimal minCapacity, BigDecimal maxCapacity) {
        logger.debug("Finding trucks by capacity range: {} - {}", minCapacity, maxCapacity);
        
        return truckRepository.findByCapacityBetween(minCapacity, maxCapacity)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> findByFuelType(String fuelType) {
        logger.debug("Finding trucks by fuel type: {}", fuelType);
        
        return truckRepository.findByFuelType(fuelType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // DOCUMENT MANAGEMENT
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithExpiredDocuments() {
        logger.debug("Fetching trucks with expired documents");
        
        return truckRepository.findTrucksWithExpiredDocuments()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithDocumentsExpiringSoon(int days) {
        logger.debug("Fetching trucks with documents expiring in {} days", days);
        
        return truckRepository.findTrucksWithDocumentsExpiringInDays(days)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithExpiredRC() {
        logger.debug("Fetching trucks with expired RC");
        
        return truckRepository.findTrucksWithExpiredRC()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithExpiredInsurance() {
        logger.debug("Fetching trucks with expired insurance");
        
        return truckRepository.findTrucksWithExpiredInsurance()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithExpiredPermits() {
        logger.debug("Fetching trucks with expired permits");
        
        return truckRepository.findTrucksWithExpiredPermits()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithExpiredFitness() {
        logger.debug("Fetching trucks with expired fitness certificates");
        
        return truckRepository.findTrucksWithExpiredFitness()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithExpiredPUC() {
        logger.debug("Fetching trucks with expired PUC certificates");
        
        return truckRepository.findTrucksWithExpiredPUC()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public TruckDTO updateDocumentInfo(Long id, String documentType, String documentNumber, LocalDate expiryDate) {
        logger.info("Updating {} document for truck ID: {}", documentType, id);
        
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck", id));
        
        // Validate expiry date
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new BusinessValidationException("Document expiry date cannot be in the past");
        }
        
        // Update document based on type
        switch (documentType.toUpperCase()) {
            case "RC":
                truck.setRcBookNumber(documentNumber);
                truck.setRcExpiryDate(expiryDate);
                break;
            case "INSURANCE":
                truck.setInsurancePolicyNumber(documentNumber);
                truck.setInsuranceExpiryDate(expiryDate);
                break;
            case "PERMIT":
                truck.setPermitNumber(documentNumber);
                truck.setPermitExpiryDate(expiryDate);
                break;
            case "FITNESS":
                truck.setFitnessCertificateNumber(documentNumber);
                truck.setFitnessExpiryDate(expiryDate);
                break;
            case "PUC":
                truck.setPucCertificateNumber(documentNumber);
                truck.setPucExpiryDate(expiryDate);
                break;
            default:
                throw new BusinessValidationException("Invalid document type: " + documentType);
        }
        
        // Add audit trail
        String auditMessage = String.format("%s document updated: %s (expires: %s) on %s", 
                                           documentType, documentNumber, expiryDate, LocalDate.now());
        addToRemarks(truck, auditMessage);
        
        Truck updatedTruck = truckRepository.save(truck);
        
        logger.info("Successfully updated {} document for truck ID: {}", documentType, id);
        return convertToDTO(updatedTruck);
    }

    // ===============================================
    // AVAILABILITY AND ASSIGNMENT
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getAvailableTrucks() {
        logger.debug("Fetching available trucks");
        
        return truckRepository.findAvailableTrucks()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithActiveTrips() {
        logger.debug("Fetching trucks with active trips");
        
        return truckRepository.findTrucksWithActiveTrips()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isTruckAvailable(Long id) {
        logger.debug("Checking availability for truck ID: {}", id);
        
        return truckRepository.findAvailableTrucks()
                .stream()
                .anyMatch(truck -> truck.getId().equals(id));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksByCapacity(BigDecimal requiredCapacity) {
        logger.debug("Finding trucks with capacity >= {}", requiredCapacity);
        
        return truckRepository.findByCapacityGreaterThanEqualAndIsActiveTrue(requiredCapacity)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // MAINTENANCE MANAGEMENT
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksDueForService() {
        logger.debug("Fetching trucks due for service");
        
        return truckRepository.findTrucksDueForService()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public TruckDTO updateOdometerReading(Long id, BigDecimal newReading) {
        logger.info("Updating odometer reading for truck ID: {} to {}", id, newReading);
        
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck", id));
        
        // Validate new reading
        if (truck.getCurrentOdometerReading() != null && 
            newReading.compareTo(truck.getCurrentOdometerReading()) < 0) {
            throw new BusinessValidationException("New odometer reading cannot be less than current reading");
        }
        
        BigDecimal oldReading = truck.getCurrentOdometerReading();
        truck.setCurrentOdometerReading(newReading);
        
        // Add audit trail
        String auditMessage = String.format("Odometer updated from %s to %s km on %s", 
                                           oldReading, newReading, LocalDate.now());
        addToRemarks(truck, auditMessage);
        
        Truck updatedTruck = truckRepository.save(truck);
        
        logger.info("Successfully updated odometer reading for truck ID: {}", id);
        return convertToDTO(updatedTruck);
    }
    
    @Override
    public TruckDTO updateServiceInfo(Long id, LocalDate serviceDate, BigDecimal nextServiceDue, String remarks) {
        logger.info("Updating service info for truck ID: {}", id);
        
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck", id));
        
        truck.setLastServiceDate(serviceDate);
        truck.setNextServiceDue(nextServiceDue);
        
        // Add audit trail
        String auditMessage = String.format("Service completed on %s. Next service due at %s km", 
                                           serviceDate, nextServiceDue);
        if (StringUtils.hasText(remarks)) {
            auditMessage += " - " + remarks;
        }
        addToRemarks(truck, auditMessage);
        
        Truck updatedTruck = truckRepository.save(truck);
        
        logger.info("Successfully updated service info for truck ID: {}", id);
        return convertToDTO(updatedTruck);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithHighMaintenanceCost(BigDecimal threshold) {
        logger.debug("Fetching trucks with maintenance cost above {}", threshold);
        
        return truckRepository.findTrucksWithHighMaintenanceCost(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // PERFORMANCE ANALYTICS
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTruckPerformanceSummary() {
        logger.debug("Fetching truck performance summary");
        
        return truckRepository.getTruckPerformanceSummary();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TruckDTO> getTopPerformingTrucks(Pageable pageable) {
        logger.debug("Fetching top performing trucks");
        
        return truckRepository.findTopPerformingTrucks(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithLowFuelEfficiency(BigDecimal threshold) {
        logger.debug("Fetching trucks with fuel efficiency below {}", threshold);
        
        return truckRepository.findTrucksWithLowFuelEfficiency(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTruckUtilizationReport(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating truck utilization report from {} to {}", startDate, endDate);
        
        return truckRepository.getTruckUtilizationReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTruckProfitability(Long id, LocalDate startDate, LocalDate endDate) {
        logger.debug("Calculating profitability for truck ID: {} from {} to {}", id, startDate, endDate);
        
        return truckRepository.calculateTruckProfitability(id, startDate, endDate);
    }

    // ===============================================
    // FUEL MANAGEMENT
    // ===============================================
    
    @Override
    public TruckDTO updateFuelEfficiency(Long id, BigDecimal newEfficiency) {
        logger.info("Updating fuel efficiency for truck ID: {} to {}", id, newEfficiency);
        
        if (newEfficiency.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Fuel efficiency must be greater than zero");
        }
        
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck", id));
        
        BigDecimal oldEfficiency = truck.getMileage();
        truck.setMileage(newEfficiency);
        
        // Add audit trail
        String auditMessage = String.format("Fuel efficiency updated from %s to %s km/l on %s", 
                                           oldEfficiency, newEfficiency, LocalDate.now());
        addToRemarks(truck, auditMessage);
        
        Truck updatedTruck = truckRepository.save(truck);
        
        logger.info("Successfully updated fuel efficiency for truck ID: {}", id);
        return convertToDTO(updatedTruck);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getFuelConsumptionReport(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating fuel consumption report from {} to {}", startDate, endDate);
        
        return truckRepository.getFuelConsumptionReport(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> getTrucksWithHighFuelConsumption(BigDecimal threshold) {
        logger.debug("Fetching trucks with fuel consumption above {}", threshold);
        
        return truckRepository.findTrucksWithHighFuelConsumption(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ===============================================
    // REPORTING
    // ===============================================
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyTruckSummary(LocalDate startDate, LocalDate endDate) {
        logger.debug("Generating monthly truck summary from {} to {}", startDate, endDate);
        
        return truckRepository.getMonthlyTruckSummary(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object[] getTruckStatistics() {
        logger.debug("Fetching truck statistics");
        
        return truckRepository.getTruckStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> generateTruckReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating truck report from {} to {}", startDate, endDate);
        
        return getAllActiveTrucks().stream()
                .map(dto -> {
                    // Enhance DTO with additional metrics
                    if (dto.getPurchasePrice() != null && dto.getPurchaseDate() != null) {
                        // Calculate depreciation (assuming 10% annual depreciation)
                        BigDecimal depreciatedValue = calculateDepreciationValue(
                            dto.getPurchasePrice(), dto.getPurchaseDate(), BigDecimal.valueOf(10));
                        // Could add this to DTO if needed
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TruckDTO> generateDocumentExpiryReport(int days) {
        logger.info("Generating document expiry report for next {} days", days);
        
        return getTrucksWithDocumentsExpiringSoon(days);
    }

    // ===============================================
    // VALIDATION METHODS
    // ===============================================
    
    @Override
    public void validateTruckForCreation(TruckDTO truckDTO) {
        logger.debug("Validating truck data for creation");
        
        // Check truck number uniqueness
        if (!isTruckNumberUnique(truckDTO.getTruckNumber(), null)) {
            throw new DuplicateResourceException("Truck", "truck number", truckDTO.getTruckNumber());
        }
        
        // Check RC book number uniqueness
        if (!isRcBookNumberUnique(truckDTO.getRcBookNumber(), null)) {
            throw new DuplicateResourceException("Truck", "RC book number", truckDTO.getRcBookNumber());
        }
        
        // Validate document expiry dates
        validateDocumentExpiryDates(truckDTO);
        
        // Validate capacity
        if (truckDTO.getCapacity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Truck capacity must be greater than zero");
        }
        
        // Validate fuel efficiency
        if (truckDTO.getMileage() != null && truckDTO.getMileage().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Fuel efficiency must be greater than zero");
        }
    }
    
    @Override
    public void validateTruckForUpdate(Long id, TruckDTO truckDTO) {
        logger.debug("Validating truck data for update");
        
        // Check truck number uniqueness
        if (!isTruckNumberUnique(truckDTO.getTruckNumber(), id)) {
            throw new DuplicateResourceException("Truck", "truck number", truckDTO.getTruckNumber());
        }
        
        // Check RC book number uniqueness
        if (!isRcBookNumberUnique(truckDTO.getRcBookNumber(), id)) {
            throw new DuplicateResourceException("Truck", "RC book number", truckDTO.getRcBookNumber());
        }
        
        // Validate document expiry dates
        validateDocumentExpiryDates(truckDTO);
        
        // Validate capacity
        if (truckDTO.getCapacity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Truck capacity must be greater than zero");
        }
        
        // Validate fuel efficiency
        if (truckDTO.getMileage() != null && truckDTO.getMileage().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Fuel efficiency must be greater than zero");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isTruckNumberUnique(String truckNumber, Long excludeId) {
        if (excludeId != null) {
            return !truckRepository.existsByTruckNumberAndIdNot(truckNumber, excludeId);
        }
        return truckRepository.findByTruckNumber(truckNumber).isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isRcBookNumberUnique(String rcBookNumber, Long excludeId) {
        if (excludeId != null) {
            return !truckRepository.existsByRcBookNumberAndIdNot(rcBookNumber, excludeId);
        }
        return truckRepository.findByRcBookNumber(rcBookNumber).isEmpty();
    }
    
    private void validateDocumentExpiryDates(TruckDTO truckDTO) {
        LocalDate now = LocalDate.now();
        
        if (truckDTO.getRcExpiryDate() != null && truckDTO.getRcExpiryDate().isBefore(now)) {
            throw new BusinessValidationException("RC expiry date cannot be in the past");
        }
        
        if (truckDTO.getInsuranceExpiryDate() != null && truckDTO.getInsuranceExpiryDate().isBefore(now)) {
            throw new BusinessValidationException("Insurance expiry date cannot be in the past");
        }
        
        if (truckDTO.getPermitExpiryDate() != null && truckDTO.getPermitExpiryDate().isBefore(now)) {
            throw new BusinessValidationException("Permit expiry date cannot be in the past");
        }
        
        if (truckDTO.getFitnessExpiryDate() != null && truckDTO.getFitnessExpiryDate().isBefore(now)) {
            throw new BusinessValidationException("Fitness certificate expiry date cannot be in the past");
        }
        
        if (truckDTO.getPucExpiryDate() != null && truckDTO.getPucExpiryDate().isBefore(now)) {
            throw new BusinessValidationException("PUC certificate expiry date cannot be in the past");
        }
    }
    
    // ===============================================
    // UTILITY METHODS
    // ===============================================
    
    @Override
    public TruckDTO convertToDTO(Truck truck) {
        if (truck == null) {
            return null;
        }
        
        TruckDTO dto = new TruckDTO();
        dto.setId(truck.getId());
        dto.setTruckNumber(truck.getTruckNumber());
        dto.setModel(truck.getModel());
        dto.setCapacity(truck.getCapacity());
        dto.setFuelType(truck.getFuelType());
        dto.setFuelTankCapacity(truck.getFuelTankCapacity());
        dto.setMileage(truck.getMileage());
        dto.setPurchaseDate(truck.getPurchaseDate());
        dto.setPurchasePrice(truck.getPurchasePrice());
        dto.setRcBookNumber(truck.getRcBookNumber());
        dto.setRcExpiryDate(truck.getRcExpiryDate());
        dto.setInsurancePolicyNumber(truck.getInsurancePolicyNumber());
        dto.setInsuranceExpiryDate(truck.getInsuranceExpiryDate());
        dto.setPermitNumber(truck.getPermitNumber());
        dto.setPermitExpiryDate(truck.getPermitExpiryDate());
        dto.setFitnessCertificateNumber(truck.getFitnessCertificateNumber());
        dto.setFitnessExpiryDate(truck.getFitnessExpiryDate());
        dto.setPucCertificateNumber(truck.getPucCertificateNumber());
        dto.setPucExpiryDate(truck.getPucExpiryDate());
        dto.setCurrentOdometerReading(truck.getCurrentOdometerReading());
        dto.setLastServiceDate(truck.getLastServiceDate());
        dto.setNextServiceDue(truck.getNextServiceDue());
        dto.setRemarks(truck.getRemarks());
        dto.setIsActive(truck.getIsActive());
        
        return dto;
    }
    
    @Override
    public Truck convertToEntity(TruckDTO truckDTO) {
        if (truckDTO == null) {
            return null;
        }
        
        Truck truck = new Truck();
        truck.setId(truckDTO.getId());
        truck.setTruckNumber(truckDTO.getTruckNumber());
        truck.setModel(truckDTO.getModel());
        truck.setCapacity(truckDTO.getCapacity());
        truck.setFuelType(truckDTO.getFuelType());
        truck.setFuelTankCapacity(truckDTO.getFuelTankCapacity());
        truck.setMileage(truckDTO.getMileage());
        truck.setPurchaseDate(truckDTO.getPurchaseDate());
        truck.setPurchasePrice(truckDTO.getPurchasePrice());
        truck.setRcBookNumber(truckDTO.getRcBookNumber());
        truck.setRcExpiryDate(truckDTO.getRcExpiryDate());
        truck.setInsurancePolicyNumber(truckDTO.getInsurancePolicyNumber());
        truck.setInsuranceExpiryDate(truckDTO.getInsuranceExpiryDate());
        truck.setPermitNumber(truckDTO.getPermitNumber());
        truck.setPermitExpiryDate(truckDTO.getPermitExpiryDate());
        truck.setFitnessCertificateNumber(truckDTO.getFitnessCertificateNumber());
        truck.setFitnessExpiryDate(truckDTO.getFitnessExpiryDate());
        truck.setPucCertificateNumber(truckDTO.getPucCertificateNumber());
        truck.setPucExpiryDate(truckDTO.getPucExpiryDate());
        truck.setCurrentOdometerReading(truckDTO.getCurrentOdometerReading());
        truck.setLastServiceDate(truckDTO.getLastServiceDate());
        truck.setNextServiceDue(truckDTO.getNextServiceDue());
        truck.setRemarks(truckDTO.getRemarks());
        truck.setIsActive(truckDTO.getIsActive());
        
        return truck;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTruckCount(Boolean isActive) {
        if (isActive != null && isActive) {
            return truckRepository.countByIsActiveTrue();
        }
        return truckRepository.count();
    }
    
    @Override
    public BigDecimal calculateDepreciationValue(BigDecimal purchasePrice, LocalDate purchaseDate, BigDecimal depreciationRate) {
        if (purchasePrice == null || purchaseDate == null || depreciationRate == null) {
            return BigDecimal.ZERO;
        }
        
        long yearsOwned = ChronoUnit.YEARS.between(purchaseDate, LocalDate.now());
        BigDecimal totalDepreciation = purchasePrice
                .multiply(depreciationRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(yearsOwned));
        
        BigDecimal currentValue = purchasePrice.subtract(totalDepreciation);
        return currentValue.compareTo(BigDecimal.ZERO) > 0 ? currentValue : BigDecimal.ZERO;
    }
    
    // ===============================================
    // PRIVATE HELPER METHODS
    // ===============================================
    
    private void updateTruckFields(Truck existingTruck, TruckDTO truckDTO) {
        existingTruck.setTruckNumber(truckDTO.getTruckNumber());
        existingTruck.setModel(truckDTO.getModel());
        existingTruck.setCapacity(truckDTO.getCapacity());
        existingTruck.setFuelType(truckDTO.getFuelType());
        existingTruck.setFuelTankCapacity(truckDTO.getFuelTankCapacity());
        existingTruck.setMileage(truckDTO.getMileage());
        existingTruck.setPurchaseDate(truckDTO.getPurchaseDate());
        existingTruck.setPurchasePrice(truckDTO.getPurchasePrice());
        existingTruck.setRcBookNumber(truckDTO.getRcBookNumber());
        existingTruck.setRcExpiryDate(truckDTO.getRcExpiryDate());
        existingTruck.setInsurancePolicyNumber(truckDTO.getInsurancePolicyNumber());
        existingTruck.setInsuranceExpiryDate(truckDTO.getInsuranceExpiryDate());
        existingTruck.setPermitNumber(truckDTO.getPermitNumber());
        existingTruck.setPermitExpiryDate(truckDTO.getPermitExpiryDate());
        existingTruck.setFitnessCertificateNumber(truckDTO.getFitnessCertificateNumber());
        existingTruck.setFitnessExpiryDate(truckDTO.getFitnessExpiryDate());
        existingTruck.setPucCertificateNumber(truckDTO.getPucCertificateNumber());
        existingTruck.setPucExpiryDate(truckDTO.getPucExpiryDate());
        existingTruck.setRemarks(truckDTO.getRemarks());
        
        // Don't update odometer reading, service dates through general update
        // Use specific methods for these fields
    }
    
    private void addToRemarks(Truck truck, String newRemark) {
        String currentRemarks = truck.getRemarks() != null ? truck.getRemarks() : "";
        truck.setRemarks(currentRemarks + "\n" + newRemark);
    }
    
    private void validateTruckDocuments(Truck truck) {
        LocalDate now = LocalDate.now();
        
        if (truck.getRcExpiryDate() != null && truck.getRcExpiryDate().isBefore(now)) {
            throw new BusinessValidationException("Cannot activate truck with expired RC");
        }
        
        if (truck.getInsuranceExpiryDate() != null && truck.getInsuranceExpiryDate().isBefore(now)) {
            throw new BusinessValidationException("Cannot activate truck with expired insurance");
        }
    }
}
