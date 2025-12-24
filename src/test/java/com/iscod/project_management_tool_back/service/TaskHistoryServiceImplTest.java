package com.iscod.project_management_tool_back.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;
import com.iscod.project_management_tool_back.repository.ITaskHistoryRepository;
import com.iscod.project_management_tool_back.service.impl.TaskHistoryServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskHistoryService Tests")
class TaskHistoryServiceImplTest {

    @Mock
    private ITaskHistoryRepository taskHistoryRepository;

    @InjectMocks
    private TaskHistoryServiceImpl taskHistoryService;

    private PmtUser testUser;
    private Task testTask;
    private TaskHistory testHistory;

    @BeforeEach
    void setUp() {
        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Test Task");

        testHistory = new TaskHistory();
        testHistory.setId(1L);
        testHistory.setTask(testTask);
        testHistory.setUser(testUser);
        testHistory.setAction("CREATE");
    }

    // ==================== RECORD CREATION TESTS ====================

    @Test
    @DisplayName("recordCreation - should save task history")
    void recordCreation_shouldSaveTaskHistory() {
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(testHistory);

        taskHistoryService.recordCreation(testTask, testUser);

        verify(taskHistoryRepository).save(any(TaskHistory.class));
    }

    // ==================== RECORD ASSIGNMENT TESTS ====================

    @Test
    @DisplayName("recordAssignment - should save assignment history")
    void recordAssignment_shouldSaveAssignmentHistory() {
        PmtUser previousAssignee = new PmtUser();
        previousAssignee.setId(2L);
        previousAssignee.setUsername("previous");

        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(testHistory);

        taskHistoryService.recordAssignment(testTask, testUser, previousAssignee, testUser);

        verify(taskHistoryRepository).save(any(TaskHistory.class));
    }

    @Test
    @DisplayName("recordAssignment - should handle null previous assignee")
    void recordAssignment_shouldHandleNullPreviousAssignee() {
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(testHistory);

        taskHistoryService.recordAssignment(testTask, testUser, null, testUser);

        verify(taskHistoryRepository).save(any(TaskHistory.class));
    }

    // ==================== RECORD STATUS CHANGE TESTS ====================

    @Test
    @DisplayName("recordStatusChange - should save status change history")
    void recordStatusChange_shouldSaveStatusChangeHistory() {
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(testHistory);

        taskHistoryService.recordStatusChange(testTask, testUser, "TODO", "IN_PROGRESS");

        verify(taskHistoryRepository).save(any(TaskHistory.class));
    }

    // ==================== RECORD CHANGE TESTS ====================

    @Test
    @DisplayName("recordChange - should save change history")
    void recordChange_shouldSaveChangeHistory() {
        when(taskHistoryRepository.save(any(TaskHistory.class))).thenReturn(testHistory);

        taskHistoryService.recordChange(testTask, testUser, "UPDATE", "name", "Old Name", "New Name");

        verify(taskHistoryRepository).save(any(TaskHistory.class));
    }

    // ==================== GET TASK HISTORY TESTS ====================

    @Test
    @DisplayName("getTaskHistory - should return history list")
    void getTaskHistory_shouldReturnHistoryList() {
        when(taskHistoryRepository.findByTaskIdOrderByChangedAtDesc(1L))
            .thenReturn(Arrays.asList(testHistory));

        List<TaskHistory> result = taskHistoryService.getTaskHistory(1L);

        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
    }

    @Test
    @DisplayName("getTaskHistory - should return empty list when no history")
    void getTaskHistory_shouldReturnEmptyList() {
        when(taskHistoryRepository.findByTaskIdOrderByChangedAtDesc(1L))
            .thenReturn(Arrays.asList());

        List<TaskHistory> result = taskHistoryService.getTaskHistory(1L);

        assertTrue(result.isEmpty());
    }
}
