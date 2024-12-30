package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MyProfileResponseTest {

    @Test
    public void User으로_응답객체를_만들_수_있다(){
        //given
        User user = User.builder()
                .email("test@tset.com")
                .nickname("a")
                .address("a")
                .certificationCode(UUID.randomUUID().toString())
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .build();

        //when
        MyProfileResponse from = MyProfileResponse.from(user);

        //then
        assertThat(from.getEmail()).isEqualTo("test@tset.com");
        assertThat(from.getNickname()).isEqualTo("a");
        assertThat(from.getLastLoginAt()).isEqualTo(100L);
        // 다른 값들도 다 테스트 해준다 FIXME
    }
}
