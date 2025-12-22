package com.iscod.project_management_tool_back.service;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUserDto;

import java.util.List;

public interface IPmtUserService {
    PmtUserDto login(LoginRequestDTO request);

    PmtUserDto findById(Long id);

    List<PmtUserDto> getAllUsers();
}
