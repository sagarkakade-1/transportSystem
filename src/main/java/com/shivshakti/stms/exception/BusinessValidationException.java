package com.shivshakti.stms.exception;

/**
 * Exception thrown when business validation rules are violated
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public class BusinessValidationException extends STMSException {
    
    public BusinessValidationException(String message) {
        super("BUSINESS_VALIDATION_ERROR", message);
    }
    
    public BusinessValidationException(String message, Object... args) {
        super("BUSINESS_VALIDATION_ERROR", message, args);
    }
    
    public BusinessValidationException(String message, Throwable cause) {
        super("BUSINESS_VALIDATION_ERROR", message, cause);
    }
}
