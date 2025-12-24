package com.iscod.project_management_tool_back.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.NotificationTypeEnum;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.INotificationRepository;
import com.iscod.project_management_tool_back.service.impl.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceImplTest {

    @Mock
    private INotificationRepository notificationRepository;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private PmtUser testUser;
    private Project testProject;
    private Task testTask;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Test Task");
        testTask.setProject(testProject);

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setTask(testTask);
        testNotification.setProject(testProject);
        testNotification.setType(NotificationTypeEnum.TASK_ASSIGNED);
        testNotification.setMessage("Test message");
        testNotification.setIsRead(false);
        testNotification.setSentAt(LocalDateTime.now());
    }

    // ==================== NOTIFY TASK ASSIGNMENT TESTS ====================

    @Test
    @DisplayName("notifyTaskAssignment - should create notification and send email")
    void notifyTaskAssignment_shouldCreateNotificationAndSendEmail() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });

        Notification result = notificationService.notifyTaskAssignment(testTask, testUser);

        assertNotNull(result);
        assertEquals(NotificationTypeEnum.TASK_ASSIGNED, result.getType());
        verify(emailService).sendTaskAssignmentEmail(testUser, testTask);
        verify(notificationRepository).save(any(Notification.class));
    }

    // ==================== NOTIFY PROJECT INVITATION TESTS ====================

    @Test
    @DisplayName("notifyProjectInvitation - should create notification and send email")
    void notifyProjectInvitation_shouldCreateNotificationAndSendEmail() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });

        Notification result = notificationService.notifyProjectInvitation(testProject, testUser, "Admin");

        assertNotNull(result);
        assertEquals(NotificationTypeEnum.PROJECT_INVITATION, result.getType());
        verify(emailService).sendProjectInvitationEmail(testUser, "Test Project", "Admin");
    }

    // ==================== GET NOTIFICATIONS BY USER TESTS ====================

    @Test
    @DisplayName("getNotificationsByUser - should return notifications list")
    void getNotificationsByUser_shouldReturnNotificationsList() {
        when(notificationRepository.findByUserIdOrderBySentAtDesc(1L))
            .thenReturn(Arrays.asList(testNotification));

        List<Notification> result = notificationService.getNotificationsByUser(1L);

        assertEquals(1, result.size());
        assertEquals(testNotification.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("getNotificationsByUser - should return empty list when no notifications")
    void getNotificationsByUser_shouldReturnEmptyList() {
        when(notificationRepository.findByUserIdOrderBySentAtDesc(1L)).thenReturn(Arrays.asList());

        List<Notification> result = notificationService.getNotificationsByUser(1L);

        assertTrue(result.isEmpty());
    }

    // ==================== GET UNREAD NOTIFICATIONS TESTS ====================

    @Test
    @DisplayName("getUnreadNotificationsByUser - should return unread notifications")
    void getUnreadNotificationsByUser_shouldReturnUnreadNotifications() {
        when(notificationRepository.findByUserIdAndIsReadFalseOrderBySentAtDesc(1L))
            .thenReturn(Arrays.asList(testNotification));

        List<Notification> result = notificationService.getUnreadNotificationsByUser(1L);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getIsRead());
    }

    // ==================== MARK AS READ TESTS ====================

    @Test
    @DisplayName("markAsRead - should mark notification as read")
    void markAsRead_shouldMarkNotificationAsRead() throws ResourceNotFoundException {
        when(notificationRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        Notification result = notificationService.markAsRead(1L);

        assertTrue(result.getIsRead());
        assertNotNull(result.getReadAt());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("markAsRead - should throw when notification not found")
    void markAsRead_shouldThrow_whenNotFound() {
        when(notificationRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(99L));
    }

    // ==================== MARK ALL AS READ TESTS ====================

    @Test
    @DisplayName("markAllAsRead - should call repository method")
    void markAllAsRead_shouldCallRepository() {
        notificationService.markAllAsRead(1L);

        verify(notificationRepository).markAllAsReadByUserId(1L);
    }
}
