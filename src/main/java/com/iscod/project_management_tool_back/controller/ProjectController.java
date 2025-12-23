package com.iscod.project_management_tool_back.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.project_management_tool_back.dto.project.InviteMemberRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectMemberResponseDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectResponseDTO;
import com.iscod.project_management_tool_back.dto.project.UpdateMemberRoleDTO;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.service.IProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    /**
     * Create a new project.
     * The creator automatically becomes an ADMIN member of the project.
     * 
     * SECURITY NOTE: In production, the createdById should be extracted from
     * the authenticated user's JWT token, not from the request body.
     */
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO request) {
        Project project = projectService.createProject(request);
        ProjectResponseDTO response = toProjectResponse(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        Project project = projectService.findById(id);
        ProjectResponseDTO response = toProjectResponse(project);
        return ResponseEntity.ok(response);
    }

    /**
     * Invite a user to the project by their email address.
     * 
     * SECURITY NOTE: In production, this endpoint should verify that the
     * requesting user is an ADMIN of the project using JWT token validation.
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<ProjectMemberResponseDTO> inviteMember(
            @PathVariable Long id,
            @Valid @RequestBody InviteMemberRequestDTO request) {
        ProjectMember member = projectService.inviteMember(id, request);
        ProjectMemberResponseDTO response = toMemberResponse(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all members of a project.
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberResponseDTO>> getProjectMembers(@PathVariable Long id) {
        List<ProjectMember> members = projectService.getProjectMembers(id);
        List<ProjectMemberResponseDTO> response = members.stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Update the role of a project member.
     * 
     * SECURITY NOTE: In production, this endpoint should verify that the
     * requesting user is an ADMIN of the project using JWT token validation.
     */
    @PutMapping("/{id}/members/{memberId}/role")
    public ResponseEntity<ProjectMemberResponseDTO> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateMemberRoleDTO request) {
        ProjectMember member = projectService.updateMemberRole(id, memberId, request);
        ProjectMemberResponseDTO response = toMemberResponse(member);
        return ResponseEntity.ok(response);
    }

    private ProjectResponseDTO toProjectResponse(Project project) {
        ProjectResponseDTO response = new ProjectResponseDTO();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setCreatedByUserId(project.getCreatedBy().getId());
        response.setCreatedByUsername(project.getCreatedBy().getUsername());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }

    private ProjectMemberResponseDTO toMemberResponse(ProjectMember member) {
        ProjectMemberResponseDTO response = new ProjectMemberResponseDTO();
        response.setId(member.getId());
        response.setProjectId(member.getProject().getId());
        response.setProjectName(member.getProject().getName());
        response.setUserId(member.getUser().getId());
        response.setUsername(member.getUser().getUsername());
        response.setEmail(member.getUser().getEmail());
        response.setRole(member.getRole().getRole());
        response.setInvitedAt(member.getInvitedAt());
        response.setJoinedAt(member.getJoinedAt());
        return response;
    }
}
