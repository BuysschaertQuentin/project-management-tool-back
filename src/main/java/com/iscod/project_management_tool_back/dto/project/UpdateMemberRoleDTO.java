package com.iscod.project_management_tool_back.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMemberRoleDTO {

    @NotBlank(message = "Role is required")
    private String role;
}
