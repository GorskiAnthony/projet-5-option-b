package com.openclassrooms.mddapi.dto;

public class TopicDto {

    private Long id;
    private String name;
    private String description;
    private boolean subscribed;

    public TopicDto(Long id, String name, String description, boolean subscribed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subscribed = subscribed;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isSubscribed() { return subscribed; }
}
