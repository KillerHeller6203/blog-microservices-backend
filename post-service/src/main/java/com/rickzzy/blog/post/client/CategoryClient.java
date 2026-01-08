package com.rickzzy.blog.post.client;

import com.rickzzy.blog.post.dtos.CategoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
public class CategoryClient {

    private final WebClient webClient;

    public CategoryClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void validateCategory(UUID categoryId, String token) {

        if (token == null || token.isBlank()) {
            throw new RuntimeException("Authorization token missing");
        }
        String authHeader = token.startsWith("Bearer ")
                ? token
                : "Bearer " + token;

        try {
            webClient.get()
                    .uri("http://category-service:8083/categories/{id}", categoryId)
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Category not found");
            }
            throw e;
        }
    }

    /**
     * Public read – no Authorization needed
     */
    public CategoryDto getById(UUID categoryId) {
        return webClient.get()
                .uri("http://category-service:8083/categories/{id}", categoryId)
                .retrieve()
                .bodyToMono(CategoryDto.class)
                .block();
    }
}
