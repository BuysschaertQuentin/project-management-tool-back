package com.iscod.project_management_tool_back.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectResponseDTO;
import com.iscod.project_management_tool_back.entity.Project;
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
        ProjectResponseDTO response = toResponse(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        Project project = projectService.findById(id);
        ProjectResponseDTO response = toResponse(project);
        return ResponseEntity.ok(response);
    }

    private ProjectResponseDTO toResponse(Project project) {
        ProjectResponseDTO response = new ProjectResponseDTO();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setCreatedById(project.getCreatedBy().getId());
        response.setCreatedByUsername(project.getCreatedBy().getUsername());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }
}
