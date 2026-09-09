package com.guidev1911.expl_world_api.topic.repository;

import com.guidev1911.expl_world_api.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Optional<Topic> findByCategoryIdAndSlug(Long categoryId, String slug);

    Optional<Topic> findByCategoryIdAndSlugAndActiveTrue(
            Long categoryId,
            String slug
    );

    List<Topic> findAllByCategoryIdAndActiveTrueOrderByNameAsc(
            Long categoryId
    );

    boolean existsByCategoryIdAndSlug(
            Long categoryId,
            String slug
    );

    boolean existsByCategoryIdAndSlugAndIdNot(
            Long categoryId,
            String slug,
            Long id
    );
}