package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;

/**
 * Service interface for Task History operations (US12).
 * Tracks all modifications made to tasks.
 */
public interface ITaskHistoryService {

    /**
     * Records a change in task history.
     *
     * @param task the task that was modified
     * @param user the user who made the change
     * @param action the type of action (CREATE, UPDATE, ASSIGN, STATUS_CHANGE, etc.)
     * @param fieldChanged the name of the field that was changed
     * @param oldValue the previous value
     * @param newValue the new value
     * @return the created TaskHistory entry
     */
    TaskHistory recordChange(Task task, PmtUser user, String action, String fieldChanged, String oldValue, String newValue);

    /**
     * Records task creation.
     */
    TaskHistory recordCreation(Task task, PmtUser creator);

    /**
     * Records task assignment.
     */
    TaskHistory recordAssignment(Task task, PmtUser assignedBy, PmtUser previousAssignee, PmtUser newAssignee);

    /**
     * Records task status change.
     */
    TaskHistory recordStatusChange(Task task, PmtUser changedBy, String oldStatus, String newStatus);

    /**
     * Gets all history entries for a task (US12).
     *
     * @param taskId the task ID
     * @return list of TaskHistory entries ordered by date (newest first)
     */
    List<TaskHistory> getTaskHistory(Long taskId);
}
