package com.guidev1911.expl_world_api.article.dto.response;

import java.time.LocalDateTime;

public record ArticleSectionResponse(
        Long id,
        String title,
        String content,
        String sectionType,
        Integer displayOrder,
        Boolean active,
        Long articleId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}