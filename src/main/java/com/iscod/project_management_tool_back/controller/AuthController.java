package com.iscod.project_management_tool_back.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.project_management_tool_back.datamapper.PmtUserDatamapper;
import com.iscod.project_management_tool_back.dto.login.LoginRequestDTO;
import com.iscod.project_management_tool_back.dto.login.RegisterRequestDTO;
import com.iscod.project_management_tool_back.dto.login.UserResponseDTO;
import com.iscod.project_management_tool_back.entity.PmtUser;
import com.iscod.project_management_tool_back.exception.AuthenticationException;
import com.iscod.project_management_tool_back.exception.ConflictException;
import com.iscod.project_management_tool_back.exception.ResourceNotFoundException;
import com.iscod.project_management_tool_back.service.IPmtUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final IPmtUserService userService;
    private final PmtUserDatamapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) 
            throws ConflictException {
        PmtUser user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) 
            throws AuthenticationException {
        PmtUser user = userService.login(request);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) 
            throws ResourceNotFoundException {
        PmtUser user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }
}
