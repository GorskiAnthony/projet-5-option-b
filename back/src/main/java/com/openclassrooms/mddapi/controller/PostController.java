package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.service.IPostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final IPostService postService;

    public PostController(IPostService postService) {
        this.postService = postService;
    }

    @GetMapping("/feed")
    public List<PostDto> getFeed() {
        return postService.getFeed();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PostDto> create(@RequestBody CreatePostRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
