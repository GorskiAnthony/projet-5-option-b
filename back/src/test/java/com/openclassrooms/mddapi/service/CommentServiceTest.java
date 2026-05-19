package com.openclassrooms.mddapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService — tests unitaires")
class CommentServiceTest {

    @Mock CommentRepository commentRepository;
    @Mock PostRepository postRepository;
    @Mock UserRepository userRepository;
    @InjectMocks CommentService commentService;

    private Post post;
    private User user;

    private User userBob;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Post test");

        user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");
        user.setUsername("alice");

        userBob = new User();
        userBob.setId(2L);
        userBob.setEmail("bob@example.com");
        userBob.setUsername("bob");
    }

    @Test
    @DisplayName("getByPost — doit retourner les commentaires mappés dans l'ordre chronologique")
    void getByPost_shouldReturnMappedCommentsInChronologicalOrder() {
        // Arrange
        Comment c1 = buildComment(1L, "Premier", user,    LocalDateTime.of(2024, 1, 1, 10, 0));
        Comment c2 = buildComment(2L, "Deuxième", userBob, LocalDateTime.of(2024, 1, 2, 10, 0));
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(c1, c2));

        // Act
        List<CommentDto> result = commentService.getByPost(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("Premier");
        assertThat(result.get(1).getContent()).isEqualTo("Deuxième");
    }

    @Test
    @DisplayName("getByPost — doit retourner une liste vide quand le post existe sans commentaires")
    void getByPost_shouldReturnEmptyList_whenPostHasNoComments() {
        // Arrange
        when(postRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L)).thenReturn(List.of());

        // Act
        List<CommentDto> result = commentService.getByPost(1L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getByPost — doit lever une exception quand le post est introuvable")
    void getByPost_shouldThrow_whenPostNotFound() {
        // Arrange
        when(postRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> commentService.getByPost(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Article non trouvé");
    }

    @Test
    @DisplayName("create — doit persister le commentaire avec auteur et post corrects")
    void create_shouldPersistComment_withCorrectAuthorAndPost() {
        // Arrange
        CreateCommentRequest req = new CreateCommentRequest();
        req.setContent("Super article !");

        Comment saved = buildComment(10L, "Super article !", user, LocalDateTime.now());

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        // Act
        CommentDto result = commentService.create(1L, req, "alice@example.com");

        // Assert
        assertThat(result.getContent()).isEqualTo("Super article !");
        assertThat(result.getAuthor()).isEqualTo("alice");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("create — doit lever une exception si le post est introuvable")
    void create_shouldThrow_whenPostNotFound() {
        // Arrange
        CreateCommentRequest req = new CreateCommentRequest();
        req.setContent("Test");
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.create(99L, req, "alice@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Article non trouvé");
    }

    private Comment buildComment(Long id, String content, User author, LocalDateTime createdAt) {
        Comment c = new Comment();
        c.setId(id);
        c.setContent(content);
        c.setAuthor(author);
        c.setPost(post);
        c.setCreatedAt(createdAt);
        return c;
    }
}
