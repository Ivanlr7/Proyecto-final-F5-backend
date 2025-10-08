package dev.ivan.reviewverso_back.profile;

import jakarta.persistence.*;
import lombok.*;
import dev.ivan.reviewverso_back.user.UserEntity;


@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_profile;

	@Column(length = 512)
	private String profileImageUrl;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id_user")
	private UserEntity user;
}
