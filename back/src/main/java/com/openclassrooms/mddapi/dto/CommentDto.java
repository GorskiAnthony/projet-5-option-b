package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private String content;
    private String author;
    private Long postId;
    private LocalDateTime createdAt;

    public CommentDto(Long id, String content, String author, Long postId, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public Long getPostId() { return postId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
