package com.iscod.project_management_tool_back.repository;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.service.impl.PmtUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PmtUserServiceImplTest {

    @Mock
    private IPmtUserRepository userRepository;

    @InjectMocks
    private PmtUserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsValid() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        PmtUserDto user = new PmtUserDto();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        PmtUserDto result = userService.login(request);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void login_ShouldThrow_WhenInvalidPassword() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("john@example.com");
        request.setPassword("wrongpass");

        PmtUserDto user = new PmtUserDto();
        user.setEmail("john@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () -> userService.login(request));
    }

    @Test
    void login_ShouldThrow_WhenEmailNotFound() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("unknown@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> userService.login(request));
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        PmtUserDto user = new PmtUserDto();
        user.setId(1L);
        user.setUsername("john");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        PmtUserDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }
}
