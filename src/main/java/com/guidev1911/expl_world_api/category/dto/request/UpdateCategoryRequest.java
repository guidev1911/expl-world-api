package com.guidev1911.expl_world_api.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 120)
        String slug,

        String description,

        @Size(max = 500)
        String imageUrl,

        Boolean active
) {
}