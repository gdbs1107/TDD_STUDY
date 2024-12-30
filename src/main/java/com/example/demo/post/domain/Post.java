package com.example.demo.post.domain;

import com.example.demo.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;

public class Post {

    private Long id;

    private String content;

    private Long createdAt;

    private Long modifiedAt;

    private UserEntity writer;


    @Builder
    public Post(Long id, String content, Long createdAt, Long modifiedAt, UserEntity writer) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.writer = writer;
    }
}
