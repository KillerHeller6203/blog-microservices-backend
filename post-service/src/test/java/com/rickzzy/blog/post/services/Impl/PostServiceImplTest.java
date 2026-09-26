package com.rickzzy.blog.post.services.Impl;

import com.rickzzy.blog.post.client.CategoryClient;
import com.rickzzy.blog.post.client.TagClient;
import com.rickzzy.blog.post.dtos.CategoryDto;
import com.rickzzy.blog.post.dtos.CreatePostRequestDto;
import com.rickzzy.blog.post.dtos.PostResponseDto;
import com.rickzzy.blog.post.entities.Post;
import com.rickzzy.blog.post.entities.PostStatus;
import com.rickzzy.blog.post.repositories.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    private static final String TOKEN = "Bearer test-token";

    @Mock private PostRepository repo;
    @Mock private CategoryClient categoryClient;
    @Mock private TagClient tagClient;

    @InjectMocks private PostServiceImpl service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of()));
    }

    private Post post(String author, PostStatus status, String content) {
        return Post.builder()
                .id(UUID.randomUUID())
                .title("A post title")
                .content(content)
                .categoryId(UUID.randomUUID())
                .status(status)
                .author(author)
                .build();
    }

    private static String words(int count) {
        return "word ".repeat(count).trim();
    }

    // ---------- create ----------

    @Test
    void create_validatesCategoryAndTagsThenSavesWithAuthor() {
        UUID categoryId = UUID.randomUUID();
        Set<UUID> tagIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        CreatePostRequestDto dto = CreatePostRequestDto.builder()
                .title("Hello microservices")
                .content("Some long enough content")
                .categoryId(categoryId)
                .tagIds(tagIds)
                .status(PostStatus.PUBLISHED)
                .build();
        when(repo.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post saved = service.create(dto, "rick@example.com", TOKEN);

        verify(categoryClient).validateCategory(categoryId, TOKEN);
        verify(tagClient).validateTags(tagIds, TOKEN);
        assertThat(saved.getAuthor()).isEqualTo("rick@example.com");
        assertThat(saved.getCategoryId()).isEqualTo(categoryId);
        assertThat(saved.getTagIds()).isEqualTo(tagIds);
        assertThat(saved.getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    void create_withUnknownCategory_doesNotSave() {
        CreatePostRequestDto dto = CreatePostRequestDto.builder()
                .title("Hello microservices")
                .content("Some long enough content")
                .categoryId(UUID.randomUUID())
                .status(PostStatus.DRAFT)
                .build();
        doThrow(new RuntimeException("Category not found"))
                .when(categoryClient).validateCategory(any(), eq(TOKEN));

        assertThatThrownBy(() -> service.create(dto, "rick@example.com", TOKEN))
                .hasMessage("Category not found");

        verify(repo, never()).save(any());
    }

    @Test
    void create_withInvalidTags_doesNotSave() {
        CreatePostRequestDto dto = CreatePostRequestDto.builder()
                .title("Hello microservices")
                .content("Some long enough content")
                .categoryId(UUID.randomUUID())
                .tagIds(Set.of(UUID.randomUUID()))
                .status(PostStatus.DRAFT)
                .build();
        doThrow(new RuntimeException("Invalid tag(s)"))
                .when(tagClient).validateTags(anySet(), eq(TOKEN));

        assertThatThrownBy(() -> service.create(dto, "rick@example.com", TOKEN))
                .hasMessage("Invalid tag(s)");

        verify(repo, never()).save(any());
    }

    // ---------- update / delete ownership ----------

    @Test
    void update_byAuthor_appliesChanges() {
        Post existing = post("rick@example.com", PostStatus.DRAFT, "old content");
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);
        Post changes = Post.builder()
                .title("New title")
                .content("new content")
                .categoryId(UUID.randomUUID())
                .tagIds(Set.of())
                .status(PostStatus.PUBLISHED)
                .build();

        Post updated = service.update(existing.getId(), changes, "rick@example.com");

        assertThat(updated.getTitle()).isEqualTo("New title");
        assertThat(updated.getContent()).isEqualTo("new content");
        assertThat(updated.getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    void update_bySomeoneElse_isRejected() {
        Post existing = post("rick@example.com", PostStatus.PUBLISHED, "content");
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(existing.getId(), Post.builder().title("hacked").build(),
                "intruder@example.com"))
                .hasMessage("You are not allowed to edit this post");

        verify(repo, never()).save(any());
        assertThat(existing.getTitle()).isEqualTo("A post title");
    }

    @Test
    void delete_byAuthor_deletesPost() {
        Post existing = post("rick@example.com", PostStatus.PUBLISHED, "content");
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));

        service.delete(existing.getId(), "rick@example.com");

        verify(repo).delete(existing);
    }

    @Test
    void delete_bySomeoneElse_isRejected() {
        Post existing = post("rick@example.com", PostStatus.PUBLISHED, "content");
        when(repo.findById(existing.getId())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.delete(existing.getId(), "intruder@example.com"))
                .hasMessage("You are not allowed to delete this post");

        verify(repo, never()).delete(any());
    }

    @Test
    void getById_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id)).hasMessage("Post not found");
    }

    // ---------- reads ----------

    @Test
    void getDrafts_returnsOnlyTheAuthorsDrafts() {
        Post mine = post("rick@example.com", PostStatus.DRAFT, "content");
        Post theirs = post("other@example.com", PostStatus.DRAFT, "content");
        when(repo.findByStatus(PostStatus.DRAFT)).thenReturn(List.of(mine, theirs));

        assertThat(service.getDrafts("rick@example.com")).containsExactly(mine);
    }

    @Test
    void getAll_byCategory_hidesDrafts() {
        UUID categoryId = UUID.randomUUID();
        Post published = post("rick@example.com", PostStatus.PUBLISHED, "content");
        Post draft = post("rick@example.com", PostStatus.DRAFT, "content");
        when(repo.findByCategoryId(categoryId)).thenReturn(List.of(published, draft));
        when(categoryClient.getById(any())).thenReturn(new CategoryDto(categoryId, "Java"));

        List<PostResponseDto> result = service.getAll(categoryId, null);

        assertThat(result).extracting(PostResponseDto::getId).containsExactly(published.getId());
    }

    @Test
    void getAll_enrichesWithCategoryAndMarksOwnPostsEditable() {
        loginAs("rick@example.com");
        Post mine = post("rick@example.com", PostStatus.PUBLISHED, "content");
        Post theirs = post("other@example.com", PostStatus.PUBLISHED, "content");
        when(repo.findByStatus(PostStatus.PUBLISHED)).thenReturn(List.of(mine, theirs));
        when(categoryClient.getById(any())).thenReturn(new CategoryDto(UUID.randomUUID(), "Java"));

        List<PostResponseDto> result = service.getAll(null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isEditable()).isTrue();
        assertThat(result.get(1).isEditable()).isFalse();
        assertThat(result).allSatisfy(r -> assertThat(r.getCategory().getName()).isEqualTo("Java"));
    }

    @Test
    void getAll_anonymousVisitor_cannotEditAnything() {
        when(repo.findByStatus(PostStatus.PUBLISHED))
                .thenReturn(List.of(post("rick@example.com", PostStatus.PUBLISHED, "content")));
        when(categoryClient.getById(any())).thenReturn(new CategoryDto(UUID.randomUUID(), "Java"));

        assertThat(service.getAll(null, null).get(0).isEditable()).isFalse();
    }

    @Test
    void readingTime_isWordsDividedBy200_withMinimumOfOneMinute() {
        Post longPost = post("rick@example.com", PostStatus.PUBLISHED, words(1000));
        Post shortPost = post("rick@example.com", PostStatus.PUBLISHED, words(50));
        Post emptyPost = post("rick@example.com", PostStatus.PUBLISHED, "   ");
        when(repo.findById(longPost.getId())).thenReturn(Optional.of(longPost));
        when(repo.findById(shortPost.getId())).thenReturn(Optional.of(shortPost));
        when(repo.findById(emptyPost.getId())).thenReturn(Optional.of(emptyPost));
        when(categoryClient.getById(any())).thenReturn(new CategoryDto(UUID.randomUUID(), "Java"));

        assertThat(service.getResponseById(longPost.getId()).getReadingTime()).isEqualTo(5);
        assertThat(service.getResponseById(shortPost.getId()).getReadingTime()).isEqualTo(1);
        assertThat(service.getResponseById(emptyPost.getId()).getReadingTime()).isEqualTo(1);
    }
}
