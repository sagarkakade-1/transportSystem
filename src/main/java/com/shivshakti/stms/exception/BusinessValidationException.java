package com.shivshakti.stms.exception;

/**
 * Exception thrown when business validation rules are violated
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public class BusinessValidationException extends RuntimeException {
    
    private final String errorCode;
    
    public BusinessValidationException(String message) {
        super(message);
        this.errorCode = null;
    }
    
    public BusinessValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
    
    public BusinessValidationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

