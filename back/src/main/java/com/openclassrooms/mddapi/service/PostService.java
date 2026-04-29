package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, TopicRepository topicRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PostDto> getFeed(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return postRepository.findAll().stream()
                .filter(p -> user.getSubscriptions().contains(p.getTopic()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toDto)
                .toList();
    }

    @Override
    public PostDto getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post non trouvé"));
        return toDto(post);
    }

    @Override
    public PostDto create(CreatePostRequest request, String authorEmail) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic non trouvé"));
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Post post = new Post();
        post.setTopic(topic);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author.getRealUsername());
        post.setCreatedAt(LocalDateTime.now());
        return toDto(postRepository.save(post));
    }

    private PostDto toDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getTopic().getId(),
                post.getTopic().getName(),
                post.getCreatedAt()
        );
    }
}
