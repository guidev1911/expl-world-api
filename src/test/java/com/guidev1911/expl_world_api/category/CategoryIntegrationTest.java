package com.guidev1911.expl_world_api.category;

import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldCreateCategory() throws Exception {

        String request = """
                {
                    "name": "Technology",
                    "slug": "technology-integration-test",
                    "description": "Technology category"
                }
                """;

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Technology"))
                .andExpect(jsonPath("$.slug")
                        .value("technology-integration-test"));

        assertTrue(
                categoryRepository.existsBySlug(
                        "technology-integration-test"
                )
        );
    }
}