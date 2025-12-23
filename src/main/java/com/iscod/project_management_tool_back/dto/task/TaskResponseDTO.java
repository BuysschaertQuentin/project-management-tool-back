package com.iscod.project_management_tool_back.dto.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * DTO for returning task information.
 * Includes all task details per US9.
 */
@Data
public class TaskResponseDTO {

    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String description;
    private String status;
    private String priority;
    private Long assignedToId;
    private String assignedToUsername;
    private Long createdByUserId;
    private String createdByUsername;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
