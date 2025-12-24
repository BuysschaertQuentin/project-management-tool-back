package com.iscod.project_management_tool_back.exception;

/**
 * Exception thrown when user is not authorized to perform an action (HTTP 403).
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("Access denied");
    }

    public ForbiddenException(String action, String resource) {
        super("You are not authorized to " + action + " this " + resource);
    }
}
