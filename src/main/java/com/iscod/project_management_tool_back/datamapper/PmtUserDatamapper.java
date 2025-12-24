package com.iscod.project_management_tool_back.datamapper;

import org.springframework.stereotype.Component;

import com.iscod.project_management_tool_back.dto.login.UserResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;

@Component
public class PmtUserDatamapper {

    public UserResponseDTO toDTO(PmtUser user) {
        if (user == null) return null;
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
