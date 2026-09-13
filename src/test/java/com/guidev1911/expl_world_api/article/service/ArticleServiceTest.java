package com.guidev1911.expl_world_api.article.service;

import com.guidev1911.expl_world_api.article.dto.request.CreateArticleRequest;
import com.guidev1911.expl_world_api.article.dto.response.ArticleResponse;
import com.guidev1911.expl_world_api.article.entity.Article;
import com.guidev1911.expl_world_api.article.repository.ArticleRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void shouldCreateArticle() {

        Topic topic = Topic.builder()
                .id(1L)
                .name("Sharks")
                .slug("sharks")
                .active(true)
                .build();

        CreateArticleRequest request = new CreateArticleRequest(
                "Great White Shark",
                "great-white-shark",
                "Learn about the great white shark.",
                "Complete article description.",
                null,
                true,
                1L
        );

        Article article = Article.builder()
                .id(1L)
                .title("Great White Shark")
                .slug("great-white-shark")
                .shortDescription("Learn about the great white shark.")
                .description("Complete article description.")
                .published(true)
                .topic(topic)
                .build();

        when(topicRepository.findById(1L))
                .thenReturn(Optional.of(topic));

        when(articleRepository.existsByTopicIdAndSlug(
                1L,
                "great-white-shark"
        )).thenReturn(false);

        when(articleRepository.save(any(Article.class)))
                .thenReturn(article);

        ArticleResponse response = articleService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Great White Shark", response.title());
        assertEquals("great-white-shark", response.slug());
        assertEquals(1L, response.topicId());

        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void shouldNotCreateArticleWithExistingSlug() {

        Topic topic = Topic.builder()
                .id(1L)
                .name("Sharks")
                .slug("sharks")
                .active(true)
                .build();

        CreateArticleRequest request = new CreateArticleRequest(
                "Great White Shark",
                "great-white-shark",
                "Learn about the great white shark.",
                null,
                null,
                true,
                1L
        );

        when(topicRepository.findById(1L))
                .thenReturn(Optional.of(topic));

        when(articleRepository.existsByTopicIdAndSlug(
                1L,
                "great-white-shark"
        )).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> articleService.create(request)
        );

        verify(articleRepository, never())
                .save(any(Article.class));
    }
}