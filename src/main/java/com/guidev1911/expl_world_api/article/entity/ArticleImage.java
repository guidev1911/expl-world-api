package com.guidev1911.expl_world_api.article.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "article_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "alt_text", length = 255)
    private String altText;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "article_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_article_image")
    )
    private Article article;
}
