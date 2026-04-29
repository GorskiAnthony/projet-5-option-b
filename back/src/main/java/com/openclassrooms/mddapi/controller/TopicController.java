package com.openclassrooms.mddapi.controller;

import java.util.List;

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
public class TopicController {

    private final ITopicService topicService;

    public TopicController(ITopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public List<TopicDto> getTopics(@AuthenticationPrincipal User user) {
        return topicService.getTopics(user.getEmail());
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<Void> subscribe(@PathVariable Long id, @AuthenticationPrincipal User user) {
        topicService.subscribe(id, user.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/subscribe")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long id, @AuthenticationPrincipal User user) {
        topicService.unsubscribe(id, user.getEmail());
        return ResponseEntity.ok().build();
    }
}
