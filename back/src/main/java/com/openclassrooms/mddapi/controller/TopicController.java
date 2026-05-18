package com.openclassrooms.mddapi.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.ITopicService;

/**
 * Expose les endpoints de gestion des topics et des abonnements.
 */
@RestController
@RequestMapping("/api/topics")
@Tag(name = "Thèmes", description = "Consultation des thèmes et gestion des abonnements")
public class TopicController {

    private final ITopicService topicService;

    public TopicController(ITopicService topicService) {
        this.topicService = topicService;
    }

    @Operation(summary = "Lister tous les thèmes", description = "Retourne tous les thèmes disponibles avec l'état d'abonnement de l'utilisateur courant.")
    @ApiResponse(responseCode = "200", description = "Liste des thèmes")
    @GetMapping
    public List<TopicDto> getTopics(@AuthenticationPrincipal User user) {
        return topicService.getTopics(user.getEmail());
    }

    @Operation(summary = "S'abonner à un thème")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Abonnement effectué"),
        @ApiResponse(responseCode = "404", description = "Thème introuvable")
    })
    @PostMapping("/{id}/subscribe")
    public ResponseEntity<Void> subscribe(@PathVariable Long id, @AuthenticationPrincipal User user) {
        topicService.subscribe(id, user.getEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Se désabonner d'un thème")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Désabonnement effectué"),
        @ApiResponse(responseCode = "404", description = "Thème introuvable")
    })
    @DeleteMapping("/{id}/subscribe")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long id, @AuthenticationPrincipal User user) {
        topicService.unsubscribe(id, user.getEmail());
        return ResponseEntity.noContent().build();
    }
}
