package com.openclassrooms.mddapi.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.dto.CreateCommentRequest;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.ICommentService;

import jakarta.validation.Valid;

/**
 * Expose les endpoints de lecture et de création des commentaires d'un article
 * ({@code GET/POST /api/posts/{postId}/comments}).
 */
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@Tag(name = "Commentaires", description = "Lecture et ajout de commentaires sur un article")
public class CommentController {

    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Commentaires d'un article")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste de commentaires (peut être vide)"),
        @ApiResponse(responseCode = "404", description = "Article introuvable")
    })
    @GetMapping
    public List<CommentDto> getByPost(@PathVariable Long postId) {
        return commentService.getByPost(postId);
    }

    @Operation(summary = "Ajouter un commentaire")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Commentaire créé"),
        @ApiResponse(responseCode = "400", description = "Contenu manquant"),
        @ApiResponse(responseCode = "404", description = "Article introuvable")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable Long postId,
                             @Valid @RequestBody CreateCommentRequest request,
                             @AuthenticationPrincipal User user) {
        return commentService.create(postId, request, user.getEmail());
    }
}
