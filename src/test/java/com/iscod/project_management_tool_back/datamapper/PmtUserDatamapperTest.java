package com.iscod.project_management_tool_back.datamapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iscod.project_management_tool_back.dto.login.UserResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;

@DisplayName("PmtUserDatamapper Tests")
class PmtUserDatamapperTest {

    private PmtUserDatamapper userMapper;
    private PmtUser testUser;

    @BeforeEach
    void setUp() {
        userMapper = new PmtUserDatamapper();

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("toDTO - should convert user to DTO")
    void toDTO_shouldConvertUserToDTO() {
        UserResponseDTO result = userMapper.toDTO(testUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john_doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    @DisplayName("toDTO - should return null when user is null")
    void toDTO_shouldReturnNull_whenUserNull() {
        UserResponseDTO result = userMapper.toDTO(null);

        assertNull(result);
    }
}
