package com.iscod.project_management_tool_back.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;
import com.iscod.project_management_tool_back.repository.ITaskHistoryRepository;
import com.iscod.project_management_tool_back.service.ITaskHistoryService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of ITaskHistoryService.
 * Records all modifications made to tasks for audit and tracking purposes (US12).
 */
@Service
@RequiredArgsConstructor
public class TaskHistoryServiceImpl implements ITaskHistoryService {

    private final ITaskHistoryRepository taskHistoryRepository;

    @Override
    @Transactional
    public TaskHistory recordChange(Task task, PmtUserDto user, String action, String fieldChanged, String oldValue, String newValue) {
        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setUser(user);
        history.setAction(action);
        history.setFieldChanged(fieldChanged);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        
        return taskHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public TaskHistory recordCreation(Task task, PmtUserDto creator) {
        return recordChange(
            task, 
            creator, 
            "CREATE", 
            null, 
            null, 
            "Tâche créée: " + task.getName()
        );
    }

    @Override
    @Transactional
    public TaskHistory recordAssignment(Task task, PmtUserDto assignedBy, PmtUserDto previousAssignee, PmtUserDto newAssignee) {
        String oldValue = previousAssignee != null ? previousAssignee.getUsername() : "Non assigné";
        String newValue = newAssignee != null ? newAssignee.getUsername() : "Non assigné";
        
        return recordChange(
            task,
            assignedBy,
            "ASSIGN",
            "assignedTo",
            oldValue,
            newValue
        );
    }

    @Override
    @Transactional
    public TaskHistory recordStatusChange(Task task, PmtUserDto changedBy, String oldStatus, String newStatus) {
        return recordChange(
            task,
            changedBy,
            "STATUS_CHANGE",
            "status",
            oldStatus,
            newStatus
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskHistory> getTaskHistory(Long taskId) {
        return taskHistoryRepository.findByTaskIdOrderByChangedAtDesc(taskId);
    }
}
