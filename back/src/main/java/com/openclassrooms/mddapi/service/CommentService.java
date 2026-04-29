package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.repository.CommentRepository;

@Service
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<CommentDto> getByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getContent(),
                        c.getAuthor(),
                        c.getPost().getId(),
                        c.getCreatedAt()
                ))
                .toList();
    }
}
