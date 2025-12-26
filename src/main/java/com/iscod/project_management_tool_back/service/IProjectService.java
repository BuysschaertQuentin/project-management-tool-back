package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.dto.project.InviteMemberRequestDTO;
import com.iscod.project_management_tool_back.dto.project.ProjectRequestDTO;
import com.iscod.project_management_tool_back.dto.project.UpdateMemberRoleDTO;
import com.iscod.project_management_tool_back.entity.Project;
import com.iscod.project_management_tool_back.entity.ProjectMember;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;

public interface IProjectService {

    Project createProject(ProjectRequestDTO request) throws ResourceNotFoundException;

    Project findById(Long id) throws ResourceNotFoundException;

    void deleteProject(Long id) throws ResourceNotFoundException;

    ProjectMember inviteMember(Long projectId, InviteMemberRequestDTO request) 
            throws ResourceNotFoundException, ConflictException, BadRequestException;

    List<ProjectMember> getProjectMembers(Long projectId) throws ResourceNotFoundException;

    ProjectMember updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleDTO request) 
            throws ResourceNotFoundException, BadRequestException;

    void removeMember(Long projectId, Long memberId) 
            throws ResourceNotFoundException, BadRequestException;
}
