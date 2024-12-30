package com.example.demo.post.service;

import com.example.demo.post.domain.Post;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.post.domain.dto.PostCreateDto;
import com.example.demo.post.domain.dto.PostUpdateDto;
import com.example.demo.post.infrastructure.PostEntity;
import com.example.demo.user.infrastructure.UserEntity;
import java.time.Clock;

import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public Post getById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    public Post create(PostCreateDto postCreateDto) {
        User writer = userService.getById(postCreateDto.getWriterId());
        Post post = Post.from(writer,postCreateDto);
        return postRepository.save(post);
    }

    public Post update(long id, PostUpdateDto postUpdateDto) {
        Post post = getById(id);
        post = post.update(postUpdateDto);
        return postRepository.save(post);
    }
}