package com.guidev1911.expl_world_api.article.controller;

import com.guidev1911.expl_world_api.article.dto.request.CreateArticleRequest;
import com.guidev1911.expl_world_api.article.dto.request.UpdateArticleRequest;
import com.guidev1911.expl_world_api.article.dto.response.ArticleResponse;
import com.guidev1911.expl_world_api.article.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> create(
            @Valid @RequestBody CreateArticleRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleService.create(request));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<Page<ArticleResponse>> findAllByTopic(
            @PathVariable Long topicId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                articleService.findAllByTopic(topicId, pageable)
        );
    }

    @GetMapping("/topic/{topicId}/{slug}")
    public ResponseEntity<ArticleResponse> findBySlug(
            @PathVariable Long topicId,
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(
                articleService.findBySlug(topicId, slug)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateArticleRequest request
    ) {
        return ResponseEntity.ok(
                articleService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        articleService.delete(id);

        return ResponseEntity.noContent().build();
    }
}