package com.rickzzy.blog.user.service.impl;

import com.rickzzy.blog.user.dtos.*;
import com.rickzzy.blog.user.entity.User;
import com.rickzzy.blog.user.repository.UserRepository;
import com.rickzzy.blog.user.security.JwtService;
import com.rickzzy.blog.user.security.UserDetailsImpl;
import com.rickzzy.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponseDto register(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists"
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        return mapToDto(userRepository.save(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetailsImpl principal =
                (UserDetailsImpl) authentication.getPrincipal();

        String token = jwtService.generateToken(principal.getUsername());
        long expiresIn = 3600;

        return AuthResponse.builder()
                .token(token)
                .expiresIn(expiresIn)
                .build();
    }


    @Override
    public UserResponseDto getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToDto(user);
    }

    private UserResponseDto mapToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
