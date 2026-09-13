package com.guidev1911.expl_world_api.category;

import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
    @Test
    void shouldGetCategoryBySlug() throws Exception {

        categoryRepository.save(
                com.guidev1911.expl_world_api.category.entity.Category.builder()
                        .name("Animals")
                        .slug("animals-integration-test")
                        .description("Animal category")
                        .active(true)
                        .build()
        );

        mockMvc.perform(
                        get("/api/v1/categories/animals-integration-test")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Animals"))
                .andExpect(jsonPath("$.slug").value("animals-integration-test"))
                .andExpect(jsonPath("$.active").value(true));
    }
    @Test
    void shouldUpdateCategory() throws Exception {

        var category = categoryRepository.save(
                Category.builder()
                        .name("Animals")
                        .slug("animals-update-test")
                        .description("Old description")
                        .active(true)
                        .build()
        );

        String request = """
            {
                "name": "Animals Updated",
                "slug": "animals-updated",
                "description": "New description",
                "active": true
            }
            """;

        mockMvc.perform(
                        put("/api/v1/categories/" + category.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Animals Updated"))
                .andExpect(jsonPath("$.slug").value("animals-updated"))
                .andExpect(jsonPath("$.description").value("New description"));
    }
}