package dev.ivan.reviewverso_back.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class SecurityConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ResultMatcher statusIsOneOf(int... allowed) {
        return mvcResult -> {
            int status = mvcResult.getResponse().getStatus();
            for (int s : allowed) {
                if (status == s) {
                    return;
                }
            }
            throw new AssertionError("Unexpected HTTP status: " + status);
        };
    }

    @Test
    @DisplayName("/users requiere autenticación y rol ADMIN")
    void usersEndpointRequiresAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    // @Test
    // @WithMockUser(username = "admin", roles = {"ADMIN"})
    // @DisplayName("/users permite acceso a ADMIN")
    // void usersEndpointAdminAccess() throws Exception {
    //     mockMvc.perform(get("/api/v1/users"))
    //             .andExpect(statusIsOneOf(200, 404));
    //     mockMvc.perform(get("/api/v1/users/1"))
    //             .andExpect(statusIsOneOf(200, 404));
    // }

    @Test
    @DisplayName("/files/** permite acceso público")
    void filesEndpointPermitAll() throws Exception {
        mockMvc.perform(get("/api/v1/files/anyfile.jpg"))
                .andExpect(statusIsOneOf(200, 404));
    }

    @Test
    @DisplayName("/reviews GET permite acceso público, POST requiere autenticación")
    void reviewsEndpointAccess() throws Exception {
        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(statusIsOneOf(200, 404));
        mockMvc.perform(post("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("/reviews POST permite acceso a USER")
    void reviewsPostUserAccess() throws Exception {
        mockMvc.perform(post("/api/v1/reviews").contentType(MediaType.APPLICATION_JSON))
                .andExpect(statusIsOneOf(200, 400));
    }
}
