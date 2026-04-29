package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class TopicService implements ITopicService {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public TopicService(TopicRepository topicRepository, UserRepository userRepository) {
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TopicDto> getTopics(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return topicRepository.findAll().stream()
                .map(t -> new TopicDto(t.getId(), t.getName(), t.getDescription(),
                        user.getSubscriptions().contains(t)))
                .toList();
    }

    @Override
    @Transactional
    public void subscribe(Long topicId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic non trouvé"));
        user.getSubscriptions().add(topic);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unsubscribe(Long topicId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic non trouvé"));
        user.getSubscriptions().remove(topic);
        userRepository.save(user);
    }
}
