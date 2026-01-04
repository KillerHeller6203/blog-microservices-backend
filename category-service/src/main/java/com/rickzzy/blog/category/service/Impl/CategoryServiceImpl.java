package com.rickzzy.blog.category.service.Impl;

import com.rickzzy.blog.category.dtos.CategoryRequest;
import com.rickzzy.blog.category.dtos.CategoryResponse;
import com.rickzzy.blog.category.entities.Category;
import com.rickzzy.blog.category.repository.CategoryRepository;
import com.rickzzy.blog.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();

        Category saved = repo.save(category);
        return mapToResponse(saved);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CategoryResponse getById(UUID id) {
        Category category = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToResponse(category);
    }

    @Override
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());

        Category updated = repo.save(category);
        return mapToResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        repo.deleteById(id);
    }

    // 🔁 Mapper
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
