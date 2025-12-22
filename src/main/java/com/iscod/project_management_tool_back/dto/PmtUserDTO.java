package com.iscod.project_management_tool_back.dto;

import com.iscod.project_management_tool_back.constraint.PasswordValidator;
import com.iscod.project_management_tool_back.entity.pmtenum.ProjectRoleEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PmtUserDTO {
    @NotNull
    private Long id;

    @NotNull(message = "Name is required and size between 2 and 50 characters")
    @Size(min = 2, max = 50)
    private String name;

    @NotNull(message = "Email is required")
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$",
            message = "Invalid email format")
    private String email;

    @NotNull
    @Size(min = 6, max = 50)
    @PasswordValidator
    private String password;

    private ProjectRoleEnum role = ProjectRoleEnum.USER;

}

