package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService — tests unitaires")
class PostServiceTest {

    @Mock PostRepository postRepository;
    @Mock TopicRepository topicRepository;
    @Mock UserRepository userRepository;
    @InjectMocks PostService postService;

    private Topic topic;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        topic = new Topic();
        topic.setId(1L);
        topic.setName("Angular");

        user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");
        user.setUsername("alice");
        user.setSubscriptions(new HashSet<>(Set.of(topic)));

        post = new Post();
        post.setId(1L);
        post.setTitle("Mon article");
        post.setContent("Contenu");
        post.setAuthor(user);
        post.setTopic(topic);
        post.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("getFeed — doit retourner les articles des topics abonnés")
    void getFeed_shouldReturnPostsForSubscribedTopics() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findByTopicsOrderByCreatedAtDesc(user.getSubscriptions()))
                .thenReturn(List.of(post));

        // Act
        List<PostDto> result = postService.getFeed("alice@example.com");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Mon article");
    }

    @Test
    @DisplayName("getFeed — doit lever une exception si l'utilisateur est introuvable")
    void getFeed_shouldThrow_whenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.getFeed("nobody@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Utilisateur non trouvé");
    }

    @Test
    @DisplayName("getById — doit retourner le PostDto quand l'article existe")
    void getById_shouldReturnPostDto_whenExists() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        PostDto result = postService.getById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Mon article");
    }

    @Test
    @DisplayName("getById — doit lever une exception si l'article est introuvable")
    void getById_shouldThrow_whenPostNotFound() {
        // Arrange
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Article non trouvé");
    }

    @Test
    @DisplayName("create — doit persister un article avec auteur et topic corrects")
    void create_shouldPersistPost_withCorrectAuthorAndTopic() {
        // Arrange
        CreatePostRequest req = new CreatePostRequest();
        req.setTopicId(1L);
        req.setTitle("Mon article");
        req.setContent("Contenu");

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        PostDto result = postService.create(req, "alice@example.com");

        // Assert
        assertThat(result.getAuthor()).isEqualTo("alice");
        assertThat(result.getTopicId()).isEqualTo(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("create — doit lever une exception si le topic est introuvable")
    void create_shouldThrow_whenTopicNotFound() {
        // Arrange
        CreatePostRequest req = new CreatePostRequest();
        req.setTopicId(99L);
        when(topicRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.create(req, "alice@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Thème non trouvé");
    }
}
