package com.rickzzy.blog.post.dtos;

import com.rickzzy.blog.post.entities.PostStatus;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private UUID id;
    private String title;
    private String content;
    private UUID categoryId;
    private Set<UUID> tagIds;
    private PostStatus status;
    private String author;
}
