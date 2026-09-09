package com.guidev1911.expl_world_api.topic.controller;

import com.guidev1911.expl_world_api.topic.dto.request.CreateTopicRequest;
import com.guidev1911.expl_world_api.topic.dto.request.UpdateTopicRequest;
import com.guidev1911.expl_world_api.topic.dto.response.TopicResponse;
import com.guidev1911.expl_world_api.topic.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping
    public ResponseEntity<TopicResponse> create(
            @Valid @RequestBody CreateTopicRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(topicService.create(request));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TopicResponse>> findAllByCategory(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(
                topicService.findAllByCategory(categoryId)
        );
    }

    @GetMapping("/category/{categoryId}/{slug}")
    public ResponseEntity<TopicResponse> findBySlug(
            @PathVariable Long categoryId,
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(
                topicService.findBySlug(categoryId, slug)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTopicRequest request
    ) {
        return ResponseEntity.ok(
                topicService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        topicService.delete(id);

        return ResponseEntity.noContent().build();
    }
}