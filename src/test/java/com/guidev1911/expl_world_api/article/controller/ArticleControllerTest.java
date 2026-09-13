package com.guidev1911.expl_world_api.article.controller;

import com.guidev1911.expl_world_api.article.dto.response.ArticleResponse;
import com.guidev1911.expl_world_api.article.service.ArticleService;
import com.guidev1911.expl_world_api.exception.GlobalExceptionHandler;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
@Import(GlobalExceptionHandler.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @Test
    void shouldCreateArticle() throws Exception {

        ArticleResponse response = new ArticleResponse(
                1L,
                "Great White Shark",
                "great-white-shark",
                "Learn about the great white shark.",
                "Complete article description.",
                null,
                true,
                1L,
                "Sharks",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(articleService.create(any()))
                .thenReturn(response);

        String request = """
                {
                    "title": "Great White Shark",
                    "slug": "great-white-shark",
                    "shortDescription": "Learn about the great white shark.",
                    "description": "Complete article description.",
                    "published": true,
                    "topicId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Great White Shark"))
                .andExpect(jsonPath("$.slug")
                        .value("great-white-shark"))
                .andExpect(jsonPath("$.topicId").value(1));

        verify(articleService).create(any());
    }

    @Test
    void shouldReturnArticleNotFound() throws Exception {

        when(articleService.findBySlug(
                1L,
                "great-white-shark"
        )).thenThrow(new ResourceNotFoundException(
                "Article not found"
        ));

        mockMvc.perform(
                        get("/api/v1/articles/topic/1/great-white-shark")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Article not found"));
    }
}