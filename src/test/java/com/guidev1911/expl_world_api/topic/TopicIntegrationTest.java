package com.guidev1911.expl_world_api.topic;

import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TopicIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void shouldCreateTopic() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Integration")
                        .slug("animals-topic-integration")
                        .description("Animals")
                        .active(true)
                        .build()
        );

        String request = """
                {
                    "name": "Sharks",
                    "slug": "sharks-integration-test",
                    "description": "Shark topics",
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(
                        post("/api/v1/topics")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sharks"))
                .andExpect(jsonPath("$.slug")
                        .value("sharks-integration-test"))
                .andExpect(jsonPath("$.categoryId")
                        .value(category.getId()));

        assertTrue(
                topicRepository.existsByCategoryIdAndSlug(
                        category.getId(),
                        "sharks-integration-test"
                )
        );
    }
    @Test
    void shouldGetTopicBySlug() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Get")
                        .slug("animals-topic-get-test")
                        .description("Animals")
                        .active(true)
                        .build()
        );

        topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-get-test")
                        .description("Shark topics")
                        .category(category)
                        .active(true)
                        .build()
        );

        mockMvc.perform(
                        get("/api/v1/topics/category/"
                                + category.getId()
                                + "/sharks-get-test")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sharks"))
                .andExpect(jsonPath("$.slug").value("sharks-get-test"))
                .andExpect(jsonPath("$.categoryId").value(category.getId()))
                .andExpect(jsonPath("$.categoryName").value("Animals Get"));
    }
    @Test
    void shouldReturn404WhenTopicDoesNotExist() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Not Found")
                        .slug("animals-topic-not-found")
                        .description("Animals")
                        .active(true)
                        .build()
        );

        mockMvc.perform(
                        get("/api/v1/topics/category/"
                                + category.getId()
                                + "/topic-does-not-exist")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Topic not found"));
    }
    @Test
    void shouldUpdateTopic() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Update")
                        .slug("animals-topic-update")
                        .description("Animals")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-update-test")
                        .description("Old description")
                        .category(category)
                        .active(true)
                        .build()
        );

        String request = """
            {
                "name": "Great White Sharks",
                "slug": "great-white-sharks",
                "description": "New description",
                "active": true,
                "categoryId": %d
            }
            """.formatted(category.getId());

        mockMvc.perform(
                        put("/api/v1/topics/" + topic.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Great White Sharks"))
                .andExpect(jsonPath("$.slug").value("great-white-sharks"))
                .andExpect(jsonPath("$.description").value("New description"))
                .andExpect(jsonPath("$.categoryId").value(category.getId()));
    }
    @Test
    void shouldDeleteTopic() throws Exception {

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Animals Delete")
                        .slug("animals-topic-delete")
                        .description("Animals")
                        .active(true)
                        .build()
        );

        Topic topic = topicRepository.save(
                Topic.builder()
                        .name("Sharks")
                        .slug("sharks-delete-test")
                        .description("Delete test")
                        .category(category)
                        .active(true)
                        .build()
        );

        mockMvc.perform(
                        delete("/api/v1/topics/" + topic.getId())
                )
                .andExpect(status().isNoContent());

        var deletedTopic = topicRepository.findById(topic.getId())
                .orElseThrow();

        assertFalse(deletedTopic.getActive());
    }
}