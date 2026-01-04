package com.rickzzy.blog.user.controller;

import com.rickzzy.blog.user.dtos.*;
import com.rickzzy.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
