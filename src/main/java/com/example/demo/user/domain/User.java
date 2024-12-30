package com.example.demo.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;


@Getter
public class User {

    private Long id;

    private String email;

    private String nickname;

    private String address;

    private String certificationCode;

    private UserStatus status;

    private Long lastLoginAt;



    @Builder
    public User(Long id, String email, String nickname, String address, String certificationCode, UserStatus status, Long lastLoginAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.address = address;
        this.certificationCode = certificationCode;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
    }
}
