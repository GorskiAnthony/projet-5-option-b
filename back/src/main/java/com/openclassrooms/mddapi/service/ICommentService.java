package com.openclassrooms.mddapi.service;

import java.util.List;

import com.openclassrooms.mddapi.dto.CommentDto;

public interface ICommentService {

    List<CommentDto> getByPost(Long postId);
}
