package com.openclassrooms.mddapi.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.IPostService;

/**
 * Expose les endpoints de gestion des articles :
 * fil d'actualité, consultation et création.
 */
@RestController
@RequestMapping("/api/posts")
@Tag(name = "Articles", description = "Fil d'actualité, consultation et création d'articles")
public class PostController {

    private final IPostService postService;

    public PostController(IPostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Fil d'articles", description = "Retourne les articles des thèmes auxquels l'utilisateur est abonné, triés par date décroissante. Paramètres de pagination acceptés : page, size, sort.")
    @ApiResponse(responseCode = "200", description = "Liste d'articles (peut être vide)")
    @GetMapping("/feed")
    public List<PostDto> getFeed(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getFeed(user.getEmail(), pageable);
    }

    @Operation(summary = "Détail d'un article")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Article trouvé"),
        @ApiResponse(responseCode = "404", description = "Article introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @Operation(summary = "Créer un article")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Article créé"),
        @ApiResponse(responseCode = "400", description = "Champs invalides"),
        @ApiResponse(responseCode = "404", description = "Thème introuvable")
    })
    @PostMapping
    public ResponseEntity<PostDto> create(@Valid @RequestBody CreatePostRequest request,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.create(request, user.getEmail()));
    }
}
