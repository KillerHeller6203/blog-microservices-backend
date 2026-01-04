package com.rickzzy.blog.post.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoryDto {
    private UUID id;
    private String name;
}