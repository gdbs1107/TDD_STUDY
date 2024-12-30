package com.example.demo.post.domain;

import com.example.demo.post.domain.dto.PostCreateDto;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PostTest {

    @Test
    public void postCreateDTO_로_게시글을_만들_수_있다(){
        //given

        PostCreateDto postCreateDto = PostCreateDto.builder()
                .writerId(1)
                .content("hello")
                .build();

        User writer =  User.builder()
                .email("test@tset.com")
                .nickname("a")
                .address("a")
                .certificationCode(UUID.randomUUID().toString())
                .status(UserStatus.ACTIVE)
                .build();

        //when
        Post post = Post.from(writer,postCreateDto);

        //then
        assertThat(post.getContent()).isEqualTo("hello");
        assertThat(post.getWriter().getNickname()).isEqualTo("a");
        // 다른 값들도 다 테스트 해준다 FIXME
    }
}
