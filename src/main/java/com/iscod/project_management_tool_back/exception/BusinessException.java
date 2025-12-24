package com.iscod.project_management_tool_back.exception;

/**
 * Base exception class for all business exceptions.
 * Extends Exception (checked) to force explicit handling.
 */
public abstract class BusinessException extends Exception {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
