package com.rickzzy.blog.category.service.Impl;

import com.rickzzy.blog.category.dtos.CategoryRequest;
import com.rickzzy.blog.category.dtos.CategoryResponse;
import com.rickzzy.blog.category.entities.Category;
import com.rickzzy.blog.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock private CategoryRepository repo;

    @InjectMocks private CategoryServiceImpl service;

    @Test
    void create_savesCategoryAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        when(repo.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(id);
            return c;
        });

        CategoryResponse response = service.create(CategoryRequest.builder().name("Java").build());

        ArgumentCaptor<Category> saved = ArgumentCaptor.forClass(Category.class);
        verify(repo).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Java");
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getName()).isEqualTo("Java");
    }

    @Test
    void getAll_mapsEveryCategory() {
        when(repo.findAll()).thenReturn(List.of(
                Category.builder().id(UUID.randomUUID()).name("Java").build(),
                Category.builder().id(UUID.randomUUID()).name("Docker").build()));

        List<CategoryResponse> result = service.getAll();

        assertThat(result).extracting(CategoryResponse::getName).containsExactly("Java", "Docker");
    }

    @Test
    void getById_returnsCategory() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.of(Category.builder().id(id).name("Java").build()));

        assertThat(service.getById(id).getName()).isEqualTo("Java");
    }

    @Test
    void getById_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id)).hasMessage("Category not found");
    }

    @Test
    void update_changesNameAndSaves() {
        UUID id = UUID.randomUUID();
        Category existing = Category.builder().id(id).name("Old name").build();
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        CategoryResponse response = service.update(id, CategoryRequest.builder().name("New name").build());

        assertThat(response.getName()).isEqualTo("New name");
        assertThat(existing.getName()).isEqualTo("New name");
    }

    @Test
    void update_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, CategoryRequest.builder().name("x").build()))
                .hasMessage("Category not found");
        verify(repo, never()).save(any());
    }

    @Test
    void delete_removesExistingCategory() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repo).deleteById(id);
    }

    @Test
    void delete_throwsWhenMissingAndDeletesNothing() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id)).hasMessage("Category not found");
        verify(repo, never()).deleteById(any());
    }
}
