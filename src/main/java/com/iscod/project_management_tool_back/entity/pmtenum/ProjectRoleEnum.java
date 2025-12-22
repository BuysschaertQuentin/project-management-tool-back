package com.iscod.project_management_tool_back.entity.pmtenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ProjectRoleEnum {
    ADMIN("ADMIN"), USER("USER");

    private final String role;

    public static ProjectRoleEnum fromString(String role) {
        return Arrays.stream(ProjectRoleEnum.values())
                .filter(projectRoleEnum -> projectRoleEnum.role.equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role : " + role));
    }
}
