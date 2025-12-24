package com.iscod.project_management_tool_back.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iscod.project_management_tool_back.datamapper.TaskMapper;
import com.iscod.project_management_tool_back.dto.task.AssignTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.CreateTaskRequestDTO;
import com.iscod.project_management_tool_back.dto.task.TaskHistoryResponseDTO;
import com.iscod.project_management_tool_back.dto.task.TaskResponseDTO;
import com.iscod.project_management_tool_back.dto.task.UpdateTaskRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.Task;
import com.iscod.project_management_tool_back.entity.TaskHistory;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskPriorityEnum;
import com.iscod.project_management_tool_back.entity.pmtenum.TaskStatusEnum;
import com.iscod.project_management_tool_back.exception.GlobalExceptionHandler;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.service.ITaskHistoryService;
import com.iscod.project_management_tool_back.service.ITaskService;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskController Tests")
class TaskControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ITaskService taskService;

    @Mock
    private ITaskHistoryService taskHistoryService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskController taskController;

    private Task testTask;
    private TaskHistory testHistory;
    private PmtUser testUser;
    private Project testProject;
    private TaskResponseDTO taskResponseDTO;
    private TaskHistoryResponseDTO historyResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setProject(testProject);
        testTask.setName("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatusEnum.TODO);
        testTask.setPriority(TaskPriorityEnum.MEDIUM);
        testTask.setCreatedBy(testUser);
        testTask.setCreatedAt(LocalDateTime.now());

        testHistory = new TaskHistory();
        testHistory.setId(1L);
        testHistory.setTask(testTask);
        testHistory.setUser(testUser);
        testHistory.setAction("CREATE");
        testHistory.setChangedAt(LocalDateTime.now());

        taskResponseDTO = new TaskResponseDTO();
        taskResponseDTO.setId(1L);
        taskResponseDTO.setProjectId(1L);
        taskResponseDTO.setProjectName("Test Project");
        taskResponseDTO.setName("Test Task");
        taskResponseDTO.setDescription("Test Description");
        taskResponseDTO.setStatus("TODO");
        taskResponseDTO.setPriority("MEDIUM");
        taskResponseDTO.setCreatedByUserId(1L);
        taskResponseDTO.setCreatedByUsername("john_doe");

        historyResponseDTO = new TaskHistoryResponseDTO();
        historyResponseDTO.setId(1L);
        historyResponseDTO.setTaskId(1L);
        historyResponseDTO.setTaskName("Test Task");
        historyResponseDTO.setUserId(1L);
        historyResponseDTO.setUsername("john_doe");
        historyResponseDTO.setAction("CREATE");
    }

    // ==================== CREATE TASK TESTS ====================

    @Test
    @DisplayName("POST /api/projects/{projectId}/tasks - should create task")
    void createTask_shouldReturnCreatedTask() throws Exception {
        CreateTaskRequestDTO request = new CreateTaskRequestDTO();
        request.setName("New Task");
        request.setDescription("New Description");
        request.setPriority("HIGH");
        request.setDueDate(LocalDate.now().plusDays(7));
        request.setCreatedByUserId(1L);

        when(taskService.createTask(eq(1L), any(CreateTaskRequestDTO.class))).thenReturn(testTask);
        when(taskMapper.toDTO(testTask)).thenReturn(taskResponseDTO);

        mockMvc.perform(post("/api/projects/1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Task"));

        verify(taskService).createTask(eq(1L), any(CreateTaskRequestDTO.class));
    }

    // ==================== GET TASK TESTS ====================

    @Test
    @DisplayName("GET /api/tasks/{id} - should return task")
    void getTask_shouldReturnTask() throws Exception {
        when(taskService.findById(1L)).thenReturn(testTask);
        when(taskMapper.toDTO(testTask)).thenReturn(taskResponseDTO);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Task"));

        verify(taskService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - should return 404 when not found")
    void getTask_shouldReturn404_whenNotFound() throws Exception {
        when(taskService.findById(99L))
            .thenThrow(new ResourceNotFoundException("Task", 99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== ASSIGN TASK TESTS ====================

    @Test
    @DisplayName("PUT /api/tasks/{id}/assign - should assign task")
    void assignTask_shouldReturnUpdatedTask() throws Exception {
        AssignTaskRequestDTO request = new AssignTaskRequestDTO();
        request.setAssigneeId(2L);

        when(taskService.assignTask(eq(1L), any(AssignTaskRequestDTO.class))).thenReturn(testTask);
        when(taskMapper.toDTO(testTask)).thenReturn(taskResponseDTO);

        mockMvc.perform(put("/api/tasks/1/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(taskService).assignTask(eq(1L), any(AssignTaskRequestDTO.class));
    }

    // ==================== UPDATE TASK TESTS ====================

    @Test
    @DisplayName("PUT /api/tasks/{id} - should update task")
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        UpdateTaskRequestDTO request = new UpdateTaskRequestDTO();
        request.setName("Updated Task");
        request.setStatus("IN_PROGRESS");

        when(taskService.updateTask(eq(1L), any(UpdateTaskRequestDTO.class))).thenReturn(testTask);
        when(taskMapper.toDTO(testTask)).thenReturn(taskResponseDTO);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(taskService).updateTask(eq(1L), any(UpdateTaskRequestDTO.class));
    }

    // ==================== GET PROJECT TASKS TESTS ====================

    @Test
    @DisplayName("GET /api/projects/{projectId}/tasks - should return tasks list")
    void getProjectTasks_shouldReturnTasksList() throws Exception {
        when(taskService.getTasksByProject(1L)).thenReturn(Arrays.asList(testTask));
        when(taskMapper.toDTO(testTask)).thenReturn(taskResponseDTO);

        mockMvc.perform(get("/api/projects/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Task"));

        verify(taskService).getTasksByProject(1L);
    }

    @Test
    @DisplayName("GET /api/projects/{projectId}/tasks?status=TODO - should return filtered tasks")
    void getProjectTasks_shouldReturnFilteredTasks() throws Exception {
        when(taskService.getTasksByProjectAndStatus(1L, TaskStatusEnum.TODO))
            .thenReturn(Arrays.asList(testTask));
        when(taskMapper.toDTO(testTask)).thenReturn(taskResponseDTO);

        mockMvc.perform(get("/api/projects/1/tasks?status=TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(taskService).getTasksByProjectAndStatus(1L, TaskStatusEnum.TODO);
    }

    // ==================== GET TASK HISTORY TESTS ====================

    @Test
    @DisplayName("GET /api/tasks/{id}/history - should return history")
    void getTaskHistory_shouldReturnHistory() throws Exception {
        when(taskService.findById(1L)).thenReturn(testTask);
        when(taskHistoryService.getTaskHistory(1L)).thenReturn(Arrays.asList(testHistory));
        when(taskMapper.toHistoryDTO(testHistory)).thenReturn(historyResponseDTO);

        mockMvc.perform(get("/api/tasks/1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].action").value("CREATE"));

        verify(taskHistoryService).getTaskHistory(1L);
    }
}
