package com.iscod.project_management_tool_back.controller;

import java.time.LocalDateTime;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscod.project_management_tool_back.datamapper.PmtUserDatamapper;
import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.dto.login.UserResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.exception.AuthenticationException;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.GlobalExceptionHandler;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.service.IPmtUserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPmtUserService userService;

    @Mock
    private PmtUserDatamapper userMapper;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private PmtUser testUser;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        // Using a no-op validator to bypass validation in unit tests
        // Validation is tested separately via integration tests
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new org.springframework.validation.Validator() {
                    @Override
                    public boolean supports(Class<?> clazz) {
                        return true;
                    }
                    @Override
                    public void validate(Object target, org.springframework.validation.Errors errors) {
                        // No-op: skip validation
                    }
                })
                .build();
        objectMapper = new ObjectMapper();

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setCreatedAt(LocalDateTime.now());

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setUsername("john_doe");
        userResponseDTO.setEmail("john@example.com");
        userResponseDTO.setCreatedAt(testUser.getCreatedAt());
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("POST /api/auth/register - should register user successfully")
    void register_shouldReturnCreatedUser() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        when(userService.register(any(RegisterRequestDTO.class))).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).register(any(RegisterRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 409 when email exists")
    void register_shouldReturn409_whenEmailExists() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        when(userService.register(any(RegisterRequestDTO.class)))
            .thenThrow(new ConflictException("User", "email", "john@example.com"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("POST /api/auth/login - should login successfully")
    void login_shouldReturnUser() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        when(userService.login(any(LoginRequestDTO.class))).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"));

        verify(userService).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/login - should return 401 when credentials invalid")
    void login_shouldReturn401_whenCredentialsInvalid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("john@example.com");
        request.setPassword("WrongPass123!");

        when(userService.login(any(LoginRequestDTO.class)))
            .thenThrow(new AuthenticationException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET USER TESTS ====================

    @Test
    @DisplayName("GET /api/auth/{id} - should return user")
    void getUser_shouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);
        when(userMapper.toDTO(testUser)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/auth/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"));

        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/auth/{id} - should return 404 when not found")
    void getUser_shouldReturn404_whenNotFound() throws Exception {
        when(userService.findById(99L))
            .thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/api/auth/99"))
                .andExpect(status().isNotFound());
    }
}
