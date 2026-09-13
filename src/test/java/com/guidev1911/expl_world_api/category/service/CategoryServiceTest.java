package com.guidev1911.expl_world_api.category.service;

import com.guidev1911.expl_world_api.category.dto.request.CreateCategoryRequest;
import com.guidev1911.expl_world_api.category.dto.response.CategoryResponse;
import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldCreateCategory() {

        CreateCategoryRequest request = new CreateCategoryRequest(
                "Animals",
                "animals",
                "Animal category",
                "https://example.com/animals.jpg"
        );

        Category category = Category.builder()
                .id(1L)
                .name("Animals")
                .slug("animals")
                .description("Animal category")
                .imageUrl("https://example.com/animals.jpg")
                .active(true)
                .build();

        when(categoryRepository.existsBySlug("animals"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryResponse response = categoryService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Animals", response.name());
        assertEquals("animals", response.slug());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldNotCreateCategoryWithExistingSlug() {

        CreateCategoryRequest request = new CreateCategoryRequest(
                "Animals",
                "animals",
                "Animal category",
                null
        );

        when(categoryRepository.existsBySlug("animals"))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }
}