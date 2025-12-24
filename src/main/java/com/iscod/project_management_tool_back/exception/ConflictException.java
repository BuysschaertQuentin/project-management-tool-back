package com.iscod.project_management_tool_back.exception;

/**
 * Exception thrown when there's a conflict with existing data (HTTP 409).
 * Examples: duplicate email, duplicate project member, etc.
 */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resourceName, String field, String value) {
        super(resourceName + " with " + field + " '" + value + "' already exists");
    }
}
