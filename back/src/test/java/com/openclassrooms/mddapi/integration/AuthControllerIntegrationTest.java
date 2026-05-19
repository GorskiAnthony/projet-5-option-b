package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AuthController — tests d'intégration")
class AuthControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    private static String jwtToken;

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register — doit créer l'utilisateur et retourner un JWT (201)")
    void register_shouldReturn201AndJwt() {
        // Arrange
        String body = """
                {"username":"testuser","email":"testuser@mdd.com","password":"Test1234!"}
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Act
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "/api/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                JsonNode.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().has("token")).isTrue();
        jwtToken = response.getBody().get("token").asText();
        assertThat(jwtToken).isNotBlank();
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/login — doit retourner un JWT pour des identifiants valides (200)")
    void login_shouldReturn200AndJwt_whenCredentialsAreValid() {
        // Arrange
        String body = """
                {"identifier":"testuser@mdd.com","password":"Test1234!"}
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Act
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                JsonNode.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().has("token")).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/login — doit retourner 401 pour des identifiants invalides")
    void login_shouldReturn401_whenCredentialsAreInvalid() {
        // Arrange
        String body = """
                {"identifier":"testuser@mdd.com","password":"wrongpassword"}
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/topics — doit retourner 401 sans JWT")
    void getTopics_shouldReturn401_whenNotAuthenticated() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/topics", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/topics — doit retourner 200 avec un JWT valide")
    void getTopics_shouldReturn200_whenAuthenticated() {
        // Arrange — utilise le token généré au test 1
        assertThat(jwtToken).as("Le test de register doit s'exécuter avant").isNotNull();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        // Act
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "/api/topics",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/posts/feed — doit retourner 200 avec un JWT valide")
    void getFeed_shouldReturn200_whenAuthenticated() {
        // Arrange
        assertThat(jwtToken).as("Le test de register doit s'exécuter avant").isNotNull();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        // Act
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                "/api/posts/feed",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }
}
