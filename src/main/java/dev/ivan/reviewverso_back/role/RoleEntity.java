package dev.ivan.reviewverso_back.role;

import dev.ivan.reviewverso_back.user.UserEntity;

import java.util.Set;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_role;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

}