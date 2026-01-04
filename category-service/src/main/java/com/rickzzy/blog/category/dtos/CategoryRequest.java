package com.rickzzy.blog.category.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryRequest {

    @NotBlank
    private String name;
    private String description;
}