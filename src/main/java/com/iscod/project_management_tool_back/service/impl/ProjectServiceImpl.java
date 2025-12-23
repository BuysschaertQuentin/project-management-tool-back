package com.iscod.project_management_tool_back.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.entity.pmtenum.ProjectRoleEnum;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.repository.IProjectMemberRepository;
import com.iscod.project_management_tool_back.repository.IProjectRepository;
import com.iscod.project_management_tool_back.service.IProjectService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the IProjectService interface.
 * Handles project creation and retrieval operations.
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;
    private final IPmtUserRepository userRepository;
    private final IProjectMemberRepository projectMemberRepository;

    /**
     * Creates a new project and automatically adds the creator as an ADMIN member.
     * 
     * SECURITY NOTE: In a production environment, the createdById should be
     * extracted from the authenticated user's security context (JWT token),
     * not from the request body. This would prevent users from creating
     * projects on behalf of other users.
     */
    @Override
    @Transactional
    public Project createProject(ProjectRequestDTO request) {
        PmtUserDto creator = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCreatedById()));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setCreatedBy(creator);

        Project savedProject = projectRepository.save(project);

        // Add creator as ADMIN member of the project
        ProjectMember adminMember = new ProjectMember();
        adminMember.setProject(savedProject);
        adminMember.setUser(creator);
        adminMember.setRole(ProjectRoleEnum.ADMIN);
        adminMember.setJoinedAt(LocalDateTime.now());
        projectMemberRepository.save(adminMember);

        return savedProject;
    }

    /**
     * Finds a project by its unique identifier.
     * 
     * SECURITY NOTE: In production, this method should verify that the
     * requesting user is a member of the project before returning it.
     */
    @Override
    @Transactional(readOnly = true)
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }
}
