package com.guidev1911.expl_world_api.topic.repository;

import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class TopicRepositoryTest {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindActiveTopicBySlug() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals-test")
                .active(true)
                .build();

        categoryRepository.save(category);

        Topic topic = Topic.builder()
                .name("Sharks")
                .slug("sharks")
                .description("Everything about sharks.")
                .active(true)
                .category(category)
                .build();

        topicRepository.save(topic);

        var result = topicRepository
                .findByCategoryIdAndSlugAndActiveTrue(
                        category.getId(),
                        "sharks"
                );

        assertTrue(result.isPresent());
        assertEquals("Sharks", result.get().getName());
    }

    @Test
    void shouldNotFindInactiveTopicBySlug() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals-test")
                .active(true)
                .build();

        categoryRepository.save(category);

        Topic topic = Topic.builder()
                .name("Cars")
                .slug("cars")
                .active(false)
                .category(category)
                .build();

        topicRepository.save(topic);

        var result = topicRepository
                .findByCategoryIdAndSlugAndActiveTrue(
                        category.getId(),
                        "cars"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindActiveTopicsByCategory() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals-test")
                .active(true)
                .build();

        categoryRepository.save(category);

        Topic topic1 = Topic.builder()
                .name("Sharks")
                .slug("sharks")
                .active(true)
                .category(category)
                .build();

        Topic topic2 = Topic.builder()
                .name("Whales")
                .slug("whales")
                .active(true)
                .category(category)
                .build();

        Topic inactiveTopic = Topic.builder()
                .name("Dinosaurs")
                .slug("dinosaurs")
                .active(false)
                .category(category)
                .build();

        topicRepository.save(topic1);
        topicRepository.save(topic2);
        topicRepository.save(inactiveTopic);

        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        var result = topicRepository
                .findAllByCategoryIdAndActiveTrue(
                        category.getId(),
                        pageable
                );

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent()
                .stream()
                .allMatch(Topic::getActive));
    }
}