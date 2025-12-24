package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;

public interface ITaskService {

    Task createTask(Long projectId, CreateTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException;

    Task findById(Long id) throws ResourceNotFoundException;

    Task assignTask(Long taskId, AssignTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException;

    Task updateTask(Long taskId, UpdateTaskRequestDTO request) 
            throws ResourceNotFoundException, BadRequestException;

    List<Task> getTasksByProject(Long projectId) throws ResourceNotFoundException;

    List<Task> getTasksByProjectAndStatus(Long projectId, TaskStatusEnum status) 
            throws ResourceNotFoundException;
}
