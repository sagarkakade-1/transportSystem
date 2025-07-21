package com.shivshakti.stms.exception;

/**
 * Base exception class for STMS application
 * All custom exceptions should extend this class
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public class STMSException extends RuntimeException {
    
    private final String errorCode;
    private final Object[] args;
    
    public STMSException(String message) {
        super(message);
        this.errorCode = null;
        this.args = null;
    }
    
    public STMSException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.args = null;
    }
    
    public STMSException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public STMSException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public STMSException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
}
