package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.exception.AuthenticationException;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;

public interface IPmtUserService {

    PmtUser login(LoginRequestDTO request) throws AuthenticationException;

    PmtUser register(RegisterRequestDTO request) throws ConflictException;

    PmtUser findById(Long id) throws ResourceNotFoundException;

    List<PmtUser> getAllUsers();
}
