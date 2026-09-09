package com.guidev1911.expl_world_api.article.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateArticleSectionRequest(

        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        String content,

        @NotBlank
        @Size(max = 50)
        String sectionType,

        Integer displayOrder,

        Boolean active
) {
}