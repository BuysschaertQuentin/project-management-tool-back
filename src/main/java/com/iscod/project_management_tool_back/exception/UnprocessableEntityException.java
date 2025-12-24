package com.iscod.project_management_tool_back.exception;

/**
 * Exception thrown when an operation cannot be processed due to business rules (HTTP 422).
 */
public class UnprocessableEntityException extends BusinessException {

    public UnprocessableEntityException(String message) {
        super(message);
    }
}
