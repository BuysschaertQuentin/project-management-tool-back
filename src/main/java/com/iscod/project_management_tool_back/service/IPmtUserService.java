package com.iscod.project_management_tool_back.service;

import java.util.List;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;

public interface IPmtUserService {
    PmtUser login(LoginRequestDTO request);

    PmtUser register(RegisterRequestDTO request);

    PmtUser findById(Long id);

    List<PmtUser> getAllUsers();
}
