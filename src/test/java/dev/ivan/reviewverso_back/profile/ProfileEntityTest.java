package dev.ivan.reviewverso_back.profile;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ProfileEntityTest {
    @Test
    void testProfileEntityFields() {
        ProfileEntity profile = ProfileEntity.builder()
            .idProfile(1L)
            .profileImage("image.png")
            .user(null)
            .build();
        assertThat(profile.getIdProfile(), is(1L));
        assertThat(profile.getProfileImage(), is("image.png"));
        assertThat(profile.getUser(), is(nullValue()));
    }
}
