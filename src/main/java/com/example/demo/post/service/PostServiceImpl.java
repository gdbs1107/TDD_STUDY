package com.example.demo.post.service;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.post.domain.dto.PostCreateDto;
import com.example.demo.post.domain.dto.PostUpdateDto;

import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Builder
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ClockHolder clockHolder;

    @Override
    public Post getById(long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Posts", id));
    }

    @Override
    public Post create(PostCreateDto postCreateDto) {
        User writer = userRepository.findById(postCreateDto.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("User", postCreateDto.getWriterId()));
        Post post = Post.from(writer,postCreateDto);
        return postRepository.save(post);
    }

    @Override
    public Post update(long id, PostUpdateDto postUpdateDto) {
        Post post = getById(id);
        post = post.update(postUpdateDto);
        return postRepository.save(post);
    }
}