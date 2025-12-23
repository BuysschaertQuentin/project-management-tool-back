package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUserDto;

public interface IPmtUserService {
    PmtUserDto login(LoginRequestDTO request);

    PmtUserDto register(RegisterRequestDTO request);

    PmtUserDto findById(Long id);

    List<PmtUserDto> getAllUsers();
}
