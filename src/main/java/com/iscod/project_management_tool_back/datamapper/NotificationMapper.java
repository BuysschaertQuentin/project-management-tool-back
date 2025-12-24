package com.iscod.project_management_tool_back.datamapper;

import org.springframework.stereotype.Component;

import com.iscod.project_management_tool_back.dto.notification.NotificationResponseDTO;
import com.iscod.project_management_tool_back.entity.Notification;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toDTO(Notification notification) {
        if (notification == null) return null;
        
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setUsername(notification.getUser().getUsername());
        dto.setType(notification.getType().getType());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setSentAt(notification.getSentAt());
        dto.setReadAt(notification.getReadAt());
        
        if (notification.getTask() != null) {
            dto.setTaskId(notification.getTask().getId());
            dto.setTaskName(notification.getTask().getName());
        }
        
        if (notification.getProject() != null) {
            dto.setProjectId(notification.getProject().getId());
            dto.setProjectName(notification.getProject().getName());
        }
        
        return dto;
    }
}
