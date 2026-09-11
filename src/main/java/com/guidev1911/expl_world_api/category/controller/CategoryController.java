package com.guidev1911.expl_world_api.category.controller;

import com.guidev1911.expl_world_api.category.dto.request.CreateCategoryRequest;
import com.guidev1911.expl_world_api.category.dto.request.UpdateCategoryRequest;
import com.guidev1911.expl_world_api.category.dto.response.CategoryResponse;
import com.guidev1911.expl_world_api.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> findAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                categoryService.findAll(pageable)
        );
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryResponse> findBySlug(
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(categoryService.findBySlug(slug));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(
                categoryService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}