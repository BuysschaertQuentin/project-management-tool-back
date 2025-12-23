package com.iscod.project_management_tool_back.dto.task;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for assigning a task to a member.
 * According to US7: assign tasks to specific project members.
 */
@Data
public class AssignTaskRequestDTO {

    @NotNull(message = "Assignee user ID is required")
    private Long assigneeId;
}
