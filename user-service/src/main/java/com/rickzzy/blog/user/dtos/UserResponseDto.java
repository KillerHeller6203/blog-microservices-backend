package com.rickzzy.blog.user.dtos;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponseDto {
    private UUID id;
    private String name;
    private String email;
}
