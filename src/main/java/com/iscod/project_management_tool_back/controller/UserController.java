package com.iscod.project_management_tool_back.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.project_management_tool_back.datamapper.PmtUserDatamapper;
import com.iscod.project_management_tool_back.datamapper.ProjectMapper;
import com.iscod.project_management_tool_back.datamapper.TaskMapper;
import com.iscod.project_management_tool_back.dto.login.UserResponseDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectResponseDTO;
import com.iscod.project_management_tool_back.dto.task.TaskResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IProjectMemberRepository;
import com.iscod.project_management_tool_back.repository.ITaskRepository;
import com.iscod.project_management_tool_back.service.IPmtUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IPmtUserService userService;
    private final PmtUserDatamapper userMapper;
    private final IProjectMemberRepository projectMemberRepository;
    private final ITaskRepository taskRepository;
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<PmtUser> users = userService.getAllUsers();
        List<UserResponseDTO> response = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) 
            throws ResourceNotFoundException {
        PmtUser user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectResponseDTO>> getUserProjects(@PathVariable Long id) 
            throws ResourceNotFoundException {
        // Verify user exists
        userService.findById(id);
        
        List<ProjectMember> memberships = projectMemberRepository.findByUserId(id);
        List<ProjectResponseDTO> response = memberships.stream()
                .map(pm -> projectMapper.toDTO(pm.getProject()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getUserTasks(@PathVariable Long id) 
            throws ResourceNotFoundException {
        // Verify user exists
        userService.findById(id);
        
        List<Task> tasks = taskRepository.findByAssignedToId(id);
        List<TaskResponseDTO> response = tasks.stream()
                .map(taskMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
