package com.rickzzy.blog.category.controller;

import com.rickzzy.blog.category.dtos.CategoryRequest;
import com.rickzzy.blog.category.dtos.CategoryResponse;
import com.rickzzy.blog.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity
                .status(201)
                .body(categoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {

        categoryService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
