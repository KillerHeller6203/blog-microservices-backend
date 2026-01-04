package com.rickzzy.blog.post.controllers;

import com.rickzzy.blog.post.dtos.CreatePostRequestDto;
import com.rickzzy.blog.post.dtos.PostDto;
import com.rickzzy.blog.post.dtos.UpdatePostRequestDto;
import com.rickzzy.blog.post.entities.Post;
import com.rickzzy.blog.post.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.rickzzy.blog.post.dtos.PostResponseDto;


import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @GetMapping
    public List<PostResponseDto> getAll(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId) {

        return service.getAll(categoryId, tagId);
//                .stream().map(this::toDto).toList();
    }

//    @GetMapping("/{id}")
//    public PostDto get(@PathVariable UUID id) {
//        return toDto(service.getById(id));
//    }

    @GetMapping("/{id}")
    public PostResponseDto get(@PathVariable UUID id) {
        return service.getResponseById(id);
    }

    @GetMapping("/drafts")
    public List<PostDto> drafts() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return service.getDrafts(username)
                .stream().map(this::toDto).toList();
    }


//    @PostMapping
//    public PostDto create(
//            @RequestHeader("Authorization") String token,
//            @Valid @RequestBody CreatePostRequestDto dto) {
//
//        String username = SecurityContextHolder.getContext()
//                .getAuthentication().getName();
//
//        Post post = Post.builder()
//                .title(dto.getTitle())
//                .content(dto.getContent())
//                .categoryId(dto.getCategoryId())
//                .tagIds(dto.getTagIds())
//                .status(dto.getStatus())
//                .author(username)
//                .build();
//
//        return toDto(service.create(post, token));
//    }
@PostMapping
public PostDto create(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody CreatePostRequestDto dto) {

    String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();

    return toDto(
            service.create(dto, username, token)
    );
}




    @PutMapping("/{id}")
    public PostDto update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePostRequestDto dto) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .categoryId(dto.getCategoryId())
                .tagIds(dto.getTagIds())
                .status(dto.getStatus())
                .build();

        return toDto(service.update(id, post, username));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        service.delete(id, username);
    }



    private PostDto toDto(Post p) {
        return PostDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .content(p.getContent())
                .categoryId(p.getCategoryId())
                .tagIds(p.getTagIds())
                .status(p.getStatus())
                .author(p.getAuthor())
                .build();
    }
}
