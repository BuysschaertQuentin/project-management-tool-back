package com.iscod.project_management_tool_back.entity.pmtenum;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Roles that a user can have within a project.
 * - ADMIN: Can manage project, invite members, assign roles, create/update tasks
 * - MEMBER: Can create and update tasks, view project
 * - OBSERVER: Can only view project and tasks, receive notifications
 */
@Getter
@AllArgsConstructor
public enum ProjectRoleEnum {
    ADMIN("ADMIN"),
    MEMBER("MEMBER"),
    OBSERVER("OBSERVER");

    private final String role;

    public static ProjectRoleEnum fromString(String role) {
        return Arrays.stream(ProjectRoleEnum.values())
                .filter(projectRoleEnum -> projectRoleEnum.role.equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role));
    }
}
