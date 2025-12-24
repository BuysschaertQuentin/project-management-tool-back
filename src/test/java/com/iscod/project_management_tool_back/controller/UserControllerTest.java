package com.iscod.project_management_tool_back.controller;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.iscod.project_management_tool_back.datamapper.PmtUserDatamapper;
import com.iscod.project_management_tool_back.dto.login.UserResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.exception.GlobalExceptionHandler;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.service.IPmtUserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPmtUserService userService;

    @Mock
    private PmtUserDatamapper userMapper;

    @InjectMocks
    private UserController userController;

    private PmtUser testUser;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setCreatedAt(LocalDateTime.now());

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setUsername("john_doe");
        userResponseDTO.setEmail("john@example.com");
        userResponseDTO.setCreatedAt(testUser.getCreatedAt());
    }

    // ==================== GET ALL USERS TESTS ====================

    @Test
    @DisplayName("GET /api/users - should return all users")
    void getAllUsers_shouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("john_doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("GET /api/users - should return empty list")
    void getAllUsers_shouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== GET USER BY ID TESTS ====================

    @Test
    @DisplayName("GET /api/users/{id} - should return user")
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - should return 404 when not found")
    void getUserById_shouldReturn404_whenNotFound() throws Exception {
        when(userService.findById(99L))
            .thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
