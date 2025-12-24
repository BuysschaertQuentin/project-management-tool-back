package com.iscod.project_management_tool_back.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskPriorityEnum;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.repository.IProjectMemberRepository;
import com.iscod.project_management_tool_back.repository.ITaskRepository;
import com.iscod.project_management_tool_back.service.INotificationService;
import com.iscod.project_management_tool_back.service.IProjectService;
import com.iscod.project_management_tool_back.service.ITaskHistoryService;
import com.iscod.project_management_tool_back.service.ITaskService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

    private final ITaskRepository taskRepository;
    private final IProjectService projectService;
    private final IPmtUserRepository userRepository;
    private final IProjectMemberRepository projectMemberRepository;
    private final INotificationService notificationService;
    private final ITaskHistoryService taskHistoryService;

    @Override
    @Transactional
    public Task createTask(Long projectId, CreateTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException {
        Project project = projectService.findById(projectId);

        PmtUser creator = userRepository.findById(request.getCreatedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedByUserId()));

        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, creator.getId())) {
            throw new BadRequestException("User is not a member of this project");
        }

        TaskPriorityEnum priority;
        try {
            priority = TaskPriorityEnum.fromString(request.getPriority());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("priority", "Valid values are: LOW, MEDIUM, HIGH, URGENT");
        }

        Task task = new Task();
        task.setProject(project);
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(priority);
        task.setStatus(TaskStatusEnum.TODO);
        task.setCreatedBy(creator);

        Task savedTask = taskRepository.save(task);
        taskHistoryService.recordCreation(savedTask, creator);

        return savedTask;
    }

    @Override
    @Transactional(readOnly = true)
    public Task findById(Long id) throws ResourceNotFoundException {
        return taskRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    @Override
    @Transactional
    public Task assignTask(Long taskId, AssignTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException {
        Task task = findById(taskId);
        PmtUser previousAssignee = task.getAssignedTo();

        PmtUser assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId()));

        if (!projectMemberRepository.existsByProjectIdAndUserId(task.getProject().getId(), assignee.getId())) {
            throw new BadRequestException("Assignee is not a member of this project");
        }

        task.setAssignedTo(assignee);
        Task savedTask = taskRepository.save(task);

        taskHistoryService.recordAssignment(savedTask, assignee, previousAssignee, assignee);
        notificationService.notifyTaskAssignment(savedTask, assignee);

        return savedTask;
    }

    @Override
    @Transactional
    public Task updateTask(Long taskId, UpdateTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException {
        Task task = findById(taskId);
        PmtUser updater = task.getCreatedBy();
        String oldStatus = task.getStatus().getStatus();

        if (request.getName() != null && !request.getName().isBlank()) {
            String oldName = task.getName();
            task.setName(request.getName());
            taskHistoryService.recordChange(task, updater, "UPDATE", "name", oldName, request.getName());
        }

        if (request.getDescription() != null) {
            String oldDesc = task.getDescription();
            task.setDescription(request.getDescription());
            taskHistoryService.recordChange(task, updater, "UPDATE", "description", oldDesc, request.getDescription());
        }

        if (request.getDueDate() != null) {
            String oldDate = task.getDueDate() != null ? task.getDueDate().toString() : null;
            task.setDueDate(request.getDueDate());
            taskHistoryService.recordChange(task, updater, "UPDATE", "dueDate", oldDate, request.getDueDate().toString());
        }

        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            try {
                String oldPriority = task.getPriority().getPriority();
                task.setPriority(TaskPriorityEnum.fromString(request.getPriority()));
                taskHistoryService.recordChange(task, updater, "UPDATE", "priority", oldPriority, request.getPriority());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("priority", request.getPriority());
            }
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                task.setStatus(TaskStatusEnum.fromString(request.getStatus()));
                taskHistoryService.recordStatusChange(task, updater, oldStatus, request.getStatus());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("status", request.getStatus());
            }
        }

        if (request.getCompletedDate() != null) {
            task.setCompletedDate(request.getCompletedDate());
        }

        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProject(Long projectId) throws ResourceNotFoundException {
        projectService.findById(projectId);
        return taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProjectAndStatus(Long projectId, TaskStatusEnum status) 
            throws ResourceNotFoundException {
        projectService.findById(projectId);
        return taskRepository.findByProjectIdAndStatus(projectId, status);
    }
}
