package com.iscod.project_management_tool_back.service.impl;

import java.util.List;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.exception.BadRequestException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.service.IPmtUserService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the IPmtUserService interface.
 * Handles user authentication, registration, and retrieval operations.
 */
@Service
@RequiredArgsConstructor
public class PmtUserServiceImpl implements IPmtUserService {

    private final IPmtUserRepository userRepository;

    @Override
    public PmtUser login(LoginRequestDTO request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> user.getPassword().equals(request.getPassword()))
                .orElseThrow(() -> new AuthenticationException("Invalid email or password") {
                });
    }

    /**
     * Register a new user.
     * 
     * SECURITY NOTE: In production, the password should be hashed using BCrypt
     * before saving to database. Example:
     * user.setPassword(passwordEncoder.encode(request.getPassword()));
     */
    @Override
    public PmtUser register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        PmtUser newUser = new PmtUser();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        // SECURITY NOTE: Password should be hashed in production
        newUser.setPassword(request.getPassword());

        return userRepository.save(newUser);
    }

    @Override
    public PmtUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<PmtUser> getAllUsers() {
        return userRepository.findAll();
    }
}
