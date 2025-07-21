package com.shivshakti.stms.exception;

/**
 * Exception thrown when a requested resource is not found
 * 
 * @author STMS Development Team
 * @version 1.0.0
 */
public class ResourceNotFoundException extends STMSException {
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
    
    public ResourceNotFoundException(String resourceType, Object id) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s with id '%s' not found", resourceType, id));
    }
    
    public ResourceNotFoundException(String resourceType, String field, Object value) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s with %s '%s' not found", resourceType, field, value));
    }
}
