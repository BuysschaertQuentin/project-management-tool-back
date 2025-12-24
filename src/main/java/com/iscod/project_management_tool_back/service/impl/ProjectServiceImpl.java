package com.iscod.project_management_tool_back.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.iscod.project_management_tool_back.service.IProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;
    private final IPmtUserRepository userRepository;
    private final IProjectMemberRepository projectMemberRepository;

    @Override
    @Transactional
    public Project createProject(ProjectRequestDTO request) throws ResourceNotFoundException {
        PmtUser creator = userRepository.findById(request.getCreatedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getCreatedByUserId()));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setCreatedBy(creator);

        Project savedProject = projectRepository.save(project);

        ProjectMember adminMember = new ProjectMember();
        adminMember.setProject(savedProject);
        adminMember.setUser(creator);
        adminMember.setRole(ProjectRoleEnum.ADMIN);
        adminMember.setJoinedAt(LocalDateTime.now());
        projectMemberRepository.save(adminMember);

        return savedProject;
    }

    @Override
    @Transactional(readOnly = true)
    public Project findById(Long id) throws ResourceNotFoundException {
        return projectRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    @Override
    @Transactional
    public ProjectMember inviteMember(Long projectId, InviteMemberRequestDTO request) 
            throws ResourceNotFoundException, ConflictException, BadRequestException {
        Project project = findById(projectId);

        PmtUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new ConflictException("User is already a member of this project");
        }

        ProjectRoleEnum role;
        try {
            role = ProjectRoleEnum.fromString(request.getRole());
        } catch (IllegalArgumentException e) {
            role = ProjectRoleEnum.MEMBER;
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        member.setInvitedAt(LocalDateTime.now());

        return projectMemberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMember> getProjectMembers(Long projectId) throws ResourceNotFoundException {
        findById(projectId);
        return projectMemberRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional
    public ProjectMember updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleDTO request) 
            throws ResourceNotFoundException, BadRequestException {
        findById(projectId);

        ProjectMember member = projectMemberRepository.findByIdWithRelations(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectMember", memberId));

        if (!member.getProject().getId().equals(projectId)) {
            throw new BadRequestException("Member does not belong to this project");
        }

        ProjectRoleEnum newRole;
        try {
            newRole = ProjectRoleEnum.fromString(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("role", "Valid roles are: ADMIN, MEMBER, OBSERVER");
        }

        member.setRole(newRole);
        return projectMemberRepository.save(member);
    }
}
