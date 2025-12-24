package com.iscod.project_management_tool_back.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.exception.AuthenticationException;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.service.impl.PmtUserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("PmtUserService Tests")
class PmtUserServiceImplTest {

    @Mock
    private IPmtUserRepository userRepository;

    @InjectMocks
    private PmtUserServiceImpl userService;

    private PmtUser testUser;
    private LoginRequestDTO loginRequest;
    private RegisterRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("new_user");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("newpassword123");
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("login - should return user when credentials are valid")
    void login_shouldReturnUser_whenCredentialsValid() throws AuthenticationException {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        PmtUser result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        assertEquals("john_doe", result.getUsername());
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("login - should throw AuthenticationException when email not found")
    void login_shouldThrow_whenEmailNotFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> userService.login(loginRequest));
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("login - should throw AuthenticationException when password is wrong")
    void login_shouldThrow_whenPasswordWrong() {
        loginRequest.setPassword("wrongpassword");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(AuthenticationException.class, () -> userService.login(loginRequest));
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("register - should create user when data is valid")
    void register_shouldCreateUser_whenDataValid() throws ConflictException {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("new_user")).thenReturn(false);
        when(userRepository.save(any(PmtUser.class))).thenAnswer(invocation -> {
            PmtUser user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        PmtUser result = userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("new_user", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).save(any(PmtUser.class));
    }

    @Test
    @DisplayName("register - should throw ConflictException when email exists")
    void register_shouldThrow_whenEmailExists() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register - should throw ConflictException when username exists")
    void register_shouldThrow_whenUsernameExists() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("new_user")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    @DisplayName("findById - should return user when exists")
    void findById_shouldReturnUser_whenExists() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        PmtUser result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john_doe", result.getUsername());
    }

    @Test
    @DisplayName("findById - should throw ResourceNotFoundException when not found")
    void findById_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    // ==================== GET ALL USERS TESTS ====================

    @Test
    @DisplayName("getAllUsers - should return all users")
    void getAllUsers_shouldReturnAllUsers() {
        PmtUser user2 = new PmtUser();
        user2.setId(2L);
        user2.setUsername("jane_doe");
        
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<PmtUser> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("getAllUsers - should return empty list when no users")
    void getAllUsers_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<PmtUser> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }
}
