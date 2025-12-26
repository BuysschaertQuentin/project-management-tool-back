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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.iscod.project_management_tool_back.dto.project.InviteMemberRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.dto.project.UpdateMemberRoleDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.entity.pmtenum.ProjectRoleEnum;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.repository.IProjectMemberRepository;
import com.iscod.project_management_tool_back.repository.IProjectRepository;
import com.iscod.project_management_tool_back.service.impl.ProjectServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Tests")
class ProjectServiceImplTest {

    @Mock
    private IProjectRepository projectRepository;

    @Mock
    private IPmtUserRepository userRepository;

    @Mock
    private IProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private PmtUser testUser;
    private Project testProject;
    private ProjectMember testMember;
    private ProjectRequestDTO projectRequest;

    @BeforeEach
    void setUp() {
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

        projectRequest = new ProjectRequestDTO();
        projectRequest.setName("New Project");
        projectRequest.setDescription("New Description");
        projectRequest.setStartDate(LocalDate.now());
        projectRequest.setCreatedByUserId(1L);
    }

    // ==================== CREATE PROJECT TESTS ====================

    @Test
    @DisplayName("createProject - should create project and add creator as admin")
    void createProject_shouldCreateProjectAndAddCreatorAsAdmin() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(testMember);

        Project result = projectService.createProject(projectRequest);

        assertNotNull(result);
        assertEquals("New Project", result.getName());
        verify(projectRepository).save(any(Project.class));
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    @DisplayName("createProject - should throw when creator not found")
    void createProject_shouldThrow_whenCreatorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.createProject(projectRequest));
        verify(projectRepository, never()).save(any());
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    @DisplayName("findById - should return project when exists")
    void findById_shouldReturnProject_whenExists() throws ResourceNotFoundException {
        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));

        Project result = projectService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getName());
    }

    @Test
    @DisplayName("findById - should throw when not found")
    void findById_shouldThrow_whenNotFound() {
        when(projectRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.findById(99L));
    }

    // ==================== INVITE MEMBER TESTS ====================

    @Test
    @DisplayName("inviteMember - should add member to project")
    void inviteMember_shouldAddMemberToProject() throws Exception {
        PmtUser invitee = new PmtUser();
        invitee.setId(2L);
        invitee.setEmail("invitee@example.com");

        InviteMemberRequestDTO request = new InviteMemberRequestDTO();
        request.setEmail("invitee@example.com");
        request.setRole("MEMBER");

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByEmail("invitee@example.com")).thenReturn(Optional.of(invitee));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L)).thenReturn(false);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(invocation -> {
            ProjectMember m = invocation.getArgument(0);
            m.setId(2L);
            return m;
        });

        ProjectMember result = projectService.inviteMember(1L, request);

        assertNotNull(result);
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    @DisplayName("inviteMember - should throw when user not found")
    void inviteMember_shouldThrow_whenUserNotFound() {
        InviteMemberRequestDTO request = new InviteMemberRequestDTO();
        request.setEmail("unknown@example.com");

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.inviteMember(1L, request));
    }

    @Test
    @DisplayName("inviteMember - should throw when user already member")
    void inviteMember_shouldThrow_whenUserAlreadyMember() {
        InviteMemberRequestDTO request = new InviteMemberRequestDTO();
        request.setEmail("john@example.com");

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> projectService.inviteMember(1L, request));
    }

    // ==================== GET PROJECT MEMBERS TESTS ====================

    @Test
    @DisplayName("getProjectMembers - should return members list")
    void getProjectMembers_shouldReturnMembersList() throws ResourceNotFoundException {
        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByProjectId(1L)).thenReturn(Arrays.asList(testMember));

        List<ProjectMember> result = projectService.getProjectMembers(1L);

        assertEquals(1, result.size());
        assertEquals(testMember.getId(), result.get(0).getId());
    }

    // ==================== UPDATE MEMBER ROLE TESTS ====================

    @Test
    @DisplayName("updateMemberRole - should update role successfully")
    void updateMemberRole_shouldUpdateRole() throws Exception {
        UpdateMemberRoleDTO request = new UpdateMemberRoleDTO();
        request.setRole("OBSERVER");

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testMember));
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(testMember);

        ProjectMember result = projectService.updateMemberRole(1L, 1L, request);

        assertNotNull(result);
        assertEquals(ProjectRoleEnum.OBSERVER, result.getRole());
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    @DisplayName("updateMemberRole - should throw when member not found")
    void updateMemberRole_shouldThrow_whenMemberNotFound() {
        UpdateMemberRoleDTO request = new UpdateMemberRoleDTO();
        request.setRole("MEMBER");

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> projectService.updateMemberRole(1L, 99L, request));
    }

    @Test
    @DisplayName("updateMemberRole - should throw when member not in project")
    void updateMemberRole_shouldThrow_whenMemberNotInProject() {
        Project otherProject = new Project();
        otherProject.setId(2L);
        testMember.setProject(otherProject);

        UpdateMemberRoleDTO request = new UpdateMemberRoleDTO();
        request.setRole("MEMBER");

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testMember));

        assertThrows(BadRequestException.class, 
            () -> projectService.updateMemberRole(1L, 1L, request));
    }

    @Test
    @DisplayName("updateMemberRole - should throw when role is invalid")
    void updateMemberRole_shouldThrow_whenRoleInvalid() {
        UpdateMemberRoleDTO request = new UpdateMemberRoleDTO();
        request.setRole("INVALID_ROLE");

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testMember));

        assertThrows(BadRequestException.class, 
            () -> projectService.updateMemberRole(1L, 1L, request));
    }

    // ==================== DELETE PROJECT TESTS ====================

    @Test
    @DisplayName("deleteProject - should delete project and members")
    void deleteProject_shouldDeleteProjectAndMembers() throws ResourceNotFoundException {
        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByProjectId(1L)).thenReturn(Arrays.asList(testMember));

        projectService.deleteProject(1L);

        verify(projectMemberRepository).deleteAll(any());
        verify(projectRepository).delete(testProject);
    }

    @Test
    @DisplayName("deleteProject - should throw when project not found")
    void deleteProject_shouldThrow_whenProjectNotFound() {
        when(projectRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(99L));
        verify(projectRepository, never()).delete(any());
    }

    // ==================== REMOVE MEMBER TESTS ====================

    @Test
    @DisplayName("removeMember - should remove member from project")
    void removeMember_shouldRemoveMember() throws Exception {
        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testMember));

        projectService.removeMember(1L, 1L);

        verify(projectMemberRepository).delete(testMember);
    }

    @Test
    @DisplayName("removeMember - should throw when member not found")
    void removeMember_shouldThrow_whenMemberNotFound() {
        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> projectService.removeMember(1L, 99L));
        verify(projectMemberRepository, never()).delete(any(ProjectMember.class));
    }

    @Test
    @DisplayName("removeMember - should throw when member not in project")
    void removeMember_shouldThrow_whenMemberNotInProject() {
        Project otherProject = new Project();
        otherProject.setId(2L);
        testMember.setProject(otherProject);

        when(projectRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testProject));
        when(projectMemberRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testMember));

        assertThrows(BadRequestException.class, 
            () -> projectService.removeMember(1L, 1L));
        verify(projectMemberRepository, never()).delete(any(ProjectMember.class));
    }
}
