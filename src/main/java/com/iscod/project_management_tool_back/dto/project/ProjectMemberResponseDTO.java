package com.iscod.project_management_tool_back.dto.project;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProjectMemberResponseDTO {

    private Long id;
    private Long projectId;
    private String projectName;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime invitedAt;
    private LocalDateTime joinedAt;
}
