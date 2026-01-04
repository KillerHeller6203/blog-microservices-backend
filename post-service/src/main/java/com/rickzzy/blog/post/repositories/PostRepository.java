package com.rickzzy.blog.post.repositories;

import com.rickzzy.blog.post.entities.Post;
import com.rickzzy.blog.post.entities.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByCategoryId(UUID categoryId);

    List<Post> findByStatus(PostStatus status);
}
