package com.iscod.project_management_tool_back.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskPriorityEnum;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.repository.IProjectMemberRepository;
import com.iscod.project_management_tool_back.repository.ITaskRepository;
import com.iscod.project_management_tool_back.service.impl.TaskServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskServiceImplTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private IProjectService projectService;

    @Mock
    private IPmtUserRepository userRepository;

    @Mock
    private IProjectMemberRepository projectMemberRepository;

    @Mock
    private INotificationService notificationService;

    @Mock
    private ITaskHistoryService taskHistoryService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private PmtUser testUser;
    private Project testProject;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setCreatedBy(testUser);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setProject(testProject);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatusEnum.TODO);
        testTask.setPriority(TaskPriorityEnum.MEDIUM);
        testTask.setCreatedBy(testUser);
        testTask.setCreatedAt(LocalDateTime.now());
    }

    // ==================== CREATE TASK TESTS ====================

    @Test
    @DisplayName("createTask - should create task successfully")
    void createTask_shouldCreateTask() throws Exception {
        CreateTaskRequestDTO request = new CreateTaskRequestDTO();
        request.setName("New Task");
        request.setDescription("New Description");
        request.setPriority("HIGH");
        request.setDueDate(LocalDate.now().plusDays(7));
        request.setCreatedByUserId(1L);

        when(projectService.findById(1L)).thenReturn(testProject);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        Task result = taskService.createTask(1L, request);

        assertNotNull(result);
        assertEquals("New Task", result.getName());
        assertEquals(TaskPriorityEnum.HIGH, result.getPriority());
        verify(taskHistoryService).recordCreation(any(Task.class), eq(testUser));
    }

    @Test
    @DisplayName("createTask - should throw when creator not found")
    void createTask_shouldThrow_whenCreatorNotFound() throws ResourceNotFoundException {
        CreateTaskRequestDTO request = new CreateTaskRequestDTO();
        request.setCreatedByUserId(99L);

        when(projectService.findById(1L)).thenReturn(testProject);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(1L, request));
    }

    @Test
    @DisplayName("createTask - should throw when creator not project member")
    void createTask_shouldThrow_whenCreatorNotMember() throws ResourceNotFoundException {
        CreateTaskRequestDTO request = new CreateTaskRequestDTO();
        request.setCreatedByUserId(1L);
        request.setPriority("MEDIUM");

        when(projectService.findById(1L)).thenReturn(testProject);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> taskService.createTask(1L, request));
    }

    @Test
    @DisplayName("createTask - should throw when priority is invalid")
    void createTask_shouldThrow_whenPriorityInvalid() throws ResourceNotFoundException {
        CreateTaskRequestDTO request = new CreateTaskRequestDTO();
        request.setCreatedByUserId(1L);
        request.setPriority("INVALID");

        when(projectService.findById(1L)).thenReturn(testProject);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> taskService.createTask(1L, request));
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    @DisplayName("findById - should return task when exists")
    void findById_shouldReturnTask() throws ResourceNotFoundException {
        when(taskRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testTask));

        Task result = taskService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getName());
    }

    @Test
    @DisplayName("findById - should throw when not found")
    void findById_shouldThrow_whenNotFound() {
        when(taskRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.findById(99L));
    }

    // ==================== ASSIGN TASK TESTS ====================

    @Test
    @DisplayName("assignTask - should assign task successfully")
    void assignTask_shouldAssignTask() throws Exception {
        PmtUser assignee = new PmtUser();
        assignee.setId(2L);
        assignee.setUsername("assignee");

        AssignTaskRequestDTO request = new AssignTaskRequestDTO();
        request.setAssigneeId(2L);

        when(taskRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.assignTask(1L, request);

        assertNotNull(result);
        verify(notificationService).notifyTaskAssignment(any(Task.class), eq(assignee));
        verify(taskHistoryService).recordAssignment(any(), any(), any(), any());
    }

    @Test
    @DisplayName("assignTask - should throw when assignee not member")
    void assignTask_shouldThrow_whenAssigneeNotMember() throws ResourceNotFoundException {
        PmtUser assignee = new PmtUser();
        assignee.setId(2L);

        AssignTaskRequestDTO request = new AssignTaskRequestDTO();
        request.setAssigneeId(2L);

        when(taskRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> taskService.assignTask(1L, request));
    }

    // ==================== UPDATE TASK TESTS ====================

    @Test
    @DisplayName("updateTask - should update task name")
    void updateTask_shouldUpdateName() throws Exception {
        UpdateTaskRequestDTO request = new UpdateTaskRequestDTO();
        request.setName("Updated Name");

        when(taskRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.updateTask(1L, request);

        assertEquals("Updated Name", result.getName());
        verify(taskHistoryService).recordChange(any(), any(), eq("UPDATE"), eq("name"), any(), any());
    }

    @Test
    @DisplayName("updateTask - should update task status")
    void updateTask_shouldUpdateStatus() throws Exception {
        UpdateTaskRequestDTO request = new UpdateTaskRequestDTO();
        request.setStatus("IN_PROGRESS");

        when(taskRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.updateTask(1L, request);

        assertEquals(TaskStatusEnum.IN_PROGRESS, result.getStatus());
        verify(taskHistoryService).recordStatusChange(any(), any(), any(), any());
    }

    @Test
    @DisplayName("updateTask - should throw when status invalid")
    void updateTask_shouldThrow_whenStatusInvalid() throws ResourceNotFoundException {
        UpdateTaskRequestDTO request = new UpdateTaskRequestDTO();
        request.setStatus("INVALID");

        when(taskRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testTask));

        assertThrows(BadRequestException.class, () -> taskService.updateTask(1L, request));
    }

    // ==================== GET TASKS BY PROJECT TESTS ====================

    @Test
    @DisplayName("getTasksByProject - should return tasks list")
    void getTasksByProject_shouldReturnTasks() throws ResourceNotFoundException {
        when(projectService.findById(1L)).thenReturn(testProject);
        when(taskRepository.findByProjectIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(testTask));

        List<Task> result = taskService.getTasksByProject(1L);

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getName());
    }

    @Test
    @DisplayName("getTasksByProjectAndStatus - should return filtered tasks")
    void getTasksByProjectAndStatus_shouldReturnFilteredTasks() throws ResourceNotFoundException {
        when(projectService.findById(1L)).thenReturn(testProject);
        when(taskRepository.findByProjectIdAndStatus(1L, TaskStatusEnum.TODO)).thenReturn(Arrays.asList(testTask));

        List<Task> result = taskService.getTasksByProjectAndStatus(1L, TaskStatusEnum.TODO);

        assertEquals(1, result.size());
        assertEquals(TaskStatusEnum.TODO, result.get(0).getStatus());
    }

    // ==================== DELETE TASK TESTS ====================

    @Test
    @DisplayName("deleteTask - should delete task successfully")
    void deleteTask_shouldDeleteTask() throws ResourceNotFoundException {
        when(taskRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(testTask);
    }

    @Test
    @DisplayName("deleteTask - should throw when task not found")
    void deleteTask_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(99L));
    }
}
