package dev.ivan.reviewverso_back.reviews;

import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // Obtiene todas las reseñas de un usuario específico
    List<ReviewEntity> findByUser_IdUser(Long userId);

    // Obtiene todas las reseñas de un contenido específico, como una película
    List<ReviewEntity> findByContentTypeAndContentId(ContentType contentType, String contentId);

    // Calcular el rating promedio de un contenido
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.contentType = :contentType AND r.contentId = :contentId")
    Double calculateAverageRating(@Param("contentType") ContentType contentType, @Param("contentId") String contentId);

    // Contador para el número de reseñas de un contenido
    Long countByContentTypeAndContentId(ContentType contentType, String contentId);

    // Verifica si un usuario ya escribió una reseña sobre un contenido específico
    boolean existsByUser_IdUserAndContentTypeAndContentId(Long userId, ContentType contentType, String contentId);
}
