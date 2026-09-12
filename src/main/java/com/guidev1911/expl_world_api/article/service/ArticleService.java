package com.guidev1911.expl_world_api.article.service;

import com.guidev1911.expl_world_api.article.dto.request.CreateArticleRequest;
import com.guidev1911.expl_world_api.article.dto.request.UpdateArticleRequest;
import com.guidev1911.expl_world_api.article.dto.response.ArticleResponse;
import com.guidev1911.expl_world_api.article.entity.Article;
import com.guidev1911.expl_world_api.article.repository.ArticleRepository;
import com.guidev1911.expl_world_api.exception.BusinessException;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import com.guidev1911.expl_world_api.topic.entity.Topic;
import com.guidev1911.expl_world_api.topic.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public ArticleResponse create(CreateArticleRequest request) {

        Topic topic = topicRepository.findById(request.topicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found"));

        if (!topic.getActive()) {
            throw new BusinessException(
                    "Cannot create article in inactive topic"
            );
        }

        String slug = normalizeSlug(request.slug());

        if (articleRepository.existsByTopicIdAndSlug(
                request.topicId(),
                slug
        )) {
            throw new BusinessException(
                    "Article slug already exists in this topic"
            );
        }

        Article article = Article.builder()
                .title(request.title().trim())
                .slug(slug)
                .shortDescription(request.shortDescription().trim())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .published(request.published() != null
                        ? request.published()
                        : false)
                .topic(topic)
                .build();

        return toResponse(articleRepository.save(article));
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> findAllByTopic(
            Long topicId,
            Pageable pageable
    ) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Topic not found");
        }

        return articleRepository
                .findAllByTopicIdAndPublishedTrue(topicId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ArticleResponse findBySlug(
            Long topicId,
            String slug
    ) {

        Article article = articleRepository
                .findByTopicIdAndSlugAndPublishedTrue(
                        topicId,
                        slug
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Article not found"));

        return toResponse(article);
    }

    @Transactional
    public ArticleResponse update(
            Long id,
            UpdateArticleRequest request
    ) {

        Article article = articleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Article not found"));

        Long topicId = request.topicId() != null
                ? request.topicId()
                : article.getTopic().getId();

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found"));

        String slug = normalizeSlug(request.slug());

        if (articleRepository.existsByTopicIdAndSlugAndIdNot(
                topicId,
                slug,
                id
        )) {
            throw new BusinessException(
                    "Article slug already exists in this topic"
            );
        }

        article.setTitle(request.title().trim());
        article.setSlug(slug);
        article.setShortDescription(
                request.shortDescription().trim()
        );
        article.setDescription(request.description());
        article.setImageUrl(request.imageUrl());
        article.setTopic(topic);

        if (request.published() != null) {
            article.setPublished(request.published());
        }

        return toResponse(articleRepository.save(article));
    }

    @Transactional
    public void delete(Long id) {

        Article article = articleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Article not found"));

        article.setPublished(false);

        articleRepository.save(article);
    }

    private ArticleResponse toResponse(Article article) {

        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getShortDescription(),
                article.getDescription(),
                article.getImageUrl(),
                article.getPublished(),
                article.getTopic().getId(),
                article.getTopic().getName(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }
}