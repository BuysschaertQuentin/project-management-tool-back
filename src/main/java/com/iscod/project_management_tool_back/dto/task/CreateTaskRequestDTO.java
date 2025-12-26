package com.iscod.project_management_tool_back.dto.task;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating a new task.
 * According to US6: name, description, due date, and priority are required.
 */
@Data
public class CreateTaskRequestDTO {

    @NotBlank(message = "Task name is required")
    @Size(min = 2, max = 200, message = "Task name must be between 2 and 200 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotNull(message = "Creator user ID is required")
    private Long createdByUserId;

    // Optional: assign task to a specific user
    private Long assignedToUserId;
}
