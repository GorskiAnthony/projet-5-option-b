package com.openclassrooms.mddapi.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;

/**
 * Tests d'intégration couvrant les contrôleurs REST :
 * UserController, TopicController, PostController, CommentController
 * et GlobalExceptionHandler (400, 404, 409).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Controllers — tests d'intégration")
class ControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TopicRepository topicRepository;

    String jwtToken;
    Long topicId;
    Long postId;

    private HttpHeaders authHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", "Bearer " + jwtToken);
        h.set("Content-Type", "application/json");
        return h;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.set("Content-Type", "application/json");
        return h;
    }

    @BeforeAll
    void setup() {
        // Insère un topic de test directement via le repository
        Topic topic = new Topic();
        topic.setName("Java");
        topic.setDescription("Langage de programmation");
        topicId = topicRepository.save(topic).getId();

        // Inscrit un utilisateur et récupère le JWT
        String body = """
                {"username":"ctrl_test","email":"ctrl_test@mdd.com","password":"Test1234!"}
                """;
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), JsonNode.class);
        jwtToken = resp.getBody().get("token").asText();
    }

    // ─────────────────────────────────────────────────────
    // GlobalExceptionHandler — 400, 404, 409
    // ─────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("POST /register — doit retourner 400 quand le mot de passe est invalide (GlobalExceptionHandler 400)")
    void register_shouldReturn400_whenPasswordTooWeak() {
        // Arrange
        String body = """
                {"username":"weak","email":"weak@mdd.com","password":"weak"}
                """;

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(resp.getBody().has("erreurs")).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("POST /register — doit retourner 409 quand l'email est déjà utilisé (GlobalExceptionHandler 409)")
    void register_shouldReturn409_whenEmailDuplicate() {
        // Arrange
        String body = """
                {"username":"ctrl_test2","email":"ctrl_test@mdd.com","password":"Test1234!"}
                """;

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/auth/register", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(CONFLICT);
        assertThat(resp.getBody().get("erreur").asText()).contains("déjà utilisé");
    }

    @Test
    @Order(3)
    @DisplayName("GET /posts/999 — doit retourner 404 quand l'article est introuvable (GlobalExceptionHandler 404)")
    void getPost_shouldReturn404_whenNotFound() {
        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts/999", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(resp.getBody().get("erreur").asText()).contains("trouvé");
    }

    // ─────────────────────────────────────────────────────
    // UserController — GET /me, PUT /me
    // ─────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("GET /users/me — doit retourner le profil de l'utilisateur authentifié (200)")
    void getMe_shouldReturn200WithUserData() {
        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/users/me", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThat(resp.getBody().get("email").asText()).isEqualTo("ctrl_test@mdd.com");
        assertThat(resp.getBody().get("username").asText()).isEqualTo("ctrl_test");
    }

    @Test
    @Order(5)
    @DisplayName("PUT /users/me — doit mettre à jour le nom d'utilisateur (200)")
    void updateProfile_shouldReturn200_whenUsernameChanged() {
        // Arrange
        String body = """
                {"username":"ctrl_updated","email":"ctrl_test@mdd.com"}
                """;

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/users/me", HttpMethod.PUT,
                new HttpEntity<>(body, authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThat(resp.getBody().get("username").asText()).isEqualTo("ctrl_updated");
    }

    @Test
    @Order(6)
    @DisplayName("PUT /users/me — doit retourner 400 quand l'email a un format invalide")
    void updateProfile_shouldReturn400_whenEmailInvalid() {
        // Arrange
        String body = """
                {"email":"not-an-email"}
                """;

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/users/me", HttpMethod.PUT,
                new HttpEntity<>(body, authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    // ─────────────────────────────────────────────────────
    // TopicController — GET, subscribe, unsubscribe
    // ─────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("GET /topics — doit retourner la liste des thèmes (200)")
    void getTopics_shouldReturn200WithTopicList() {
        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/topics", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThat(resp.getBody().isArray()).isTrue();
        assertThat(resp.getBody().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(8)
    @DisplayName("POST /topics/{id}/subscribe — doit s'abonner au thème (204)")
    void subscribe_shouldReturn204_whenTopicExists() {
        // Act
        ResponseEntity<Void> resp = restTemplate.exchange(
                "/api/topics/" + topicId + "/subscribe", HttpMethod.POST,
                new HttpEntity<>(authHeaders()), Void.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @Order(9)
    @DisplayName("DELETE /topics/{id}/subscribe — doit se désabonner du thème (204)")
    void unsubscribe_shouldReturn204_whenTopicExists() {
        // Act
        ResponseEntity<Void> resp = restTemplate.exchange(
                "/api/topics/" + topicId + "/subscribe", HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()), Void.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @Order(10)
    @DisplayName("POST /topics/999/subscribe — doit retourner 404 quand le thème est introuvable")
    void subscribe_shouldReturn404_whenTopicNotFound() {
        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/topics/999/subscribe", HttpMethod.POST,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    // ─────────────────────────────────────────────────────
    // PostController — GET feed, GET by id, POST create
    // ─────────────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("GET /posts/feed — doit retourner 200 avec une liste (vide si pas d'abonnement)")
    void getFeed_shouldReturn200() {
        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts/feed", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThat(resp.getBody().isArray()).isTrue();
    }

    @Test
    @Order(12)
    @DisplayName("POST /posts — doit créer un article et retourner 201")
    void createPost_shouldReturn201_whenValid() {
        // Arrange
        String body = String.format("""
                {"topicId":%d,"title":"Mon premier article","content":"Contenu de test"}
                """, topicId);

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(CREATED);
        assertThat(resp.getBody().get("title").asText()).isEqualTo("Mon premier article");
        postId = resp.getBody().get("id").asLong();
    }

    @Test
    @Order(13)
    @DisplayName("GET /posts/{id} — doit retourner l'article (200)")
    void getPost_shouldReturn200_whenExists() {
        // Arrange
        assertThat(postId).as("L'article doit être créé au test 12").isNotNull();

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts/" + postId, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThat(resp.getBody().get("title").asText()).isEqualTo("Mon premier article");
    }

    @Test
    @Order(14)
    @DisplayName("POST /posts — doit retourner 400 quand le titre est manquant")
    void createPost_shouldReturn400_whenTitleMissing() {
        // Arrange
        String body = String.format("""
                {"topicId":%d,"content":"Contenu sans titre"}
                """, topicId);

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    // ─────────────────────────────────────────────────────
    // CommentController — GET by post, POST create
    // ─────────────────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("GET /posts/{id}/comments — doit retourner la liste des commentaires (200)")
    void getComments_shouldReturn200_whenPostExists() {
        // Arrange
        assertThat(postId).as("L'article doit être créé au test 12").isNotNull();

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts/" + postId + "/comments", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThat(resp.getBody().isArray()).isTrue();
    }

    @Test
    @Order(16)
    @DisplayName("POST /posts/{id}/comments — doit créer un commentaire (201)")
    void createComment_shouldReturn201_whenPostExists() {
        // Arrange
        assertThat(postId).as("L'article doit être créé au test 12").isNotNull();
        String body = """
                {"content":"Super article !"}
                """;

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts/" + postId + "/comments", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(CREATED);
        assertThat(resp.getBody().get("content").asText()).isEqualTo("Super article !");
    }

    @Test
    @Order(17)
    @DisplayName("GET /posts/999/comments — doit retourner 404 quand l'article est introuvable")
    void getComments_shouldReturn404_whenPostNotFound() {
        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts/999/comments", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    @Order(18)
    @DisplayName("POST /posts/999/comments — doit retourner 404 quand l'article est introuvable")
    void createComment_shouldReturn404_whenPostNotFound() {
        // Arrange
        String body = """
                {"content":"Commentaire orphelin"}
                """;

        // Act
        ResponseEntity<JsonNode> resp = restTemplate.exchange(
                "/api/posts/999/comments", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), JsonNode.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    // ─────────────────────────────────────────────────────
    // Sécurité — accès non authentifié
    // ─────────────────────────────────────────────────────

    @Test
    @Order(19)
    @DisplayName("GET /users/me — doit retourner 401 sans JWT")
    void getMe_shouldReturn401_whenNotAuthenticated() {
        // Act
        ResponseEntity<String> resp = restTemplate.getForEntity("/api/users/me", String.class);

        // Assert
        assertThat(resp.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }
}
