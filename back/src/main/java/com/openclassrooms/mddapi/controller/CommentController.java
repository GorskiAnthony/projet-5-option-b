package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.service.ICommentService;

/**
 * Expose l'endpoint de lecture des commentaires d'un article
 * ({@code GET /api/posts/{postId}/comments}).
 */
@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> getByPost(@PathVariable Long postId) {
        return commentService.getByPost(postId);
    }
}
