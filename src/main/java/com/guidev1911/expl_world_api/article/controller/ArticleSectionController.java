package com.guidev1911.expl_world_api.article.controller;

import com.guidev1911.expl_world_api.article.dto.request.CreateArticleSectionRequest;
import com.guidev1911.expl_world_api.article.dto.request.UpdateArticleSectionRequest;
import com.guidev1911.expl_world_api.article.dto.response.ArticleSectionResponse;
import com.guidev1911.expl_world_api.article.service.ArticleSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/article-sections")
@RequiredArgsConstructor
public class ArticleSectionController {

    private final ArticleSectionService articleSectionService;

    @PostMapping
    public ResponseEntity<ArticleSectionResponse> create(
            @Valid @RequestBody CreateArticleSectionRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleSectionService.create(request));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<ArticleSectionResponse>> findAllByArticle(
            @PathVariable Long articleId
    ) {
        return ResponseEntity.ok(
                articleSectionService.findAllByArticle(articleId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleSectionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateArticleSectionRequest request
    ) {
        return ResponseEntity.ok(
                articleSectionService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        articleSectionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}