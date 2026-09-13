package com.guidev1911.expl_world_api.topic.service;

import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.topic.dto.request.CreateTopicRequest;
import com.guidev1911.expl_world_api.topic.dto.response.TopicResponse;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    void shouldCreateTopic() {

        Category category = Category.builder()
                .id(1L)
                .name("Animals")
                .slug("animals")
                .active(true)
                .build();

        CreateTopicRequest request = new CreateTopicRequest(
                "Sharks",
                "sharks",
                "Everything about sharks.",
                null,
                1L
        );

        Topic topic = Topic.builder()
                .id(1L)
                .name("Sharks")
                .slug("sharks")
                .description("Everything about sharks.")
                .active(true)
                .category(category)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(java.util.Optional.of(category));

        when(topicRepository.existsByCategoryIdAndSlug(1L, "sharks"))
                .thenReturn(false);

        when(topicRepository.save(any(Topic.class)))
                .thenReturn(topic);

        TopicResponse response = topicService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Sharks", response.name());
        assertEquals("sharks", response.slug());
        assertEquals(1L, response.categoryId());

        verify(topicRepository).save(any(Topic.class));
    }

    @Test
    void shouldNotCreateTopicWithExistingSlug() {

        Category category = Category.builder()
                .id(1L)
                .name("Animals")
                .slug("animals")
                .active(true)
                .build();

        CreateTopicRequest request = new CreateTopicRequest(
                "Sharks",
                "sharks",
                "Everything about sharks.",
                null,
                1L
        );

        when(categoryRepository.findById(1L))
                .thenReturn(java.util.Optional.of(category));

        when(topicRepository.existsByCategoryIdAndSlug(1L, "sharks"))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> topicService.create(request)
        );

        verify(topicRepository, never())
                .save(any(Topic.class));
    }
}