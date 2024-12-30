package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostResponseTest {

    @Test
    public void Post_응답을_생성_할_수_있다(){
        //given
        Post post = Post.builder()
                .writer(
                        User.builder()
                                .email("test@tset.com")
                                .nickname("a")
                                .address("a")
                                .certificationCode(UUID.randomUUID().toString())
                                .status(UserStatus.ACTIVE)
                                .build())
                .content("hello")
                .build();

        //when
        PostResponse postResponse = PostResponse.from(post);

        //then
        assertThat(post.getContent()).isEqualTo("hello");
        assertThat(post.getWriter().getNickname()).isEqualTo("a");
        // 다른 값들도 다 테스트 해준다 FIXME
    }
}
