package com.iscod.project_management_tool_back.dto.task;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for updating an existing task.
 * All fields are optional - only provided fields will be updated.
 * According to US8: can change any information or add an end date.
 */
@Data
public class UpdateTaskRequestDTO {

    @Size(min = 2, max = 200, message = "Task name must be between 2 and 200 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private LocalDate dueDate;

    private String priority;

    private String status;

    private LocalDate completedDate;
}
