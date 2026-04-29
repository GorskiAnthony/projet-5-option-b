package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDto;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;

@Service
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    public PostService(PostRepository postRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    public List<PostDto> getFeed() {
        return postRepository.findAll().stream()
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
    public PostDto create(CreatePostRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic non trouvé"));
        Post post = new Post();
        post.setTopic(topic);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor("anonymous"); // remplacé par l'utilisateur connecté quand l'auth sera prête
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
