package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.dto.project.InviteMemberRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.dto.project.UpdateMemberRoleDTO;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;

/**
 * Service interface for Project management operations.
 * Defines the contract for creating, retrieving, and managing projects.
 */
public interface IProjectService {

    /**
     * Creates a new project and automatically adds the creator as an ADMIN member.
     *
     * @param request the project creation request containing name, description, startDate, and createdById
     * @return the created Project entity
     */
    Project createProject(ProjectRequestDTO request);

    /**
     * Finds a project by its unique identifier.
     *
     * @param id the project ID
     * @return the Project entity
     * @throws com.iscod.project_management_tool_back.exception.ResourceNotFoundException if project is not found
     */
    Project findById(Long id);

    /**
     * Invites a user to a project by their email address.
     * Only ADMIN members can invite new members.
     *
     * @param projectId the project ID
     * @param request the invitation request containing email and role
     * @return the created ProjectMember entity
     */
    ProjectMember inviteMember(Long projectId, InviteMemberRequestDTO request);

    /**
     * Gets all members of a project.
     *
     * @param projectId the project ID
     * @return list of ProjectMember entities
     */
    List<ProjectMember> getProjectMembers(Long projectId);

    /**
     * Updates the role of a project member.
     * Only ADMIN members can change roles.
     *
     * @param projectId the project ID
     * @param memberId the project member ID
     * @param request the role update request
     * @return the updated ProjectMember entity
     */
    ProjectMember updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleDTO request);
}

