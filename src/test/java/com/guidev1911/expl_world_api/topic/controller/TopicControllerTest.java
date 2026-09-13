package com.guidev1911.expl_world_api.topic.controller;

import com.guidev1911.expl_world_api.exception.GlobalExceptionHandler;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import com.guidev1911.expl_world_api.topic.dto.response.TopicResponse;
import com.guidev1911.expl_world_api.topic.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TopicController.class)
@Import(GlobalExceptionHandler.class)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TopicService topicService;

    @Test
    void shouldCreateTopic() throws Exception {

        TopicResponse response = new TopicResponse(
                1L,
                "Sharks",
                "sharks",
                "Everything about sharks.",
                null,
                true,
                1L,
                "Animals",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(topicService.create(any()))
                .thenReturn(response);

        String request = """
                {
                    "name": "Sharks",
                    "slug": "sharks",
                    "description": "Everything about sharks.",
                    "categoryId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sharks"))
                .andExpect(jsonPath("$.slug").value("sharks"))
                .andExpect(jsonPath("$.categoryId").value(1));

        verify(topicService).create(any());
    }

    @Test
    void shouldReturnTopicNotFound() throws Exception {

        when(topicService.findBySlug(1L, "sharks"))
                .thenThrow(new ResourceNotFoundException(
                        "Topic not found"
                ));

        mockMvc.perform(
                        get("/api/v1/topics/category/1/sharks")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Topic not found"));
    }
}