package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;

public interface INotificationService {

    Notification notifyTaskAssignment(Task task, PmtUser assignee);

    Notification notifyProjectInvitation(Project project, PmtUser invitee, String inviterName);

    List<Notification> getNotificationsByUser(Long userId);

    List<Notification> getUnreadNotificationsByUser(Long userId);

    Notification markAsRead(Long notificationId) throws ResourceNotFoundException;

    void markAllAsRead(Long userId);
}
