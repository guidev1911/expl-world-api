package com.guidev1911.expl_world_api.article.service;

import com.guidev1911.expl_world_api.article.dto.request.CreateArticleSectionRequest;
import com.guidev1911.expl_world_api.article.dto.response.ArticleSectionResponse;
import com.guidev1911.expl_world_api.article.entity.Article;
import com.guidev1911.expl_world_api.article.entity.ArticleSection;
import com.guidev1911.expl_world_api.article.repository.ArticleRepository;
import com.guidev1911.expl_world_api.article.repository.ArticleSectionRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleSectionServiceTest {

    @Mock
    private ArticleSectionRepository sectionRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleSectionService sectionService;

    @Test
    void shouldCreateArticleSection() {

        Article article = Article.builder()
                .id(1L)
                .title("Great White Shark")
                .slug("great-white-shark")
                .published(true)
                .build();

        CreateArticleSectionRequest request =
                new CreateArticleSectionRequest(
                        "Overview",
                        "The great white shark is a large predatory shark.",
                        "OVERVIEW",
                        1,
                        true,
                        1L
                );

        ArticleSection section = ArticleSection.builder()
                .id(1L)
                .title("Overview")
                .content("The great white shark is a large predatory shark.")
                .sectionType("OVERVIEW")
                .displayOrder(1)
                .active(true)
                .article(article)
                .build();

        when(articleRepository.findById(1L))
                .thenReturn(Optional.of(article));

        when(sectionRepository.save(any(ArticleSection.class)))
                .thenReturn(section);

        ArticleSectionResponse response =
                sectionService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Overview", response.title());
        assertEquals("OVERVIEW", response.sectionType());
        assertEquals(1, response.displayOrder());
        assertEquals(1L, response.articleId());

        verify(sectionRepository).save(any(ArticleSection.class));
    }

    @Test
    void shouldNotCreateSectionWhenArticleDoesNotExist() {

        CreateArticleSectionRequest request =
                new CreateArticleSectionRequest(
                        "Overview",
                        "Content",
                        "OVERVIEW",
                        1,
                        true,
                        1L
                );

        when(articleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> sectionService.create(request)
        );

        verify(sectionRepository, never())
                .save(any(ArticleSection.class));
    }
}