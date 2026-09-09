package com.guidev1911.expl_world_api.article.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateArticleRequest(

        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        @Size(max = 180)
        String slug,

        @NotBlank
        @Size(max = 500)
        String shortDescription,

        String description,

        @Size(max = 500)
        String imageUrl,

        Boolean published,

        Long topicId
) {
}