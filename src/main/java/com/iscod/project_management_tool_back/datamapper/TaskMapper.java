package com.iscod.project_management_tool_back.datamapper;

import org.springframework.stereotype.Component;

import com.iscod.project_management_tool_back.dto.task.TaskHistoryResponseDTO;
import com.iscod.project_management_tool_back.dto.task.TaskResponseDTO;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;

@Component
public class TaskMapper {

    public TaskResponseDTO toDTO(Task task) {
        if (task == null) return null;
        
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setProjectId(task.getProject().getId());
        dto.setProjectName(task.getProject().getName());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().getStatus());
        dto.setPriority(task.getPriority().getPriority());
        dto.setCreatedByUserId(task.getCreatedBy().getId());
        dto.setCreatedByUsername(task.getCreatedBy().getUsername());
        dto.setDueDate(task.getDueDate());
        dto.setCompletedDate(task.getCompletedDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            dto.setAssignedToUsername(task.getAssignedTo().getUsername());
        }

        return dto;
    }

    public TaskHistoryResponseDTO toHistoryDTO(TaskHistory history) {
        if (history == null) return null;
        
        TaskHistoryResponseDTO dto = new TaskHistoryResponseDTO();
        dto.setId(history.getId());
        dto.setTaskId(history.getTask().getId());
        dto.setTaskName(history.getTask().getName());
        dto.setUserId(history.getUser().getId());
        dto.setUsername(history.getUser().getUsername());
        dto.setAction(history.getAction());
        dto.setFieldChanged(history.getFieldChanged());
        dto.setOldValue(history.getOldValue());
        dto.setNewValue(history.getNewValue());
        dto.setChangedAt(history.getChangedAt());
        return dto;
    }
}
