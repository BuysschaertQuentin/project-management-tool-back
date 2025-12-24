package com.iscod.project_management_tool_back.exception;

/**
 * Exception thrown when input data is invalid (HTTP 400).
 */
public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String field, String reason) {
        super("Invalid " + field + ": " + reason);
    }
}
