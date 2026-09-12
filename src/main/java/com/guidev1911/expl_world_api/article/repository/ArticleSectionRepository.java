package com.guidev1911.expl_world_api.article.repository;

import com.guidev1911.expl_world_api.article.entity.ArticleSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleSectionRepository extends JpaRepository<ArticleSection, Long> {

    Page<ArticleSection> findAllByArticleIdAndActiveTrueOrderByDisplayOrderAsc(
            Long articleId,
            Pageable pageable
    );
}