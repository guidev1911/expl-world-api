package com.guidev1911.expl_world_api.category.service;

import com.guidev1911.expl_world_api.category.dto.request.CreateCategoryRequest;
import com.guidev1911.expl_world_api.category.dto.request.UpdateCategoryRequest;
import com.guidev1911.expl_world_api.category.dto.response.CategoryResponse;
import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {

        String slug = normalizeSlug(request.slug());

        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("Category slug already exists");
        }

        Category category = Category.builder()
                .name(request.name().trim())
                .slug(slug)
                .description(request.description())
                .imageUrl(request.imageUrl())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {

        return categoryRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findBySlug(String slug) {

        Category category = categoryRepository.findBySlugAndActiveTrue(slug)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        return toResponse(category);
    }

    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        String slug = normalizeSlug(request.slug());

        if (categoryRepository.existsBySlugAndIdNot(slug, id)) {
            throw new BusinessException("Category slug already exists");
        }

        category.setName(request.name().trim());
        category.setSlug(slug);
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());

        if (request.active() != null) {
            category.setActive(request.active());
        }

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        category.setActive(false);

        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl(),
                category.getActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }
}