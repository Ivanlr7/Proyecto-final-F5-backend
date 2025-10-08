package dev.ivan.reviewverso_back.register.dto;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RegisterRequestDTOTest {
    @Test
    void testRegisterRequestDTOFields() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
            "testuser",
            "test@email.com",
            "password123",
            "image.png",
            Set.of("USER", "ADMIN")
        );
        assertThat(dto.userName(), is("testuser"));
        assertThat(dto.email(), is("test@email.com"));
        assertThat(dto.password(), is("password123"));
        assertThat(dto.profileImage(), is("image.png"));
        assertThat(dto.roles(), containsInAnyOrder("USER", "ADMIN"));
    }
}
