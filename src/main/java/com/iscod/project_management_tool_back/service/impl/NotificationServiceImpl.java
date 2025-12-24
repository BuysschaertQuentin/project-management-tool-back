package com.iscod.project_management_tool_back.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.NotificationTypeEnum;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.INotificationRepository;
import com.iscod.project_management_tool_back.service.IEmailService;
import com.iscod.project_management_tool_back.service.INotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final INotificationRepository notificationRepository;
    private final IEmailService emailService;

    @Override
    @Transactional
    public Notification notifyTaskAssignment(Task task, PmtUser assignee) {
        Notification notification = new Notification();
        notification.setUser(assignee);
        notification.setTask(task);
        notification.setProject(task.getProject());
        notification.setType(NotificationTypeEnum.TASK_ASSIGNED);
        notification.setMessage(String.format(
            "La tâche \"%s\" vous a été assignée dans le projet \"%s\"",
            task.getName(), task.getProject().getName()
        ));
        
        Notification savedNotification = notificationRepository.save(notification);
        emailService.sendTaskAssignmentEmail(assignee, task);
        
        return savedNotification;
    }

    @Override
    @Transactional
    public Notification notifyProjectInvitation(Project project, PmtUser invitee, String inviterName) {
        Notification notification = new Notification();
        notification.setUser(invitee);
        notification.setProject(project);
        notification.setType(NotificationTypeEnum.PROJECT_INVITATION);
        notification.setMessage(String.format(
            "%s vous a invité à rejoindre le projet \"%s\"",
            inviterName, project.getName()
        ));
        
        Notification savedNotification = notificationRepository.save(notification);
        emailService.sendProjectInvitationEmail(invitee, project.getName(), inviterName);
        
        return savedNotification;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderBySentAtDesc(userId);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long notificationId) throws ResourceNotFoundException {
        Notification notification = notificationRepository.findByIdWithRelations(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
