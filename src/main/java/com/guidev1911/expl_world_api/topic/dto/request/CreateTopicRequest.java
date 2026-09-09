package com.guidev1911.expl_world_api.topic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTopicRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 120)
        String slug,

        String description,

        @Size(max = 500)
        String imageUrl,

        @NotNull
        Long categoryId
) {
}