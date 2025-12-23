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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.TaskHistoryResponseDTO;
import com.iscod.project_management_tool_back.dto.task.TaskResponseDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;
import com.iscod.project_management_tool_back.service.ITaskHistoryService;
import com.iscod.project_management_tool_back.service.ITaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;
    private final ITaskHistoryService taskHistoryService;

    /**
     * Create a new task for a project.
     * Only ADMIN and MEMBER can create tasks.
     */
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequestDTO request) {
        Task task = taskService.createTask(projectId, request);
        TaskResponseDTO response = toResponse(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a task by its ID.
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id) {
        Task task = taskService.findById(id);
        TaskResponseDTO response = toResponse(task);
        return ResponseEntity.ok(response);
    }

    /**
     * Assign a task to a project member.
     * Only ADMIN and MEMBER can assign tasks.
     */
    @PutMapping("/tasks/{id}/assign")
    public ResponseEntity<TaskResponseDTO> assignTask(
            @PathVariable Long id,
            @Valid @RequestBody AssignTaskRequestDTO request) {
        Task task = taskService.assignTask(id, request);
        TaskResponseDTO response = toResponse(task);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a task.
     * Only ADMIN and MEMBER can update tasks.
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequestDTO request) {
        Task task = taskService.updateTask(id, request);
        TaskResponseDTO response = toResponse(task);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all tasks for a project, optionally filtered by status (US10 - Dashboard).
     */
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) String status) {
        List<Task> tasks;
        
        if (status != null && !status.isBlank()) {
            try {
                TaskStatusEnum statusEnum = TaskStatusEnum.fromString(status);
                tasks = taskService.getTasksByProjectAndStatus(projectId, statusEnum);
            } catch (IllegalArgumentException e) {
                tasks = taskService.getTasksByProject(projectId);
            }
        } else {
            tasks = taskService.getTasksByProject(projectId);
        }
        
        List<TaskResponseDTO> response = tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Get task history (US12).
     * Returns all modifications made to a task, ordered by date (newest first).
     */
    @GetMapping("/tasks/{id}/history")
    public ResponseEntity<List<TaskHistoryResponseDTO>> getTaskHistory(@PathVariable Long id) {
        taskService.findById(id);
        
        List<TaskHistory> history = taskHistoryService.getTaskHistory(id);
        List<TaskHistoryResponseDTO> response = history.stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(task.getId());
        response.setProjectId(task.getProject().getId());
        response.setProjectName(task.getProject().getName());
        response.setName(task.getName());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().getStatus());
        response.setPriority(task.getPriority().getPriority());
        response.setCreatedByUserId(task.getCreatedBy().getId());
        response.setCreatedByUsername(task.getCreatedBy().getUsername());
        response.setDueDate(task.getDueDate());
        response.setCompletedDate(task.getCompletedDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        if (task.getAssignedTo() != null) {
            response.setAssignedToId(task.getAssignedTo().getId());
            response.setAssignedToUsername(task.getAssignedTo().getUsername());
        }

        return response;
    }

    private TaskHistoryResponseDTO toHistoryResponse(TaskHistory history) {
        TaskHistoryResponseDTO response = new TaskHistoryResponseDTO();
        response.setId(history.getId());
        response.setTaskId(history.getTask().getId());
        response.setTaskName(history.getTask().getName());
        response.setUserId(history.getUser().getId());
        response.setUsername(history.getUser().getUsername());
        response.setAction(history.getAction());
        response.setFieldChanged(history.getFieldChanged());
        response.setOldValue(history.getOldValue());
        response.setNewValue(history.getNewValue());
        response.setChangedAt(history.getChangedAt());
        return response;
    }
}
