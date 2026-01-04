package com.rickzzy.blog.category.repository;

import com.rickzzy.blog.category.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByName(String name);
}
