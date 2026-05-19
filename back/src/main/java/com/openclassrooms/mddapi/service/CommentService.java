package com.openclassrooms.mddapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
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
    @Transactional(readOnly = true)
    public List<CommentDto> getByPost(Long postId) {
        if (!postRepository.existsById(postId))
            throw new ResourceNotFoundException("Article non trouvé");
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto create(Long postId, CreateCommentRequest request, String authorEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé"));
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        return toDto(commentRepository.save(comment));
    }

    private CommentDto toDto(Comment c) {
        return new CommentDto(c.getId(), c.getContent(), c.getAuthor().getRealUsername(),
                c.getPost().getId(), c.getCreatedAt());
    }
}
