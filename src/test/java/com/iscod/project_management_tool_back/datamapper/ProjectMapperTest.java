package com.iscod.project_management_tool_back.datamapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.iscod.project_management_tool_back.dto.project.ProjectMemberResponseDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.entity.pmtenum.ProjectRoleEnum;

@DisplayName("ProjectMapper Tests")
class ProjectMapperTest {

    private ProjectMapper projectMapper;
    private Project testProject;
    private ProjectMember testMember;
    private PmtUser testUser;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper();

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStartDate(LocalDate.now());
        testProject.setEndDate(LocalDate.now().plusMonths(3));
        testProject.setCreatedBy(testUser);
        testProject.setCreatedAt(LocalDateTime.now());
        testProject.setUpdatedAt(LocalDateTime.now());

        testMember = new ProjectMember();
        testMember.setId(1L);
        testMember.setProject(testProject);
        testMember.setUser(testUser);
        testMember.setRole(ProjectRoleEnum.ADMIN);
        testMember.setInvitedAt(LocalDateTime.now());
        testMember.setJoinedAt(LocalDateTime.now());
    }

    // ==================== TO DTO TESTS ====================

    @Test
    @DisplayName("toDTO - should convert project to DTO")
    void toDTO_shouldConvertProjectToDTO() {
        ProjectResponseDTO result = projectMapper.toDTO(testProject);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(testProject.getStartDate(), result.getStartDate());
        assertEquals(testProject.getEndDate(), result.getEndDate());
        assertEquals(1L, result.getCreatedByUserId());
        assertEquals("john_doe", result.getCreatedByUsername());
    }

    @Test
    @DisplayName("toDTO - should return null when project is null")
    void toDTO_shouldReturnNull_whenProjectNull() {
        ProjectResponseDTO result = projectMapper.toDTO(null);

        assertNull(result);
    }

    // ==================== TO MEMBER DTO TESTS ====================

    @Test
    @DisplayName("toMemberDTO - should convert member to DTO")
    void toMemberDTO_shouldConvertMemberToDTO() {
        ProjectMemberResponseDTO result = projectMapper.toMemberDTO(testMember);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getProjectId());
        assertEquals("Test Project", result.getProjectName());
        assertEquals(1L, result.getUserId());
        assertEquals("john_doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        assertNotNull(result.getInvitedAt());
        assertNotNull(result.getJoinedAt());
    }

    @Test
    @DisplayName("toMemberDTO - should return null when member is null")
    void toMemberDTO_shouldReturnNull_whenMemberNull() {
        ProjectMemberResponseDTO result = projectMapper.toMemberDTO(null);

        assertNull(result);
    }
}
