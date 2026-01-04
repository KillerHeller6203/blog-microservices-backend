package com.rickzzy.blog.post.services;

import com.rickzzy.blog.post.dtos.CreatePostRequestDto;
import com.rickzzy.blog.post.dtos.PostResponseDto;
import com.rickzzy.blog.post.entities.Post;

import java.util.List;
import java.util.UUID;

public interface PostService {

//    List<Post> getAll(UUID categoryId, UUID tagId);
    List<PostResponseDto> getAll(UUID categoryId, UUID tagId);
    List<Post> getDrafts(String author);
    Post getById(UUID id);
    Post create(CreatePostRequestDto dto, String author, String token);
    Post update(UUID id, Post updated, String username);
    void delete(UUID id, String username);
    PostResponseDto getResponseById(UUID id);
}
