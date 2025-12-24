package com.iscod.project_management_tool_back.datamapper;

import org.springframework.stereotype.Component;

import com.iscod.project_management_tool_back.dto.project.ProjectMemberResponseDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectResponseDTO;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;

@Component
public class ProjectMapper {

    public ProjectResponseDTO toDTO(Project project) {
        if (project == null) return null;
        
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setCreatedByUserId(project.getCreatedBy().getId());
        dto.setCreatedByUsername(project.getCreatedBy().getUsername());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }

    public ProjectMemberResponseDTO toMemberDTO(ProjectMember member) {
        if (member == null) return null;
        
        ProjectMemberResponseDTO dto = new ProjectMemberResponseDTO();
        dto.setId(member.getId());
        dto.setProjectId(member.getProject().getId());
        dto.setProjectName(member.getProject().getName());
        dto.setUserId(member.getUser().getId());
        dto.setUsername(member.getUser().getUsername());
        dto.setEmail(member.getUser().getEmail());
        dto.setRole(member.getRole().getRole());
        dto.setInvitedAt(member.getInvitedAt());
        dto.setJoinedAt(member.getJoinedAt());
        return dto;
    }
}
