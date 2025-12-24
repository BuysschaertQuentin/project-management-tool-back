package com.iscod.project_management_tool_back.exception;

/**
 * Exception thrown when authentication fails (HTTP 401).
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException() {
        super("Authentication failed");
    }
}
