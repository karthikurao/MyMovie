package com.moviebooking.controller;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController integration flow")
class UserControllerIntegrationTest {

    private static final String ADMIN_EMAIL = "admin@mymovie.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String SQLITE_DB_ID = UUID.randomUUID().toString().replace("-", "");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:sqlite:file:" + SQLITE_DB_ID + "?mode=memory&cache=shared&journal_mode=WAL&busy_timeout=5000");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "2");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "1");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "60000");
        registry.add("spring.datasource.hikari.auto-commit", () -> "true");
    }

    @Test
    @DisplayName("sign-in and refresh endpoints return rotating tokens")
    void signInAndRefreshRotateTokens() throws Exception {
        Map<String, String> loginPayload = Map.of(
                "email", ADMIN_EMAIL,
                "password", ADMIN_PASSWORD
        );

        MvcResult signinResult = mockMvc.perform(post("/api/users/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode signinJson = objectMapper.readTree(signinResult.getResponse().getContentAsString());
        assertThat(signinJson.path("success").asBoolean()).isTrue();

        String accessToken = signinJson.path("token").asText();
        String refreshToken = signinJson.path("refreshToken").asText();

        assertFalse(accessToken.isBlank(), "signin should return access token");
        assertFalse(refreshToken.isBlank(), "signin should return refresh token");
        assertThat(signinJson.path("email").asText()).isEqualTo(ADMIN_EMAIL);

        Map<String, String> refreshPayload = Map.of("refreshToken", refreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/api/users/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshPayload)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode refreshJson = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String rotatedToken = refreshJson.path("refreshToken").asText();
        String newAccessToken = refreshJson.path("token").asText();

        assertFalse(newAccessToken.isBlank(), "refresh should issue a new access token");
        assertThat(refreshJson.path("email").asText()).isEqualTo(ADMIN_EMAIL);
        assertNotEquals(refreshToken, rotatedToken, "refresh should rotate the refresh token");
    }
}
