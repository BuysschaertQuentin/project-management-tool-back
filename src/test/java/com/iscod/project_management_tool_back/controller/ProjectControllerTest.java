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
import com.iscod.project_management_tool_back.datamapper.ProjectMapper;
import com.iscod.project_management_tool_back.dto.project.InviteMemberRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectMemberResponseDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectResponseDTO;
import com.iscod.project_management_tool_back.dto.project.UpdateMemberRoleDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.entity.pmtenum.ProjectRoleEnum;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.GlobalExceptionHandler;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.service.IProjectService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectController Tests")
class ProjectControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IProjectService projectService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectController projectController;

    private Project testProject;
    private ProjectMember testMember;
    private PmtUser testUser;
    private ProjectResponseDTO projectResponseDTO;
    private ProjectMemberResponseDTO memberResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testUser = new PmtUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStartDate(LocalDate.now());
        testProject.setCreatedBy(testUser);
        testProject.setCreatedAt(LocalDateTime.now());

        testMember = new ProjectMember();
        testMember.setId(1L);
        testMember.setProject(testProject);
        testMember.setUser(testUser);
        testMember.setRole(ProjectRoleEnum.ADMIN);

        projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setId(1L);
        projectResponseDTO.setName("Test Project");
        projectResponseDTO.setDescription("Test Description");
        projectResponseDTO.setStartDate(LocalDate.now());
        projectResponseDTO.setCreatedByUserId(1L);
        projectResponseDTO.setCreatedByUsername("john_doe");

        memberResponseDTO = new ProjectMemberResponseDTO();
        memberResponseDTO.setId(1L);
        memberResponseDTO.setProjectId(1L);
        memberResponseDTO.setUserId(1L);
        memberResponseDTO.setUsername("john_doe");
        memberResponseDTO.setRole("ADMIN");
    }

    // ==================== CREATE PROJECT TESTS ====================

    @Test
    @DisplayName("POST /api/projects - should create project")
    void createProject_shouldReturnCreatedProject() throws Exception {
        ProjectRequestDTO request = new ProjectRequestDTO();
        request.setName("New Project");
        request.setDescription("New Description");
        request.setStartDate(LocalDate.now());
        request.setCreatedByUserId(1L);

        when(projectService.createProject(any(ProjectRequestDTO.class))).thenReturn(testProject);
        when(projectMapper.toDTO(testProject)).thenReturn(projectResponseDTO);

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Project"));

        verify(projectService).createProject(any(ProjectRequestDTO.class));
    }

    // ==================== GET PROJECT TESTS ====================

    @Test
    @DisplayName("GET /api/projects/{id} - should return project")
    void getProject_shouldReturnProject() throws Exception {
        when(projectService.findById(1L)).thenReturn(testProject);
        when(projectMapper.toDTO(testProject)).thenReturn(projectResponseDTO);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Project"));

        verify(projectService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/projects/{id} - should return 404 when not found")
    void getProject_shouldReturn404_whenNotFound() throws Exception {
        when(projectService.findById(99L))
            .thenThrow(new ResourceNotFoundException("Project", 99L));

        mockMvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== INVITE MEMBER TESTS ====================

    @Test
    @DisplayName("POST /api/projects/{id}/members - should invite member")
    void inviteMember_shouldReturnCreatedMember() throws Exception {
        InviteMemberRequestDTO request = new InviteMemberRequestDTO();
        request.setEmail("invitee@example.com");
        request.setRole("MEMBER");

        when(projectService.inviteMember(eq(1L), any(InviteMemberRequestDTO.class))).thenReturn(testMember);
        when(projectMapper.toMemberDTO(testMember)).thenReturn(memberResponseDTO);

        mockMvc.perform(post("/api/projects/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(projectService).inviteMember(eq(1L), any(InviteMemberRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/projects/{id}/members - should return 409 when already member")
    void inviteMember_shouldReturn409_whenAlreadyMember() throws Exception {
        InviteMemberRequestDTO request = new InviteMemberRequestDTO();
        request.setEmail("john@example.com");
        request.setRole("MEMBER");

        when(projectService.inviteMember(eq(1L), any(InviteMemberRequestDTO.class)))
            .thenThrow(new ConflictException("User is already a member"));

        mockMvc.perform(post("/api/projects/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ==================== GET PROJECT MEMBERS TESTS ====================

    @Test
    @DisplayName("GET /api/projects/{id}/members - should return members list")
    void getProjectMembers_shouldReturnMembersList() throws Exception {
        when(projectService.getProjectMembers(1L)).thenReturn(Arrays.asList(testMember));
        when(projectMapper.toMemberDTO(testMember)).thenReturn(memberResponseDTO);

        mockMvc.perform(get("/api/projects/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("john_doe"));

        verify(projectService).getProjectMembers(1L);
    }

    // ==================== UPDATE MEMBER ROLE TESTS ====================

    @Test
    @DisplayName("PUT /api/projects/{id}/members/{memberId}/role - should update role")
    void updateMemberRole_shouldReturnUpdatedMember() throws Exception {
        UpdateMemberRoleDTO request = new UpdateMemberRoleDTO();
        request.setRole("OBSERVER");

        when(projectService.updateMemberRole(eq(1L), eq(1L), any(UpdateMemberRoleDTO.class)))
            .thenReturn(testMember);
        when(projectMapper.toMemberDTO(testMember)).thenReturn(memberResponseDTO);

        mockMvc.perform(put("/api/projects/1/members/1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(projectService).updateMemberRole(eq(1L), eq(1L), any(UpdateMemberRoleDTO.class));
    }
}
