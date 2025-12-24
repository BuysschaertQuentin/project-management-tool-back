package com.iscod.project_management_tool_back.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.exception.AuthenticationException;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.repository.IPmtUserRepository;
import com.iscod.project_management_tool_back.service.IPmtUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PmtUserServiceImpl implements IPmtUserService {

    private final IPmtUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PmtUser login(LoginRequestDTO request) throws AuthenticationException {
        PmtUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
        
        // TODO: In production, use BCrypt to compare hashed passwords
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        return user;
    }

    @Override
    @Transactional
    public PmtUser register(RegisterRequestDTO request) throws ConflictException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User", "email", request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("User", "username", request.getUsername());
        }
        
        PmtUser newUser = new PmtUser();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        // TODO: In production, hash password with BCrypt
        newUser.setPassword(request.getPassword());

        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public PmtUser findById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PmtUser> getAllUsers() {
        return userRepository.findAll();
    }
}
