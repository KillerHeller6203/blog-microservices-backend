package com.rickzzy.blog.user.service.impl;

import com.rickzzy.blog.user.dtos.AuthResponse;
import com.rickzzy.blog.user.dtos.CreateUserRequest;
import com.rickzzy.blog.user.dtos.LoginRequest;
import com.rickzzy.blog.user.dtos.UserResponseDto;
import com.rickzzy.blog.user.entity.User;
import com.rickzzy.blog.user.repository.UserRepository;
import com.rickzzy.blog.user.security.JwtService;
import com.rickzzy.blog.user.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private UserServiceImpl userService;

    private CreateUserRequest registerRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Rick");
        request.setEmail("rick@example.com");
        request.setPassword("plain-password");
        return request;
    }

    @Test
    void register_hashesPasswordAndSavesUser() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByEmail("rick@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(id);
            return u;
        });

        UserResponseDto response = userService.register(registerRequest());

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isEqualTo("hashed-password");
        assertThat(saved.getValue().getPassword()).isNotEqualTo("plain-password");

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getEmail()).isEqualTo("rick@example.com");
        assertThat(response.getName()).isEqualTo("Rick");
    }

    @Test
    void register_rejectsDuplicateEmailWith409() {
        when(userRepository.findByEmail("rick@example.com"))
                .thenReturn(Optional.of(User.builder().email("rick@example.com").build()));

        assertThatThrownBy(() -> userService.register(registerRequest()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_returnsJwtWithOneHourExpiry() {
        User user = User.builder().email("rick@example.com").password("hashed").name("Rick").build();
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(new UserDetailsImpl(user), null, List.of()));
        when(jwtService.generateToken("rick@example.com")).thenReturn("signed.jwt.token");

        AuthResponse response = userService.login(new LoginRequest("rick@example.com", "plain-password"));

        assertThat(response.getToken()).isEqualTo("signed.jwt.token");
        assertThat(response.getExpiresIn()).isEqualTo(3600);
    }

    @Test
    void login_withBadCredentials_doesNotIssueToken() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> userService.login(new LoginRequest("rick@example.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtService);
    }

    @Test
    void getUserById_returnsUserWithoutPassword() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(
                User.builder().id(id).name("Rick").email("rick@example.com").password("hashed").build()));

        UserResponseDto response = userService.getUserById(id);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Rick");
        assertThat(response.getEmail()).isEqualTo("rick@example.com");
    }

    @Test
    void getUserById_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(id))
                .hasMessage("User not found");
    }
}
