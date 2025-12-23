package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.Task;

/**
 * Service interface for Task management operations.
 */
public interface ITaskService {

    /**
     * Creates a new task for a project.
     * Only ADMIN and MEMBER can create tasks.
     *
     * @param projectId the project ID
     * @param request the task creation request
     * @return the created Task entity
     */
    Task createTask(Long projectId, CreateTaskRequestDTO request);

    /**
     * Finds a task by its unique identifier.
     *
     * @param id the task ID
     * @return the Task entity
     */
    Task findById(Long id);

    /**
     * Assigns a task to a project member.
     * Only ADMIN and MEMBER can assign tasks.
     *
     * @param taskId the task ID
     * @param request the assignment request containing assignee user ID
     * @return the updated Task entity
     */
    Task assignTask(Long taskId, AssignTaskRequestDTO request);

    /**
     * Updates a task's information.
     * Only ADMIN and MEMBER can update tasks.
     *
     * @param taskId the task ID
     * @param request the update request with fields to update
     * @return the updated Task entity
     */
    Task updateTask(Long taskId, UpdateTaskRequestDTO request);

    /**
     * Gets all tasks for a project.
     *
     * @param projectId the project ID
     * @return list of Task entities
     */
    List<Task> getTasksByProject(Long projectId);
}
