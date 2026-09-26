package com.rickzzy.blog.tag.service.impl;

import com.rickzzy.blog.tag.entities.Tag;
import com.rickzzy.blog.tag.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock private TagRepository repo;

    @InjectMocks private TagServiceImpl service;

    @Test
    void create_savesNewTag() {
        when(repo.existsByName("spring")).thenReturn(false);
        when(repo.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        Tag tag = service.create("spring");

        assertThat(tag.getName()).isEqualTo("spring");
        verify(repo).save(any(Tag.class));
    }

    @Test
    void create_rejectsDuplicateName() {
        when(repo.existsByName("spring")).thenReturn(true);

        assertThatThrownBy(() -> service.create("spring")).hasMessage("Tag already exists");
        verify(repo, never()).save(any());
    }

    @Test
    void validateTags_passesWhenAllTagsExist() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(repo.existsById(a)).thenReturn(true);
        when(repo.existsById(b)).thenReturn(true);

        assertThatCode(() -> service.validateTags(List.of(a, b))).doesNotThrowAnyException();
    }

    @Test
    void validateTags_failsOnFirstUnknownTag() {
        UUID known = UUID.randomUUID();
        UUID unknown = UUID.randomUUID();
        when(repo.existsById(known)).thenReturn(true);
        when(repo.existsById(unknown)).thenReturn(false);

        assertThatThrownBy(() -> service.validateTags(List.of(known, unknown)))
                .hasMessage("Invalid tag id: " + unknown);
    }

    @Test
    void getById_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id)).hasMessage("Tag not found");
    }

    @Test
    void delete_removesExistingTag() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repo).deleteById(id);
    }

    @Test
    void delete_throwsWhenMissingAndDeletesNothing() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id)).hasMessage("Tag not found");
        verify(repo, never()).deleteById(any());
    }
}
