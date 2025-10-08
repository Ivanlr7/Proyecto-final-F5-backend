package dev.ivan.reviewverso_back.role;

import org.junit.jupiter.api.Test;
import java.util.Set;
import dev.ivan.reviewverso_back.user.UserEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RoleEntityTest {
    @Test
    void testRoleEntityFields() {
        RoleEntity role = RoleEntity.builder()
            .idRole(1L)
            .name("USER")
            .users(Set.of())
            .build();
        assertThat(role.getIdRole(), is(1L));
        assertThat(role.getName(), is("USER"));
        assertThat(role.getUsers(), is(empty()));
    }
}
