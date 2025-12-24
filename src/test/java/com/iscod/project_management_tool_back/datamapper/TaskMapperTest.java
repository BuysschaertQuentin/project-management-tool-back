package com.iscod.project_management_tool_back.datamapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iscod.project_management_tool_back.dto.task.TaskHistoryResponseDTO;
import com.iscod.project_management_tool_back.dto.task.TaskResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskPriorityEnum;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;

@DisplayName("TaskMapper Tests")
class TaskMapperTest {

    private TaskMapper taskMapper;
    private Task testTask;
    private TaskHistory testHistory;
    private PmtUser testUser;
    private PmtUser assignee;
    private Project testProject;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapper();

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("creator");

        assignee = new PmtUser();
        assignee.setId(2L);
        assignee.setUsername("assignee");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setProject(testProject);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatusEnum.IN_PROGRESS);
        testTask.setPriority(TaskPriorityEnum.HIGH);
        testTask.setCreatedBy(testUser);
        testTask.setAssignedTo(assignee);
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        testHistory = new TaskHistory();
        testHistory.setId(1L);
        testHistory.setTask(testTask);
        testHistory.setUser(testUser);
        testHistory.setAction("CREATE");
        testHistory.setFieldChanged("status");
        testHistory.setOldValue("TODO");
        testHistory.setNewValue("IN_PROGRESS");
        testHistory.setChangedAt(LocalDateTime.now());
    }

    // ==================== TO DTO TESTS ====================

    @Test
    @DisplayName("toDTO - should convert task to DTO")
    void toDTO_shouldConvertTaskToDTO() {
        TaskResponseDTO result = taskMapper.toDTO(testTask);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getProjectId());
        assertEquals("Test Project", result.getProjectName());
        assertEquals("Test Task", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("HIGH", result.getPriority());
        assertEquals(1L, result.getCreatedByUserId());
        assertEquals("creator", result.getCreatedByUsername());
        assertEquals(2L, result.getAssignedToId());
        assertEquals("assignee", result.getAssignedToUsername());
    }

    @Test
    @DisplayName("toDTO - should return null when task is null")
    void toDTO_shouldReturnNull_whenTaskNull() {
        TaskResponseDTO result = taskMapper.toDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("toDTO - should handle task without assignee")
    void toDTO_shouldHandleTaskWithoutAssignee() {
        testTask.setAssignedTo(null);

        TaskResponseDTO result = taskMapper.toDTO(testTask);

        assertNotNull(result);
        assertNull(result.getAssignedToId());
        assertNull(result.getAssignedToUsername());
    }

    // ==================== TO HISTORY DTO TESTS ====================

    @Test
    @DisplayName("toHistoryDTO - should convert history to DTO")
    void toHistoryDTO_shouldConvertHistoryToDTO() {
        TaskHistoryResponseDTO result = taskMapper.toHistoryDTO(testHistory);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getTaskId());
        assertEquals("Test Task", result.getTaskName());
        assertEquals(1L, result.getUserId());
        assertEquals("creator", result.getUsername());
        assertEquals("CREATE", result.getAction());
        assertEquals("status", result.getFieldChanged());
        assertEquals("TODO", result.getOldValue());
        assertEquals("IN_PROGRESS", result.getNewValue());
    }

    @Test
    @DisplayName("toHistoryDTO - should return null when history is null")
    void toHistoryDTO_shouldReturnNull_whenHistoryNull() {
        TaskHistoryResponseDTO result = taskMapper.toHistoryDTO(null);

        assertNull(result);
    }
}
