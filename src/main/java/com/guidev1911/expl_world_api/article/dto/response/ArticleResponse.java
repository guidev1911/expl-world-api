package com.guidev1911.expl_world_api.article.dto.response;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String slug,
        String shortDescription,
        String description,
        String imageUrl,
        Boolean published,
        Long topicId,
        String topicName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}