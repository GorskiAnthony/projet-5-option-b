package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.CreatePostRequest;
import com.openclassrooms.mddapi.dto.PostDto;

public interface IPostService {

    List<PostDto> getFeed();

    PostDto getById(Long id);

    PostDto create(CreatePostRequest request);
}
