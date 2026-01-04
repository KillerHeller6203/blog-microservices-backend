package com.rickzzy.blog.post.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.UUID;

@Component
public class TagClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String TAG_VALIDATE_URL =
            "http://localhost:8084/tags/validate";


    public void validateTags(Set<UUID> tagIds, String token) {

        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<Set<UUID>> entity = new HttpEntity<>(tagIds, headers);

        try {
            restTemplate.exchange(
                    TAG_VALIDATE_URL,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid tag(s)");
        }
    }
}
