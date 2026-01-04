package com.rickzzy.blog.tag.service;

import com.rickzzy.blog.tag.entities.Tag;

import java.util.List;
import java.util.UUID;

public interface TagService {
    Tag create(String name);
    List<Tag> getAll();
    Tag getById(UUID id);
    void validateTags(List<UUID> tagIds);
    void delete(UUID id);
}
