package dev.ivan.reviewverso_back.user;

import org.junit.jupiter.api.Test;
import java.util.Set;
import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserEntityTest {
    @Test
    void testUserEntityFields() {
        UserEntity user = UserEntity.builder()
            .idUser(1L)
            .userName("testuser")
            .email("test@email.com")
            .password("password123")
            .roles(Set.of())
            .profile(null)
            .build();
        assertThat(user.getIdUser(), is(1L));
        assertThat(user.getUserName(), is("testuser"));
        assertThat(user.getEmail(), is("test@email.com"));
        assertThat(user.getPassword(), is("password123"));
        assertThat(user.getRoles(), is(empty()));
        assertThat(user.getProfile(), is(nullValue()));
    }
}
