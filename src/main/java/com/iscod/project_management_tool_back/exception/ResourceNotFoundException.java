package com.iscod.project_management_tool_back.exception;

/**
 * Exception thrown when a requested resource is not found (HTTP 404).
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(resourceName + " not found with " + field + ": " + value);
    }
}
