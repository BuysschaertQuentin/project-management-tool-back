package com.iscod.project_management_tool_back.controller;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.iscod.project_management_tool_back.datamapper.NotificationMapper;
import com.iscod.project_management_tool_back.dto.notification.NotificationResponseDTO;
import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.pmtenum.NotificationTypeEnum;
import com.iscod.project_management_tool_back.exception.GlobalExceptionHandler;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.service.INotificationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationController Tests")
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private INotificationService notificationService;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationController notificationController;

    private Notification testNotification;
    private NotificationResponseDTO notificationResponseDTO;
    private PmtUser testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setProject(testProject);
        testNotification.setType(NotificationTypeEnum.TASK_ASSIGNED);
        testNotification.setMessage("You have been assigned a task");
        testNotification.setIsRead(false);
        testNotification.setSentAt(LocalDateTime.now());

        notificationResponseDTO = new NotificationResponseDTO();
        notificationResponseDTO.setId(1L);
        notificationResponseDTO.setUserId(1L);
        notificationResponseDTO.setUsername("john_doe");
        notificationResponseDTO.setProjectId(1L);
        notificationResponseDTO.setProjectName("Test Project");
        notificationResponseDTO.setType("TASK_ASSIGNED");
        notificationResponseDTO.setMessage("You have been assigned a task");
        notificationResponseDTO.setIsRead(false);
        notificationResponseDTO.setSentAt(LocalDateTime.now());
    }

    // ==================== GET USER NOTIFICATIONS TESTS ====================

    @Test
    @DisplayName("GET /api/users/{userId}/notifications - should return notifications")
    void getUserNotifications_shouldReturnNotifications() throws Exception {
        when(notificationService.getNotificationsByUser(1L))
            .thenReturn(Arrays.asList(testNotification));
        when(notificationMapper.toDTO(testNotification)).thenReturn(notificationResponseDTO);

        mockMvc.perform(get("/api/users/1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message").value("You have been assigned a task"));

        verify(notificationService).getNotificationsByUser(1L);
    }

    @Test
    @DisplayName("GET /api/users/{userId}/notifications - should return empty list")
    void getUserNotifications_shouldReturnEmptyList() throws Exception {
        when(notificationService.getNotificationsByUser(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users/1/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== GET UNREAD NOTIFICATIONS TESTS ====================

    @Test
    @DisplayName("GET /api/users/{userId}/notifications/unread - should return unread notifications")
    void getUnreadNotifications_shouldReturnUnreadNotifications() throws Exception {
        when(notificationService.getUnreadNotificationsByUser(1L))
            .thenReturn(Arrays.asList(testNotification));
        when(notificationMapper.toDTO(testNotification)).thenReturn(notificationResponseDTO);

        mockMvc.perform(get("/api/users/1/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isRead").value(false));

        verify(notificationService).getUnreadNotificationsByUser(1L);
    }

    // ==================== MARK AS READ TESTS ====================

    @Test
    @DisplayName("PUT /api/notifications/{id}/read - should mark as read")
    void markAsRead_shouldReturnUpdatedNotification() throws Exception {
        testNotification.setIsRead(true);
        testNotification.setReadAt(LocalDateTime.now());
        notificationResponseDTO.setIsRead(true);
        notificationResponseDTO.setReadAt(LocalDateTime.now());

        when(notificationService.markAsRead(1L)).thenReturn(testNotification);
        when(notificationMapper.toDTO(testNotification)).thenReturn(notificationResponseDTO);

        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isRead").value(true));

        verify(notificationService).markAsRead(1L);
    }

    @Test
    @DisplayName("PUT /api/notifications/{id}/read - should return 404 when not found")
    void markAsRead_shouldReturn404_whenNotFound() throws Exception {
        when(notificationService.markAsRead(99L))
            .thenThrow(new ResourceNotFoundException("Notification", 99L));

        mockMvc.perform(put("/api/notifications/99/read"))
                .andExpect(status().isNotFound());
    }

    // ==================== MARK ALL AS READ TESTS ====================

    @Test
    @DisplayName("PUT /api/users/{userId}/notifications/read-all - should mark all as read")
    void markAllAsRead_shouldReturn200() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1L);

        mockMvc.perform(put("/api/users/1/notifications/read-all"))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead(1L);
    }
}
