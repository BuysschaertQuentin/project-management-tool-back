package com.iscod.project_management_tool_back.controller;

import com.iscod.project_management_tool_back.model.User;
import com.iscod.project_management_tool_back.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    @Transactional()
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    @Transactional
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    @Transactional()
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            long userCount = userRepository.count();
            return ResponseEntity.ok("Connexion réussie ! Nombre d'utilisateurs : " + userCount);
        } catch (Exception e) {
            e.printStackTrace(); // Pour voir l'erreur complète dans les logs
            return ResponseEntity.internalServerError()
                    .body("Erreur de connexion : " + e.getMessage());
        }
    }
}