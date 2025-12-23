package com.iscod.project_management_tool_back.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskPriorityEnum;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.repository.IProjectMemberRepository;
import com.iscod.project_management_tool_back.repository.ITaskRepository;
import com.iscod.project_management_tool_back.service.IProjectService;
import com.iscod.project_management_tool_back.service.ITaskService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the ITaskService interface.
 * Handles task creation, assignment, update, and retrieval operations.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

    private final ITaskRepository taskRepository;
    private final IProjectService projectService;
    private final IPmtUserRepository userRepository;
    private final IProjectMemberRepository projectMemberRepository;

    /**
     * Creates a new task for a project.
     * 
     * SECURITY NOTE: In production, verify that the creator is an ADMIN or MEMBER
     * of the project using JWT token validation.
     */
    @Override
    @Transactional
    public Task createTask(Long projectId, CreateTaskRequestDTO request) {
        Project project = projectService.findById(projectId);

        PmtUserDto creator = userRepository.findById(request.getCreatedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getCreatedByUserId()));

        // Verify creator is a member of the project (ADMIN or MEMBER role)
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, creator.getId())) {
            throw new BadRequestException("User is not a member of this project");
        }

        // Parse priority
        TaskPriorityEnum priority;
        try {
            priority = TaskPriorityEnum.fromString(request.getPriority());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid priority: " + request.getPriority() + 
                ". Valid values are: LOW, MEDIUM, HIGH, URGENT");
        }

        Task task = new Task();
        task.setProject(project);
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(priority);
        task.setStatus(TaskStatusEnum.TODO);
        task.setCreatedBy(creator);

        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    /**
     * Assigns a task to a project member.
     * 
     * SECURITY NOTE: In production, verify that the requesting user is an 
     * ADMIN or MEMBER of the project.
     */
    @Override
    @Transactional
    public Task assignTask(Long taskId, AssignTaskRequestDTO request) {
        Task task = findById(taskId);

        PmtUserDto assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssigneeId()));

        // Verify assignee is a member of the project
        if (!projectMemberRepository.existsByProjectIdAndUserId(task.getProject().getId(), assignee.getId())) {
            throw new BadRequestException("Assignee is not a member of this project");
        }

        task.setAssignedTo(assignee);
        return taskRepository.save(task);
    }

    /**
     * Updates a task's information.
     * 
     * SECURITY NOTE: In production, verify that the requesting user is an 
     * ADMIN or MEMBER of the project.
     */
    @Override
    @Transactional
    public Task updateTask(Long taskId, UpdateTaskRequestDTO request) {
        Task task = findById(taskId);

        // Update fields if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            task.setName(request.getName());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            try {
                task.setPriority(TaskPriorityEnum.fromString(request.getPriority()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid priority: " + request.getPriority());
            }
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                task.setStatus(TaskStatusEnum.fromString(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + request.getStatus());
            }
        }

        if (request.getCompletedDate() != null) {
            task.setCompletedDate(request.getCompletedDate());
        }

        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProject(Long projectId) {
        projectService.findById(projectId);
        return taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }
}
