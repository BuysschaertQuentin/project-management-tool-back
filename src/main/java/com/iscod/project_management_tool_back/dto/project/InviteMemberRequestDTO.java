package com.iscod.project_management_tool_back.dto.project;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InviteMemberRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Role to assign to the invited member.
     * Valid values: MEMBER, OBSERVER
     * ADMIN role can only be assigned via role update endpoint.
     */
    private String role = "MEMBER";
}
