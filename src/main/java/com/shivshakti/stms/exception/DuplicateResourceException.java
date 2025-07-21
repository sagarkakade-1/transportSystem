package com.shivshakti.stms.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public class DuplicateResourceException extends STMSException {
    
    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
    }
    
    public DuplicateResourceException(String resourceType, String field, Object value) {
        super("DUPLICATE_RESOURCE", 
              String.format("%s with %s '%s' already exists", resourceType, field, value));
    }
    
    public DuplicateResourceException(String message, Throwable cause) {
        super("DUPLICATE_RESOURCE", message, cause);
    }
}
