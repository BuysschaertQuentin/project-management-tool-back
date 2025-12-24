package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;

/**
 * Service interface for Notification management operations.
 * Handles creating, retrieving, and managing notifications.
 */
public interface INotificationService {

    /**
     * Creates a notification when a task is assigned (US11).
     * Also sends an email to the assignee.
     *
     * @param task the assigned task
     * @param assignee the user the task is assigned to
     * @return the created Notification
     */
    Notification notifyTaskAssignment(Task task, PmtUser assignee);

    /**
     * Creates a notification when a user is invited to a project.
     *
     * @param project the project
     * @param invitee the invited user
     * @param inviterName name of the user who invited
     * @return the created Notification
     */
    Notification notifyProjectInvitation(Project project, PmtUser invitee, String inviterName);

    /**
     * Gets all notifications for a user.
     *
     * @param userId the user ID
     * @return list of Notification entities
     */
    List<Notification> getNotificationsByUser(Long userId);

    /**
     * Gets unread notifications for a user.
     *
     * @param userId the user ID  
     * @return list of unread Notification entities
     */
    List<Notification> getUnreadNotificationsByUser(Long userId);

    /**
     * Marks a notification as read.
     *
     * @param notificationId the notification ID
     * @return the updated Notification
     */
    Notification markAsRead(Long notificationId);

    /**
     * Marks all notifications as read for a user.
     *
     * @param userId the user ID
     */
    void markAllAsRead(Long userId);
}
