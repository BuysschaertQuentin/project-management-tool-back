package com.iscod.project_management_tool_back.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.project_management_tool_back.dto.notification.NotificationResponseDTO;
import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.service.INotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    /**
     * Get all notifications for a user.
     */
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUser(userId);
        List<NotificationResponseDTO> response = notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Get unread notifications for a user.
     */
    @GetMapping("/users/{userId}/notifications/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsByUser(userId);
        List<NotificationResponseDTO> response = notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Mark a notification as read.
     */
    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(toResponse(notification));
    }

    /**
     * Mark all notifications as read for a user.
     */
    @PutMapping("/users/{userId}/notifications/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    private NotificationResponseDTO toResponse(Notification notification) {
        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId(notification.getId());
        response.setUserId(notification.getUser().getId());
        response.setUsername(notification.getUser().getUsername());
        response.setType(notification.getType().getType());
        response.setMessage(notification.getMessage());
        response.setIsRead(notification.getIsRead());
        response.setSentAt(notification.getSentAt());
        response.setReadAt(notification.getReadAt());
        
        if (notification.getTask() != null) {
            response.setTaskId(notification.getTask().getId());
            response.setTaskName(notification.getTask().getName());
        }
        
        if (notification.getProject() != null) {
            response.setProjectId(notification.getProject().getId());
            response.setProjectName(notification.getProject().getName());
        }
        
        return response;
    }
}
