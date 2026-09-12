package com.guidev1911.expl_world_api.article.service;

import com.guidev1911.expl_world_api.article.dto.request.CreateArticleSectionRequest;
import com.guidev1911.expl_world_api.article.dto.request.UpdateArticleSectionRequest;
import com.guidev1911.expl_world_api.article.dto.response.ArticleSectionResponse;
import com.guidev1911.expl_world_api.article.entity.Article;
import com.guidev1911.expl_world_api.article.entity.ArticleSection;
import com.guidev1911.expl_world_api.article.repository.ArticleRepository;
import com.guidev1911.expl_world_api.article.repository.ArticleSectionRepository;
import com.guidev1911.expl_world_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleSectionService {

    private final ArticleSectionRepository sectionRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleSectionResponse create(
            CreateArticleSectionRequest request
    ) {
        Article article = articleRepository.findById(request.articleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Article not found"));

        ArticleSection section = ArticleSection.builder()
                .title(request.title().trim())
                .content(request.content())
                .sectionType(request.sectionType().trim().toUpperCase())
                .displayOrder(request.displayOrder())
                .active(request.active() != null
                        ? request.active()
                        : true)
                .article(article)
                .build();

        return toResponse(sectionRepository.save(section));
    }

    @Transactional(readOnly = true)
    public Page<ArticleSectionResponse> findAllByArticle(
            Long articleId,
            Pageable pageable
    ) {
        if (!articleRepository.existsById(articleId)) {
            throw new ResourceNotFoundException("Article not found");
        }

        return sectionRepository
                .findAllByArticleIdAndActiveTrueOrderByDisplayOrderAsc(
                        articleId,
                        pageable
                )
                .map(this::toResponse);
    }

    @Transactional
    public ArticleSectionResponse update(
            Long id,
            UpdateArticleSectionRequest request
    ) {
        ArticleSection section = sectionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Article section not found"
                        ));

        section.setTitle(request.title().trim());
        section.setContent(request.content());
        section.setSectionType(
                request.sectionType().trim().toUpperCase()
        );

        if (request.displayOrder() != null) {
            section.setDisplayOrder(request.displayOrder());
        }

        if (request.active() != null) {
            section.setActive(request.active());
        }

        return toResponse(sectionRepository.save(section));
    }

    @Transactional
    public void delete(Long id) {
        ArticleSection section = sectionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Article section not found"
                        ));

        section.setActive(false);

        sectionRepository.save(section);
    }

    private ArticleSectionResponse toResponse(
            ArticleSection section
    ) {
        return new ArticleSectionResponse(
                section.getId(),
                section.getTitle(),
                section.getContent(),
                section.getSectionType(),
                section.getDisplayOrder(),
                section.getActive(),
                section.getArticle().getId(),
                section.getCreatedAt(),
                section.getUpdatedAt()
        );
    }
}