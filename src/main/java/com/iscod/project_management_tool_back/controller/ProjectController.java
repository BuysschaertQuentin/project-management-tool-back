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

import com.iscod.project_management_tool_back.datamapper.ProjectMapper;
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
    private final ProjectMapper projectMapper;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO request) {
        Project project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toDTO(project));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        Project project = projectService.findById(id);
        return ResponseEntity.ok(projectMapper.toDTO(project));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ProjectMemberResponseDTO> inviteMember(
            @PathVariable Long id,
            @Valid @RequestBody InviteMemberRequestDTO request) {
        ProjectMember member = projectService.inviteMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toMemberDTO(member));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberResponseDTO>> getProjectMembers(@PathVariable Long id) {
        List<ProjectMember> members = projectService.getProjectMembers(id);
        List<ProjectMemberResponseDTO> response = members.stream()
                .map(projectMapper::toMemberDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/members/{memberId}/role")
    public ResponseEntity<ProjectMemberResponseDTO> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateMemberRoleDTO request) {
        ProjectMember member = projectService.updateMemberRole(id, memberId, request);
        return ResponseEntity.ok(projectMapper.toMemberDTO(member));
    }
}
