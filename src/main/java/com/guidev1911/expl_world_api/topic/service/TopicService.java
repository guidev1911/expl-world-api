package com.guidev1911.expl_world_api.topic.service;

import com.guidev1911.expl_world_api.category.entity.Category;
import com.guidev1911.expl_world_api.category.repository.CategoryRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import com.guidev1911.expl_world_api.topic.dto.request.CreateTopicRequest;
import com.guidev1911.expl_world_api.topic.dto.request.UpdateTopicRequest;
import com.guidev1911.expl_world_api.topic.dto.response.TopicResponse;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TopicResponse create(CreateTopicRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        if (!category.getActive()) {
            throw new BusinessException("Cannot create topic in inactive category");
        }

        String slug = normalizeSlug(request.slug());

        if (topicRepository.existsByCategoryIdAndSlug(
                request.categoryId(),
                slug
        )) {
            throw new BusinessException("Topic slug already exists in this category");
        }

        Topic topic = Topic.builder()
                .name(request.name().trim())
                .slug(slug)
                .description(request.description())
                .imageUrl(request.imageUrl())
                .category(category)
                .build();

        return toResponse(topicRepository.save(topic));
    }

    @Transactional(readOnly = true)
    public Page<TopicResponse> findAllByCategory(
            Long categoryId,
            Pageable pageable
    ) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        return topicRepository
                .findAllByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TopicResponse findBySlug(
            Long categoryId,
            String slug
    ) {

        Topic topic = topicRepository
                .findByCategoryIdAndSlugAndActiveTrue(
                        categoryId,
                        slug
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found"));

        return toResponse(topic);
    }

    @Transactional
    public TopicResponse update(
            Long id,
            UpdateTopicRequest request
    ) {

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found"));

        Long categoryId = request.categoryId() != null
                ? request.categoryId()
                : topic.getCategory().getId();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        String slug = normalizeSlug(request.slug());

        if (topicRepository.existsByCategoryIdAndSlugAndIdNot(
                categoryId,
                slug,
                id
        )) {
            throw new BusinessException(
                    "Topic slug already exists in this category"
            );
        }

        topic.setName(request.name().trim());
        topic.setSlug(slug);
        topic.setDescription(request.description());
        topic.setImageUrl(request.imageUrl());
        topic.setCategory(category);

        if (request.active() != null) {
            topic.setActive(request.active());
        }

        return toResponse(topicRepository.save(topic));
    }

    @Transactional
    public void delete(Long id) {

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found"));

        topic.setActive(false);

        topicRepository.save(topic);
    }

    private TopicResponse toResponse(Topic topic) {

        return new TopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getSlug(),
                topic.getDescription(),
                topic.getImageUrl(),
                topic.getActive(),
                topic.getCategory().getId(),
                topic.getCategory().getName(),
                topic.getCreatedAt(),
                topic.getUpdatedAt()
        );
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }
}