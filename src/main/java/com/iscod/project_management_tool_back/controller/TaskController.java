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

import com.iscod.project_management_tool_back.datamapper.TaskMapper;
import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.TaskHistoryResponseDTO;
import com.iscod.project_management_tool_back.dto.task.TaskResponseDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
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
    private final TaskMapper taskMapper;

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException {
        Task task = taskService.createTask(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toDTO(task));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id) 
            throws ResourceNotFoundException {
        Task task = taskService.findById(id);
        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    @PutMapping("/tasks/{id}/assign")
    public ResponseEntity<TaskResponseDTO> assignTask(
            @PathVariable Long id,
            @Valid @RequestBody AssignTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException {
        Task task = taskService.assignTask(id, request);
        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException {
        Task task = taskService.updateTask(id, request);
        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) String status) 
            throws ResourceNotFoundException {
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
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{id}/history")
    public ResponseEntity<List<TaskHistoryResponseDTO>> getTaskHistory(@PathVariable Long id) 
            throws ResourceNotFoundException {
        taskService.findById(id);
        
        List<TaskHistory> history = taskHistoryService.getTaskHistory(id);
        List<TaskHistoryResponseDTO> response = history.stream()
                .map(taskMapper::toHistoryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
