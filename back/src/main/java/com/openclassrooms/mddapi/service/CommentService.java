package com.openclassrooms.mddapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Implémentation de {@link ICommentService}.
 * Gère la lecture et la création des commentaires par article.
 */
@Service
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CommentDto> getByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto create(Long postId, CreateCommentRequest request, String authorEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(request.getContent());
        comment.setAuthor(author.getRealUsername());
        comment.setCreatedAt(LocalDateTime.now());
        return toDto(commentRepository.save(comment));
    }

    private CommentDto toDto(Comment c) {
        return new CommentDto(c.getId(), c.getContent(), c.getAuthor(),
                c.getPost().getId(), c.getCreatedAt());
    }
}
