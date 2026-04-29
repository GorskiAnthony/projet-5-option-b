package com.openclassrooms.mddapi.dto;

public class CreatePostRequest {

    private Long topicId;
    private String title;
    private String content;

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
