package com.iscod.project_management_tool_back.service;

import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.entity.Project;

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
}
