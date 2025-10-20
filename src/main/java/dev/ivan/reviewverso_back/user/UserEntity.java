package dev.ivan.reviewverso_back.user;

import java.util.List;
import java.util.Set;

import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.reviews.ReviewEntity;
import jakarta.persistence.*;
import lombok.*;
import dev.ivan.reviewverso_back.profile.ProfileEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_users", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;
    
    @Column(nullable = false, length = 100)
    private String userName;
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    @Column(nullable = false)
    private String password;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ProfileEntity profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews;
}
