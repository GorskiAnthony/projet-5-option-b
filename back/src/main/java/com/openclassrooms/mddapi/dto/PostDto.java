package com.openclassrooms.mddapi.dto;

import java.time.LocalDateTime;

public class PostDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long topicId;
    private String topicName;
    private LocalDateTime createdAt;

    public PostDto(Long id, String title, String content, String author,
                   Long topicId, String topicName, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.topicId = topicId;
        this.topicName = topicName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public Long getTopicId() { return topicId; }
    public String getTopicName() { return topicName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
