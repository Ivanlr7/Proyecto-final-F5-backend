package dev.ivan.reviewverso_back.lists;

import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_list_item")
    private Long idListItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ListEntity list;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentType contentType;

    @Column(nullable = false, length = 100)
    private String contentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApiSource apiSource;

    @Column(nullable = false)
    private Integer position;
}
