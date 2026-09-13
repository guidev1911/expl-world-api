package com.guidev1911.expl_world_api.category.repository;

import com.guidev1911.expl_world_api.category.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindActiveCategoryBySlug() {

        Category category = Category.builder()
                .name("Animals")
                .slug("animals")
                .description("Animal category")
                .active(true)
                .build();

        categoryRepository.save(category);

        var result = categoryRepository
                .findBySlugAndActiveTrue("animals");

        assertTrue(result.isPresent());
        assertEquals("Animals", result.get().getName());
    }

    @Test
    void shouldNotFindInactiveCategoryBySlug() {

        Category category = Category.builder()
                .name("Cars")
                .slug("cars")
                .active(false)
                .build();

        categoryRepository.save(category);

        var result = categoryRepository
                .findBySlugAndActiveTrue("cars");

        assertTrue(result.isEmpty());
    }
}