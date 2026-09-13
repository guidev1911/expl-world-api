package com.guidev1911.expl_world_api.article;

import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.article.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import com.guidev1911.expl_world_api.article.entity.Article;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void shouldCreateArticle() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Article")
                        .slug("animals-article-integration")
                        .description("Animals")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-article-integration")
                        .description("Sharks")
                        .category(category)
                        .active(true)
                        .build()
        );

        String request = """
                {
                    "title": "Great White Shark",
                    "slug": "great-white-shark-integration",
                    "shortDescription": "Everything about the great white shark.",
                    "description": "An educational article about great white sharks.",
                    "published": true,
                    "topicId": %d
                }
                """.formatted(topic.getId());

        mockMvc.perform(
                        post("/api/v1/articles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Great White Shark"))
                .andExpect(jsonPath("$.slug")
                        .value("great-white-shark-integration"))
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.topicId").value(topic.getId()));

        assertTrue(
                articleRepository.existsByTopicIdAndSlug(
                        topic.getId(),
                        "great-white-shark-integration"
                )
        );
    }
    @Test
    void shouldGetArticleBySlug() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Article Get")
                        .slug("animals-article-get")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-article-get")
                        .category(category)
                        .active(true)
                        .build()
        );

        articleRepository.save(
                Article.builder()
                        .title("Great White Shark")
                        .slug("great-white-shark-get")
                        .shortDescription("About great white sharks.")
                        .description("Educational content.")
                        .published(true)
                        .topic(topic)
                        .build()
        );

        mockMvc.perform(
                        get("/api/v1/articles/topic/"
                                + topic.getId()
                                + "/great-white-shark-get")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Great White Shark"))
                .andExpect(jsonPath("$.slug").value("great-white-shark-get"))
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.topicId").value(topic.getId()))
                .andExpect(jsonPath("$.topicName").value("Sharks"));
    }
    @Test
    void shouldReturn404WhenArticleDoesNotExist() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Article Not Found")
                        .slug("animals-article-not-found")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-article-not-found")
                        .category(category)
                        .active(true)
                        .build()
        );

        mockMvc.perform(
                        get("/api/v1/articles/topic/"
                                + topic.getId()
                                + "/article-does-not-exist")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Article not found"));
    }
    @Test
    void shouldUpdateArticle() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Article Update")
                        .slug("animals-article-update")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-article-update")
                        .category(category)
                        .active(true)
                        .build()
        );

        Article article = articleRepository.save(
                Article.builder()
                        .title("Old Title")
                        .slug("old-article-slug")
                        .shortDescription("Old description")
                        .description("Old content")
                        .published(true)
                        .topic(topic)
                        .build()
        );

        String request = """
            {
                "title": "Great White Shark",
                "slug": "great-white-shark-updated",
                "shortDescription": "Updated short description.",
                "description": "Updated content.",
                "published": true,
                "topicId": %d
            }
            """.formatted(topic.getId());

        mockMvc.perform(
                        put("/api/v1/articles/" + article.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Great White Shark"))
                .andExpect(jsonPath("$.slug").value("great-white-shark-updated"))
                .andExpect(jsonPath("$.shortDescription")
                        .value("Updated short description."))
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.topicId").value(topic.getId()));
    }
}