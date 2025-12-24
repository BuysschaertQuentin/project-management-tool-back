package com.iscod.project_management_tool_back.dto.login;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}