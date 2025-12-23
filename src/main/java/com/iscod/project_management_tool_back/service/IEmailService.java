package com.iscod.project_management_tool_back.service;

import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.entity.Task;

/**
 * Service interface for email operations.
 * Handles sending notification emails to users.
 */
public interface IEmailService {

    /**
     * Sends a task assignment notification email.
     * Called when a task is assigned to a user (US11).
     *
     * @param assignee the user the task is assigned to
     * @param task the assigned task
     */
    void sendTaskAssignmentEmail(PmtUserDto assignee, Task task);

    /**
     * Sends a project invitation notification email.
     *
     * @param invitee the invited user
     * @param projectName the project name
     * @param inviterName the name of the user who sent the invitation
     */
    void sendProjectInvitationEmail(PmtUserDto invitee, String projectName, String inviterName);

    /**
     * Sends a task update notification email.
     *
     * @param user the user to notify
     * @param task the updated task
     * @param changes description of changes made
     */
    void sendTaskUpdateEmail(PmtUserDto user, Task task, String changes);
}
