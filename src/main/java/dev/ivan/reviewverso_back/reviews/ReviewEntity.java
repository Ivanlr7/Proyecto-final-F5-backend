package dev.ivan.reviewverso_back.reviews;

import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review")
    private Long idReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentType contentType;

    @Column(nullable = false, length = 100)
    private String contentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApiSource apiSource;

    @Column(nullable = false, length = 200)
    private String reviewTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reviewText;

    @Column(nullable = false)
    private Double rating;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "review_likes",
        joinColumns = @JoinColumn(name = "review_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<UserEntity> likedByUsers = new HashSet<>();
}
