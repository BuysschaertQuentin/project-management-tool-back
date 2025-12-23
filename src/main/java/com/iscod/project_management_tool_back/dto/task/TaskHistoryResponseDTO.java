package com.iscod.project_management_tool_back.dto.task;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * DTO for returning task history information (US12).
 */
@Data
public class TaskHistoryResponseDTO {

    private Long id;
    private Long taskId;
    private String taskName;
    private Long userId;
    private String username;
    private String action;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;
}
