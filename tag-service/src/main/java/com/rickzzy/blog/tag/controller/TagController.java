package com.rickzzy.blog.tag.controller;

import com.rickzzy.blog.tag.dtos.CreateTagRequestDto;
import com.rickzzy.blog.tag.dtos.TagResponseDto;
import com.rickzzy.blog.tag.entities.Tag;
import com.rickzzy.blog.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService service;

    @PostMapping
    public TagResponseDto create(@Valid @RequestBody CreateTagRequestDto dto) {
        Tag tag = service.create(dto.getName());
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    @GetMapping
    public List<TagResponseDto> getAll() {
        return service.getAll().stream()
                .map(t -> TagResponseDto.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .build())
                .toList();
    }

    @GetMapping("/{id}")
    public TagResponseDto getById(@PathVariable UUID id) {
        Tag tag = service.getById(id);
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    @PostMapping("/validate")
    public void validateTags(@RequestBody List<UUID> tagIds) {
        service.validateTags(tagIds);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
