package com.guidev1911.expl_world_api.category.controller;

import com.guidev1911.expl_world_api.category.dto.response.CategoryResponse;
import com.guidev1911.expl_world_api.category.service.CategoryService;
import com.guidev1911.expl_world_api.exception.GlobalExceptionHandler;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void shouldCreateCategory() throws Exception {

        CategoryResponse response = new CategoryResponse(
                1L,
                "Animals",
                "animals",
                "Animal category",
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(categoryService.create(any()))
                .thenReturn(response);

        String request = """
                {
                    "name": "Animals",
                    "slug": "animals",
                    "description": "Animal category"
                }
                """;

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Animals"))
                .andExpect(jsonPath("$.slug").value("animals"));

        verify(categoryService).create(any());
    }

    @Test
    void shouldReturnCategoryNotFound() throws Exception {

        when(categoryService.findBySlug("animals"))
                .thenThrow(new ResourceNotFoundException(
                        "Category not found"
                ));

        mockMvc.perform(
                        get("/api/v1/categories/animals")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Category not found"));
    }
}