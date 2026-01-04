package com.rickzzy.blog.post.services.Impl;

import com.rickzzy.blog.post.client.CategoryClient;
import com.rickzzy.blog.post.client.TagClient;
import com.rickzzy.blog.post.dtos.*;
import com.rickzzy.blog.post.entities.Post;
import com.rickzzy.blog.post.entities.PostStatus;
import com.rickzzy.blog.post.repositories.PostRepository;
import com.rickzzy.blog.post.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository repo;
    private final CategoryClient categoryClient;
    private final TagClient tagClient;


//    @Override
//    public List<Post> getAll(UUID categoryId, UUID tagId) {
//
//        // Homepage MUST show only published posts
//        if (categoryId != null) {
//            return repo.findByCategoryId(categoryId)
//                    .stream()
//                    .filter(post -> post.getStatus() == PostStatus.PUBLISHED)
//                    .toList();
//        }
//
//        return repo.findByStatus(PostStatus.PUBLISHED);
//    }
    @Override
    public List<PostResponseDto> getAll(UUID categoryId, UUID tagId) {

        List<Post> posts = (categoryId != null)
                ? repo.findByCategoryId(categoryId)
                : repo.findByStatus(PostStatus.PUBLISHED);

        return posts.stream()
                .filter(p -> p.getStatus() == PostStatus.PUBLISHED)
                .map(this::toResponse)
                .toList();
    }


    @Override
    public List<Post> getDrafts(String author) {
        return repo.findByStatus(PostStatus.DRAFT)
                .stream()
                .filter(p -> p.getAuthor().equals(author))
                .toList();
    }

    @Override
    public Post getById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public Post create(CreatePostRequestDto dto, String author, String token) {

        categoryClient.validateCategory(dto.getCategoryId(), token);
        tagClient.validateTags(dto.getTagIds(), token);

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .categoryId(dto.getCategoryId())
                .tagIds(dto.getTagIds())
                .status(dto.getStatus())
                .author(author)
                .build();

        return repo.save(post);
    }


    @Override
    public Post update(UUID id, Post updated, String username) {

        Post post = getById(id);

        if (!post.getAuthor().equals(username)) {
            throw new RuntimeException("You are not allowed to edit this post");
        }

        post.setTitle(updated.getTitle());
        post.setContent(updated.getContent());
        post.setCategoryId(updated.getCategoryId());
        post.setTagIds(updated.getTagIds());
        post.setStatus(updated.getStatus());

        return repo.save(post);
    }

    @Override
    public void delete(UUID id, String username) {

        Post post = getById(id);

        if (!post.getAuthor().equals(username)) {
            throw new RuntimeException("You are not allowed to delete this post");
        }

        repo.delete(post);
    }


    private PostResponseDto toResponse(Post post) {

        String currentUser = null;

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            currentUser = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();
        }

        boolean editable = currentUser != null &&
                currentUser.equals(post.getAuthor());

        CategoryDto category =
                categoryClient.getById(post.getCategoryId());

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .status(post.getStatus())

                .author(new AuthorDto(post.getAuthor()))
                .category(category)
                .tags(List.of())

                .editable(editable)

                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .readingTime(calculateReadingTime(post.getContent()))
                .build();
    }
    @Override
    public PostResponseDto getResponseById(UUID id) {
        Post post = getById(id);
        return toResponse(post);
    }



    private int calculateReadingTime(String content) {
        if (content == null || content.isBlank()) return 1;
        int words = content.split("\\s+").length;
        return Math.max(1, words / 200);
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

}
