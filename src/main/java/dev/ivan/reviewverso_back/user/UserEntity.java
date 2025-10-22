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

      //Lógica para las ids de usuario en el set de likes
        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return idUser != null && idUser.equals(that.idUser);
    }

    @Override
    public int hashCode() {
        return idUser != null ? idUser.hashCode() : 0;
    }

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

    @ManyToMany(mappedBy = "likedByUsers")
    @Builder.Default
    private Set<ReviewEntity> likedReviews = new java.util.HashSet<>();

  
    
}
