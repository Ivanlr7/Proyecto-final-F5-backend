package dev.ivan.reviewverso_back.user;

import java.util.Set;

import dev.ivan.reviewverso_back.role.RoleEntity;
import jakarta.persistence.*;
import lombok.*;

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
    private Long id_user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_users", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email; 
    @Column(nullable = false)
    private String password;


}
