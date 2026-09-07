package com.guidev1911.expl_world_api.article.repository;

import com.guidev1911.expl_world_api.article.entity.ArticleSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleSectionRepository extends JpaRepository<ArticleSection, Long> {

    List<ArticleSection> findAllByArticleIdAndActiveTrueOrderByDisplayOrderAsc(Long articleId);
}