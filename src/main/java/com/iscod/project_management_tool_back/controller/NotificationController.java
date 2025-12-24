package com.iscod.project_management_tool_back.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.project_management_tool_back.datamapper.NotificationMapper;
import com.iscod.project_management_tool_back.dto.notification.NotificationResponseDTO;
import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.service.INotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUser(userId);
        List<NotificationResponseDTO> response = notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/notifications/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsByUser(userId);
        List<NotificationResponseDTO> response = notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notificationMapper.toDTO(notification));
    }

    @PutMapping("/users/{userId}/notifications/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
