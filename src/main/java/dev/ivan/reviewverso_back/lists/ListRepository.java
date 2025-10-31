package dev.ivan.reviewverso_back.lists;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListRepository extends JpaRepository<ListEntity, Long> {
    
    @Query("SELECT l FROM ListEntity l WHERE l.user.idUser = :userId ORDER BY l.createdAt DESC")
    List<ListEntity> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT l FROM ListEntity l JOIN FETCH l.items WHERE l.idList = :id")
    ListEntity findByIdWithItems(@Param("id") Long id);
}
