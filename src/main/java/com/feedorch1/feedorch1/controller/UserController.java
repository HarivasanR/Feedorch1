package com.feedorch1.feedorch1.controller;

import com.feedorch1.feedorch1.model.User;
import com.feedorch1.feedorch1.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // POST http://localhost:8080/api/users
    @PostMapping
public ResponseEntity<String> createUser(@RequestBody User user) {
        // 🔥 BYPASS HIBERNATE STATE ENGINE ENTIRELY
        userRepository.insertUserManually(user.getId(), user.getUsername(), user.isCelebrity());
        return ResponseEntity.ok("User created successfully via native SQL");
    }

    // GET http://localhost:8080/api/users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}