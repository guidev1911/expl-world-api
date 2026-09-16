package com.guidev1911.expl_world_api.article;

import com.guidev1911.expl_world_api.article.entity.Article;
import com.guidev1911.expl_world_api.article.entity.ArticleSection;
import com.guidev1911.expl_world_api.article.repository.ArticleRepository;
import com.guidev1911.expl_world_api.article.repository.ArticleSectionRepository;
import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleSectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleSectionRepository articleSectionRepository;

    @Test
    void shouldCreateArticleSection() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Section")
                        .slug("animals-section-integration")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-section-integration")
                        .category(category)
                        .active(true)
                        .build()
        );

        Article article = articleRepository.save(
                Article.builder()
                        .title("Great White Shark")
                        .slug("great-white-section-integration")
                        .shortDescription("About great white sharks.")
                        .description("Educational content.")
                        .published(true)
                        .topic(topic)
                        .build()
        );

        String request = """
                {
                    "title": "Habitat",
                    "content": "Great white sharks live in coastal waters.",
                    "sectionType": "habitat",
                    "displayOrder": 1,
                    "active": true,
                    "articleId": %d
                }
                """.formatted(article.getId());

        mockMvc.perform(
                        post("/api/v1/article-sections")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Habitat"))
                .andExpect(jsonPath("$.sectionType").value("HABITAT"))
                .andExpect(jsonPath("$.displayOrder").value(1))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.articleId").value(article.getId()));

        assertTrue(
                articleSectionRepository
                        .findAll()
                        .stream()
                        .anyMatch(section ->
                                section.getArticle().getId().equals(article.getId())
                                        && section.getTitle().equals("Habitat")
                        )
        );
    }
}