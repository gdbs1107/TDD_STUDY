package com.example.demo.user.domain;

import com.example.demo.mock.ClockHolderTest;
import com.example.demo.mock.UuidHolderTest;
import com.example.demo.user.domain.dto.UserCreateDto;
import com.example.demo.user.domain.dto.UserUpdateDto;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UserTest {


    @Test
    public void User는_UserCreateDTO_객체로_생성할_수_있다(){
        //given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("test@test.com")
                .nickname("test")
                .address("test")
                .build();

        /**
         *
         * 가짜 UUID 객체를 주입받은 모습
         *
         * */
        //when
        User user = User.from(userCreateDto, new UuidHolderTest("testssss"));

        //then
        assertThat(user.getId()).isEqualTo(null);
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getNickname()).isEqualTo("test");
        assertThat(user.getAddress()).isEqualTo("test");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("testssss");

    }

    @Test
    public void User는_UserUpdateDTO_객체로_수정할_수_있다(){
        //given
        User exuser =  User.builder()
                .email("test@test.com")
                .nickname("a")
                .address("a")
                .certificationCode("test")
                .status(UserStatus.ACTIVE)
                .build();

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname("test")
                .address("test")
                .build();

        //when
        User user = exuser.update(userUpdateDto);

        //then
        assertThat(user.getId()).isEqualTo(null);
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getNickname()).isEqualTo("test");
        assertThat(user.getAddress()).isEqualTo("test");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo("test");
    }

    @Test
    public void User는_로그인을_할_수_있고_로그인시_마지막_시간이_변경된다(){
        //given
        User user =  User.builder()
                .email("test@test.com")
                .nickname("a")
                .address("a")
                .certificationCode("test")
                .status(UserStatus.ACTIVE)
                .build();

        //when
        user = user.login(new ClockHolderTest(1L));

        //then
        assertThat(user.getLastLoginAt()).isEqualTo(1L);
    }


    @Test
    public void User는_유효한_인증코드로_계정을_활성화_할_수_있다(){
        //given
        User user =  User.builder()
                .email("test@test.com")
                .nickname("a")
                .address("a")
                .certificationCode("test")
                .status(UserStatus.PENDING)
                .certificationCode("asdjvfjvgnvfvgnvjgnjgn")
                .build();

        //when
        user = user.certificate("asdjvfjvgnvfvgnvjgnjgn");

        //then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @Test
    public void User는_잘못된_인증코드로_계정을_활성화_할_수_없다(){
        //given
        User user =  User.builder()
                .email("test@test.com")
                .nickname("a")
                .address("a")
                .certificationCode("test")
                .status(UserStatus.PENDING)
                .certificationCode("asdjvfjvgnvfvgnvjgnjgn")
                .build();

        //when
        assertThatThrownBy(()->
            user.certificate("asdjvfjvgnvfvgfakenvjgnjgn")
        ).isInstanceOf(CertificationCodeNotMatchedException.class);

        //then
    }
}
