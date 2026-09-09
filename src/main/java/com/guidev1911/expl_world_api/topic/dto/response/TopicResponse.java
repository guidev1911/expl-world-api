package com.guidev1911.expl_world_api.topic.dto.response;

import java.time.LocalDateTime;

public record TopicResponse(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Boolean active,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}