package com.rickzzy.blog.post.dtos;

import com.rickzzy.blog.post.entities.PostStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PostResponseDto {

    private UUID id;
    private String title;
    private String content;
    private PostStatus status;

    private AuthorDto author;
    private CategoryDto category;
    private List<TagDto> tags;

    private Instant createdAt;
    private Instant updatedAt;
    private Integer readingTime;
    private boolean editable;
}
