package com.rickzzy.blog.tag.repository;

import com.rickzzy.blog.tag.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    boolean existsByName(String name);
}
