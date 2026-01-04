package com.rickzzy.blog.post.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 🔒 AUTH FIRST (more specific paths first!)
                        .requestMatchers("/api/v1/posts/drafts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/posts/**").authenticated()

                        // ✅ PUBLIC LAST
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()

                        .anyRequest().authenticated()
                )


                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

