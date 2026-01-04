package com.rickzzy.blog.tag.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTagRequestDto {
    @NotBlank
    private String name;
}
