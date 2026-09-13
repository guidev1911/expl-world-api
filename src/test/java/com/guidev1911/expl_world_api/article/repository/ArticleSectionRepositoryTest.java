package com.guidev1911.expl_world_api.article.repository;

import com.guidev1911.expl_world_api.article.entity.Article;
import com.guidev1911.expl_world_api.article.repository.ArticleRepository;
import com.guidev1911.expl_world_api.article.entity.ArticleSection;
import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ArticleSectionRepositoryTest {

    @Autowired
    private ArticleSectionRepository articleSectionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void shouldFindActiveSectionsOrderedByDisplayOrder() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals-section-test")
                .active(true)
                .build();

        categoryRepository.save(category);

        Topic topic = Topic.builder()
                .name("Sharks")
                .slug("sharks-section-test")
                .active(true)
                .category(category)
                .build();

        topicRepository.save(topic);

        Article article = Article.builder()
                .title("Great White Shark")
                .slug("great-white-shark-section-test")
                .shortDescription("About great white sharks.")
                .published(true)
                .topic(topic)
                .build();

        articleRepository.save(article);

        ArticleSection section1 = ArticleSection.builder()
                .title("Habitat")
                .content("The great white shark lives in several oceans.")
                .sectionType("HABITAT")
                .displayOrder(2)
                .active(true)
                .article(article)
                .build();

        ArticleSection section2 = ArticleSection.builder()
                .title("Overview")
                .content("The great white shark is a large predatory shark.")
                .sectionType("OVERVIEW")
                .displayOrder(1)
                .active(true)
                .article(article)
                .build();

        ArticleSection inactiveSection = ArticleSection.builder()
                .title("Diet")
                .content("Diet information.")
                .sectionType("DIET")
                .displayOrder(3)
                .active(false)
                .article(article)
                .build();

        articleSectionRepository.save(section1);
        articleSectionRepository.save(section2);
        articleSectionRepository.save(inactiveSection);

        var pageable = PageRequest.of(0, 10);

        var result = articleSectionRepository
                .findAllByArticleIdAndActiveTrueOrderByDisplayOrderAsc(
                        article.getId(),
                        pageable
                );

        assertEquals(2, result.getTotalElements());

        assertEquals(
                "Overview",
                result.getContent().get(0).getTitle()
        );

        assertEquals(
                "Habitat",
                result.getContent().get(1).getTitle()
        );

        assertTrue(result.getContent()
                .stream()
                .allMatch(ArticleSection::getActive));
    }
}