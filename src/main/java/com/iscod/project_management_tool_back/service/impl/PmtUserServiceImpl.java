package com.iscod.project_management_tool_back.service.impl;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.service.IPmtUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PmtUserServiceImpl implements IPmtUserService {

    private final IPmtUserRepository userRepository;

    @Override
    public PmtUserDto login(LoginRequestDTO request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .orElseThrow(() -> new AuthenticationException("Invalid email or password") {
                });
    }

    @Override
    public PmtUserDto findById(Long id) {
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PmtUserDto> getAllUsers() {
        return userRepository.findAll();
    }
}