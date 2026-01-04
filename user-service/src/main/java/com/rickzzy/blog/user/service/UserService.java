package com.rickzzy.blog.user.service;

import com.rickzzy.blog.user.dtos.AuthResponse;
import com.rickzzy.blog.user.dtos.CreateUserRequest;
import com.rickzzy.blog.user.dtos.LoginRequest;
import com.rickzzy.blog.user.dtos.UserResponseDto;

import java.util.UUID;

public interface UserService {

    UserResponseDto register(CreateUserRequest request);

    AuthResponse login(LoginRequest request);

    UserResponseDto getUserById(UUID id);
}
