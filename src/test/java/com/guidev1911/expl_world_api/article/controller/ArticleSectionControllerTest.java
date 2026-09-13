package com.guidev1911.expl_world_api.article.controller;

import com.guidev1911.expl_world_api.article.dto.response.ArticleSectionResponse;
import com.guidev1911.expl_world_api.article.service.ArticleSectionService;
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

@WebMvcTest(ArticleSectionController.class)
@Import(GlobalExceptionHandler.class)
class ArticleSectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleSectionService articleSectionService;

    @Test
    void shouldCreateArticleSection() throws Exception {

        ArticleSectionResponse response =
                new ArticleSectionResponse(
                        1L,
                        "Overview",
                        "The great white shark is a large predator.",
                        "OVERVIEW",
                        1,
                        true,
                        1L,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(articleSectionService.create(any()))
                .thenReturn(response);

        String request = """
                {
                    "title": "Overview",
                    "content": "The great white shark is a large predator.",
                    "sectionType": "OVERVIEW",
                    "displayOrder": 1,
                    "active": true,
                    "articleId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/article-sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Overview"))
                .andExpect(jsonPath("$.sectionType").value("OVERVIEW"))
                .andExpect(jsonPath("$.articleId").value(1));

        verify(articleSectionService).create(any());
    }

    @Test
    void shouldReturnArticleSectionNotFound() throws Exception {

        when(articleSectionService.update(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException(
                        "Article section not found"
                ));

        String request = """
                {
                    "title": "Overview",
                    "content": "Content",
                    "sectionType": "OVERVIEW",
                    "displayOrder": 1,
                    "active": true
                }
                """;

        mockMvc.perform(
                        put("/api/v1/article-sections/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Article section not found"));
    }
}