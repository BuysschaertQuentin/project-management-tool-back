package com.iscod.project_management_tool_back.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.project_management_tool_back.dto.project.InviteMemberRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.dto.project.UpdateMemberRoleDTO;
import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.entity.pmtenum.ProjectRoleEnum;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.repository.IProjectMemberRepository;
import com.iscod.project_management_tool_back.repository.IProjectRepository;
import com.iscod.project_management_tool_back.service.IProjectService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the IProjectService interface.
 * Handles project creation, retrieval, and member management operations.
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
     * not from the request body.
     */
    @Override
    @Transactional
    public Project createProject(ProjectRequestDTO request) {
        PmtUserDto creator = userRepository.findById(request.getCreatedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCreatedByUserId()));

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

    /**
     * Invites a user to a project by their email address.
     * 
     * SECURITY NOTE: In production, you should verify that the requesting user
     * is an ADMIN of the project before allowing them to invite members.
     */
    @Override
    @Transactional
    public ProjectMember inviteMember(Long projectId, InviteMemberRequestDTO request) {
        Project project = findById(projectId);

        PmtUserDto user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new BadRequestException("User is already a member of this project");
        }

        ProjectRoleEnum role;
        try {
            role = ProjectRoleEnum.fromString(request.getRole());
        } catch (IllegalArgumentException e) {
            role = ProjectRoleEnum.MEMBER;
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        member.setInvitedAt(LocalDateTime.now());

        return projectMemberRepository.save(member);
    }

    /**
     * Gets all members of a project.
     * 
     * SECURITY NOTE: In production, verify that the requesting user
     * is a member of the project before returning the member list.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectMember> getProjectMembers(Long projectId) {
        findById(projectId);
        return projectMemberRepository.findByProjectId(projectId);
    }

    /**
     * Updates the role of a project member.
     * 
     * SECURITY NOTE: In production, you should verify that the requesting user
     * is an ADMIN of the project before allowing them to change member roles.
     */
    @Override
    @Transactional
    public ProjectMember updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleDTO request) {
        // Verify project exists
        findById(projectId);

        // Find the member
        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));

        // Verify member belongs to this project
        if (!member.getProject().getId().equals(projectId)) {
            throw new BadRequestException("Member does not belong to this project");
        }

        // Parse and validate the new role
        ProjectRoleEnum newRole;
        try {
            newRole = ProjectRoleEnum.fromString(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + request.getRole() + ". Valid roles are: ADMIN, MEMBER, OBSERVER");
        }

        member.setRole(newRole);
        return projectMemberRepository.save(member);
    }
}

