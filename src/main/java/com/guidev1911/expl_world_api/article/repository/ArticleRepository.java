package com.guidev1911.expl_world_api.article.repository;

import com.guidev1911.expl_world_api.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByTopicIdAndSlug(Long topicId, String slug);

    List<Article> findAllByTopicIdAndPublishedTrue(Long topicId);

    boolean existsByTopicIdAndSlug(Long topicId, String slug);
}