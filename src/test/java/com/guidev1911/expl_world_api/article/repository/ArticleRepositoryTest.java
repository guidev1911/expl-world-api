package com.guidev1911.expl_world_api.article.repository;

import com.guidev1911.expl_world_api.article.entity.Article;
import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void shouldFindPublishedArticleBySlug() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals-test")
                .active(true)
                .build();

        categoryRepository.save(category);

        Topic topic = Topic.builder()
                .name("Sharks")
                .slug("sharks")
                .active(true)
                .category(category)
                .build();

        topicRepository.save(topic);

        Article article = Article.builder()
                .title("Great White Shark")
                .slug("great-white-shark")
                .shortDescription("Everything about the great white shark.")
                .description("Detailed article.")
                .published(true)
                .topic(topic)
                .build();

        articleRepository.save(article);

        var result = articleRepository
                .findByTopicIdAndSlugAndPublishedTrue(
                        topic.getId(),
                        "great-white-shark"
                );

        assertTrue(result.isPresent());
        assertEquals("Great White Shark", result.get().getTitle());
    }

    @Test
    void shouldNotFindUnpublishedArticleBySlug() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals-test")
                .active(true)
                .build();

        categoryRepository.save(category);

        Topic topic = Topic.builder()
                .name("Sharks")
                .slug("sharks")
                .active(true)
                .category(category)
                .build();

        topicRepository.save(topic);

        Article article = Article.builder()
                .title("Great White Shark")
                .slug("great-white-shark")
                .shortDescription("Everything about the great white shark.")
                .published(false)
                .topic(topic)
                .build();

        articleRepository.save(article);

        var result = articleRepository
                .findByTopicIdAndSlugAndPublishedTrue(
                        topic.getId(),
                        "great-white-shark"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindPublishedArticlesByTopic() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals-test")
                .active(true)
                .build();

        categoryRepository.save(category);

        Topic topic = Topic.builder()
                .name("Sharks")
                .slug("sharks")
                .active(true)
                .category(category)
                .build();

        topicRepository.save(topic);

        Article article1 = Article.builder()
                .title("Great White Shark")
                .slug("great-white-shark")
                .shortDescription("About great white sharks.")
                .published(true)
                .topic(topic)
                .build();

        Article article2 = Article.builder()
                .title("Hammerhead Shark")
                .slug("hammerhead-shark")
                .shortDescription("About hammerhead sharks.")
                .published(true)
                .topic(topic)
                .build();

        Article unpublishedArticle = Article.builder()
                .title("Tiger Shark")
                .slug("tiger-shark")
                .shortDescription("About tiger sharks.")
                .published(false)
                .topic(topic)
                .build();

        articleRepository.save(article1);
        articleRepository.save(article2);
        articleRepository.save(unpublishedArticle);

        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        var result = articleRepository
                .findAllByTopicIdAndPublishedTrue(
                        topic.getId(),
                        pageable
                );

        assertEquals(2, result.getTotalElements());

        assertTrue(result.getContent()
                .stream()
                .allMatch(Article::getPublished));
    }
}