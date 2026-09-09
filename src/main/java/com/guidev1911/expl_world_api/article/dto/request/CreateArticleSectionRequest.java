package com.guidev1911.expl_world_api.article.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateArticleSectionRequest(

        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        String content,

        @NotBlank
        @Size(max = 50)
        String sectionType,

        @NotNull
        Integer displayOrder,

        Boolean active,

        @NotNull
        Long articleId
) {
}