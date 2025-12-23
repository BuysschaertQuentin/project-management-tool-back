package com.iscod.project_management_tool_back.dto.notification;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * DTO for returning notification information.
 */
@Data
public class NotificationResponseDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long taskId;
    private String taskName;
    private Long projectId;
    private String projectName;
    private String type;
    private String message;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
}
