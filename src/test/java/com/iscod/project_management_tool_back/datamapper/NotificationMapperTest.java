package com.iscod.project_management_tool_back.datamapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iscod.project_management_tool_back.dto.notification.NotificationResponseDTO;
import com.iscod.project_management_tool_back.entity.Notification;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.NotificationTypeEnum;

@DisplayName("NotificationMapper Tests")
class NotificationMapperTest {

    private NotificationMapper notificationMapper;
    private Notification testNotification;
    private PmtUser testUser;
    private Project testProject;
    private Task testTask;

    @BeforeEach
    void setUp() {
        notificationMapper = new NotificationMapper();

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Test Task");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setTask(testTask);
        testNotification.setProject(testProject);
        testNotification.setType(NotificationTypeEnum.TASK_ASSIGNED);
        testNotification.setMessage("You have been assigned a task");
        testNotification.setIsRead(false);
        testNotification.setSentAt(LocalDateTime.now());
    }

    // ==================== TO DTO TESTS ====================

    @Test
    @DisplayName("toDTO - should convert notification to DTO")
    void toDTO_shouldConvertNotificationToDTO() {
        NotificationResponseDTO result = notificationMapper.toDTO(testNotification);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("john_doe", result.getUsername());
        assertEquals(1L, result.getTaskId());
        assertEquals("Test Task", result.getTaskName());
        assertEquals(1L, result.getProjectId());
        assertEquals("Test Project", result.getProjectName());
        assertEquals("TASK_ASSIGNED", result.getType());
        assertEquals("You have been assigned a task", result.getMessage());
        assertFalse(result.getIsRead());
    }

    @Test
    @DisplayName("toDTO - should return null when notification is null")
    void toDTO_shouldReturnNull_whenNotificationNull() {
        NotificationResponseDTO result = notificationMapper.toDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("toDTO - should handle notification without task")
    void toDTO_shouldHandleNotificationWithoutTask() {
        testNotification.setTask(null);

        NotificationResponseDTO result = notificationMapper.toDTO(testNotification);

        assertNotNull(result);
        assertNull(result.getTaskId());
        assertNull(result.getTaskName());
    }

    @Test
    @DisplayName("toDTO - should handle notification without project")
    void toDTO_shouldHandleNotificationWithoutProject() {
        testNotification.setProject(null);

        NotificationResponseDTO result = notificationMapper.toDTO(testNotification);

        assertNotNull(result);
        assertNull(result.getProjectId());
        assertNull(result.getProjectName());
    }

    @Test
    @DisplayName("toDTO - should handle read notification")
    void toDTO_shouldHandleReadNotification() {
        testNotification.setIsRead(true);
        testNotification.setReadAt(LocalDateTime.now());

        NotificationResponseDTO result = notificationMapper.toDTO(testNotification);

        assertTrue(result.getIsRead());
        assertNotNull(result.getReadAt());
    }
}
