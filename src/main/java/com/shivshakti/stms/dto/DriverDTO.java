package com.shivshakti.stms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Driver entity
 * Used for API requests and responses
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverDTO {
    
    private Long id;
    
    @NotBlank(message = "Driver name is required")
    @Size(min = 2, max = 100, message = "Driver name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "License number is required")
    @Size(min = 5, max = 20, message = "License number must be between 5 and 20 characters")
    private String licenseNumber;
    
    @NotNull(message = "License expiry date is required")
    @Future(message = "License expiry date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate licenseExpiryDate;
    
    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
    
    private String alternateContactNumber;
    
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
    
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid salary format")
    private BigDecimal salary;
    
    @DecimalMin(value = "0.0", message = "Advance paid cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid advance amount format")
    private BigDecimal advancePaid = BigDecimal.ZERO;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    
    private String emergencyContactName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Emergency contact number must be 10 digits")
    private String emergencyContactNumber;
    
    private String bloodGroup;
    
    private String remarks;
    
    private Boolean isActive = true;
    
    // Performance metrics (read-only)
    private Integer totalTrips;
    private Integer completedTrips;
    private BigDecimal totalDistanceCovered;
    private Double averageRating;
    
    // Constructors
    public DriverDTO() {}
    
    public DriverDTO(String name, String licenseNumber, LocalDate licenseExpiryDate, 
                     String contactNumber, String address, BigDecimal salary) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
        this.contactNumber = contactNumber;
        this.address = address;
        this.salary = salary;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }
    
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getAlternateContactNumber() {
        return alternateContactNumber;
    }
    
    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public BigDecimal getSalary() {
        return salary;
    }
    
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
    
    public BigDecimal getAdvancePaid() {
        return advancePaid;
    }
    
    public void setAdvancePaid(BigDecimal advancePaid) {
        this.advancePaid = advancePaid;
    }
    
    public LocalDate getJoiningDate() {
        return joiningDate;
    }
    
    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }
    
    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }
    
    public String getBloodGroup() {
        return bloodGroup;
    }
    
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getTotalTrips() {
        return totalTrips;
    }
    
    public void setTotalTrips(Integer totalTrips) {
        this.totalTrips = totalTrips;
    }
    
    public Integer getCompletedTrips() {
        return completedTrips;
    }
    
    public void setCompletedTrips(Integer completedTrips) {
        this.completedTrips = completedTrips;
    }
    
    public BigDecimal getTotalDistanceCovered() {
        return totalDistanceCovered;
    }
    
    public void setTotalDistanceCovered(BigDecimal totalDistanceCovered) {
        this.totalDistanceCovered = totalDistanceCovered;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    // Utility methods
    public boolean isLicenseExpiringSoon(int days) {
        return licenseExpiryDate != null && 
               licenseExpiryDate.isBefore(LocalDate.now().plusDays(days));
    }
    
    public boolean hasOutstandingAdvance() {
        return advancePaid != null && advancePaid.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Override
    public String toString() {
        return "DriverDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
