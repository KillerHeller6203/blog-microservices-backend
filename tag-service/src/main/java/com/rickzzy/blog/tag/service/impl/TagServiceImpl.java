package com.rickzzy.blog.tag.service.impl;

import com.rickzzy.blog.tag.entities.Tag;
import com.rickzzy.blog.tag.repository.TagRepository;
import com.rickzzy.blog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository repo;

    @Override
    public Tag create(String name) {
        if (repo.existsByName(name)) {
            throw new RuntimeException("Tag already exists");
        }
        return repo.save(Tag.builder().name(name).build());
    }

    @Override
    public void validateTags(List<UUID> tagIds) {
        for (UUID id : tagIds) {
            if (!repo.existsById(id)) {
                throw new RuntimeException("Invalid tag id: " + id);
            }
        }
    }

    @Override
    public List<Tag> getAll() {
        return repo.findAll();
    }

    @Override
    public Tag getById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    @Override
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Tag not found");
        }
        repo.deleteById(id);
    }
}
