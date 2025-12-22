package com.iscod.project_management_tool_back.controller;

import com.iscod.project_management_tool_back.entity.PmtUserDto;
import com.iscod.project_management_tool_back.service.IPmtUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IPmtUserService userService;

    @GetMapping
    public List<PmtUserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public PmtUserDto createUser(@RequestBody PmtUserDto user) {
        // ⚠️ Ici ton service n’a pas encore de méthode save/register
        // Il faudrait l’ajouter dans IPmtUserService et son implémentation
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PmtUserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            long userCount = userService.getAllUsers().size();
            return ResponseEntity.ok("Connexion réussie ! Nombre d'utilisateurs : " + userCount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erreur de connexion : " + e.getMessage());
        }
    }
}
