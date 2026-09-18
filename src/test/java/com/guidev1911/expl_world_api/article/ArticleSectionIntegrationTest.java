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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @Test
    void shouldListArticleSectionsWithPagination() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Sections Pagination")
                        .slug("animals-sections-pagination")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-sections-pagination")
                        .category(category)
                        .active(true)
                        .build()
        );

        Article article = articleRepository.save(
                Article.builder()
                        .title("Great White Shark")
                        .slug("great-white-sections-pagination")
                        .shortDescription("About great white sharks.")
                        .description("Educational content.")
                        .published(true)
                        .topic(topic)
                        .build()
        );

        articleSectionRepository.save(
                ArticleSection.builder()
                        .title("Diet")
                        .content("Great white sharks eat fish and marine mammals.")
                        .sectionType("DIET")
                        .displayOrder(2)
                        .active(true)
                        .article(article)
                        .build()
        );

        articleSectionRepository.save(
                ArticleSection.builder()
                        .title("Habitat")
                        .content("They live in coastal waters.")
                        .sectionType("HABITAT")
                        .displayOrder(1)
                        .active(true)
                        .article(article)
                        .build()
        );

        mockMvc.perform(
                        get("/api/v1/article-sections/article/" + article.getId())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Habitat"))
                .andExpect(jsonPath("$.content[0].displayOrder").value(1))
                .andExpect(jsonPath("$.content[1].title").value("Diet"))
                .andExpect(jsonPath("$.content[1].displayOrder").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }
    @Test
    void shouldUpdateArticleSection() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Section Update")
                        .slug("animals-section-update")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-section-update")
                        .category(category)
                        .active(true)
                        .build()
        );

        Article article = articleRepository.save(
                Article.builder()
                        .title("Great White Shark")
                        .slug("great-white-section-update")
                        .shortDescription("About sharks.")
                        .description("Educational content.")
                        .published(true)
                        .topic(topic)
                        .build()
        );

        ArticleSection section = articleSectionRepository.save(
                ArticleSection.builder()
                        .title("Old Title")
                        .content("Old content")
                        .sectionType("OVERVIEW")
                        .displayOrder(1)
                        .active(true)
                        .article(article)
                        .build()
        );

        String request = """
            {
                "title": "Habitat",
                "content": "Great white sharks live in coastal waters.",
                "sectionType": "habitat",
                "displayOrder": 2,
                "active": true
            }
            """;

        mockMvc.perform(
                        put("/api/v1/article-sections/" + section.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Habitat"))
                .andExpect(jsonPath("$.content")
                        .value("Great white sharks live in coastal waters."))
                .andExpect(jsonPath("$.sectionType").value("HABITAT"))
                .andExpect(jsonPath("$.displayOrder").value(2))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.articleId").value(article.getId()));
    }
    @Test
    void shouldDeleteArticleSection() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Section Delete")
                        .slug("animals-section-delete")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-section-delete")
                        .category(category)
                        .active(true)
                        .build()
        );

        Article article = articleRepository.save(
                Article.builder()
                        .title("Great White Shark")
                        .slug("great-white-section-delete")
                        .shortDescription("About sharks.")
                        .description("Educational content.")
                        .published(true)
                        .topic(topic)
                        .build()
        );

        ArticleSection section = articleSectionRepository.save(
                ArticleSection.builder()
                        .title("Facts")
                        .content("Interesting facts about sharks.")
                        .sectionType("FACTS")
                        .displayOrder(1)
                        .active(true)
                        .article(article)
                        .build()
        );

        mockMvc.perform(
                        delete("/api/v1/article-sections/" + section.getId())
                )
                .andExpect(status().isNoContent());

        var deletedSection = articleSectionRepository
                .findById(section.getId())
                .orElseThrow();

        assertFalse(deletedSection.getActive());
    }
}