package com.rickzzy.blog.category.service;

import com.rickzzy.blog.category.dtos.CategoryRequest;
import com.rickzzy.blog.category.dtos.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);
    List<CategoryResponse> getAll();
    CategoryResponse getById(UUID id);
    CategoryResponse update(UUID id, CategoryRequest request);
    void delete(UUID id);
}
