package com.rickzzy.blog.post.dtos;

import com.rickzzy.blog.post.entities.PostStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CreatePostRequestDto {

    @NotBlank
    @Size(min = 5)
    private String title;

    @Size(min = 10, max = 50000)
    private String content;

    @NotNull
    private UUID categoryId;

    @Builder.Default
    private Set<UUID> tagIds = new HashSet<>();

    @NotNull
    private PostStatus status;
}
