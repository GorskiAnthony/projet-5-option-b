package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.TopicDto;

public interface ITopicService {

    List<TopicDto> getTopics(String userEmail);

    void subscribe(Long topicId, String userEmail);

    void unsubscribe(Long topicId, String userEmail);
}
