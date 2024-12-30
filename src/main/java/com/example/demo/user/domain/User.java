package com.example.demo.user.domain;

import com.example.demo.user.domain.dto.UserCreateDto;
import com.example.demo.user.domain.dto.UserUpdateDto;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.Clock;
import java.util.UUID;

/**
 *
 * User 객체의 필요성에 대하여
 *
 * 현재는 그냥 엔티티가 존재하고 그 엔티티의 처리를 모두 서비스 로직에서 실행하는데
 * 이러면 도메인 객체의 역할이 매우 수동적으로 변하게 됨 -> 서비스의 부담이 너무 커진다
 *
 * 그렇기 때문에 이를 구현하는 User 객체를 만들고
 * User의 CRUD가 필요한 로직을 모두 JPA와 구별되는 User 객체로서 만드는 거임
 * 해당 객체는 오직 Lombok만 의존하기 때문에
 * 테스트 할 때 spring boot에 의존할 필요가 없음
 *
 *
 * */
@Getter
public class User {

    private final Long id;

    private final String email;

    private final String nickname;

    private final String address;

    private final String certificationCode;

    private final UserStatus status;

    private final Long lastLoginAt;



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

    public static User from(UserCreateDto userCreateDto){

        return User.builder()
                .email(userCreateDto.getEmail())
                .nickname(userCreateDto.getNickname())
                .address(userCreateDto.getAddress())
                .certificationCode(UUID.randomUUID().toString())
                .status(UserStatus.PENDING)
                .build();
    }

    public User update(UserUpdateDto userUpdateDto) {
        return User.builder()
                .id(id)
                .email(email)
                .nickname(userUpdateDto.getNickname())
                .address(userUpdateDto.getAddress())
                .certificationCode(certificationCode)
                .status(status)
                .lastLoginAt(lastLoginAt)
                .build();
    }


    public User login(){
        return User.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .address(address)
                .certificationCode(certificationCode)
                .status(status)
                .lastLoginAt(Clock.systemUTC().millis())
                .build();
    }


    public User certificate(String certificationCode){

        if (!this.certificationCode.equals(certificationCode)) {
            throw new CertificationCodeNotMatchedException();
        }


        return User.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .address(address)
                .certificationCode(certificationCode)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(Clock.systemUTC().millis())
                .build();
    }
}
