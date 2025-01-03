package com.example.demo.user.service;

import com.example.demo.mock.*;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.dto.UserCreateDto;
import com.example.demo.user.domain.dto.UserUpdateDto;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import com.example.demo.user.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

/**
 *
 * 스프링 부트와 H2를 띄우지 않고 테스트가 진행된다 -> 매우 빠름
 *
 * */

public class UserServiceTest {

    private UserServiceImpl userService;

    @BeforeEach
    void init(){
        FakeMailSender fakeMailSender= new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();

        this.userService = UserServiceImpl.builder()
                .clockHolder(new ClockHolderTest(1L))
                .certificationService(new CertificationService(fakeMailSender))
                .uuidHolder(new UuidHolderTest("fadsfvbgtegrwedcfaedwfvfdvdrfakefake"))
                .userRepository(new FakeUserRepository())
                .build();

        fakeUserRepository.save(
                User.builder()
                .id(11L)
                .email("test@test.com")
                .nickname("test")
                .address("test")
                .certificationCode("fadsfvbgtegrwedcfaedwfvfdvdrfakefake")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());

        fakeUserRepository.save(
                User.builder()
                        .id(2L)
                        .email("test1@test.com")
                        .nickname("test1")
                        .address("test1")
                        .certificationCode("fadsfvbgtegrwedcfaedwfvfdvdr")
                        .status(UserStatus.PENDING)
                        .lastLoginAt(0L)
                        .build());
    }

    @Test
    void getByEmail은_액티브_상태인_유저를_찾아올_수_있다(){

        //given
        String email = "test@test.com";

        //when
        User result = userService.getByEmail(email);

        //then
        assertThat(result.getNickname()).isEqualTo("test");
    }

    @Test
    void getByEmail은_팬딩_상태인_유저를_찾아올_수_있다(){

        //given
        String email = "test1@test.com";

        //when
        //then
        assertThatThrownBy(()->{
            userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }



    @Test
    void getById은_액티브_상태인_유저를_찾아올_수_있다(){

        //given
        //when
        User result = userService.getById(11);

        //then
        assertThat(result.getNickname()).isEqualTo("test");
    }

    @Test
    void getById은_팬딩_상태인_유저를_찾아올_수_있다(){

        //given
        String email = "test1@test.com";

        //when
        //then
        assertThatThrownBy(()->{
            User result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }





    @Test
    void UserCreateDTO_를_이용하여_유저를_생성할_수_있다(){

        //given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("test-c@test.com")
                .address("test-c")
                .nickname("test-c")
                .build();

        //when
        User result = userService.create(userCreateDto);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
    }




    @Test
    void UserUpdateDTO_를_이용하여_유저를_생성할_수_있다(){

        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .address("Incheon")
                .nickname("test-update")
                .build();

        //when
        User result = userService.update(11,userUpdateDto);

        //then
        User userEntity = userService.getById(11);
        assertThat(userEntity.getId()).isNotNull();
        assertThat(userEntity.getAddress()).isEqualTo("Incheon");
        assertThat(userEntity.getNickname()).isEqualTo("test-update");
    }




    @Test
    void Login을_할_수_있다(){

        //given

        //when
        userService.login(11);

        //then
        User userEntity = userService.getById(11);
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(0L);
    }


    @Test
    void PENDING_상태의_사용자는_인증코드로_ACTIVE_시킬_수_있다(){

        //given

        //when
        userService.verifyEmail(2, "fadsfvbgtegrwedcfaedwfvfdvdr");

        //then
        User userEntity = userService.getById(2);
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @Test
    void PENDING_상태의_사용자는_잘못된_인증코드를_받으면_에러가_반환된다(){

        //given

        //when

        //then
        assertThatThrownBy(()->{
            userService.verifyEmail(2, "fadsfvbgtegrwedcfaedwfvfdvdrfakefake");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }

    /**
     *
     * private으로 설정한 메서드에 대해서는 테스트코드를 작성하지 않습니다
     * 하기 힘든게 맞다고 볼 수 있습니다
     *
     * -> 문제점이 보이죠?
     *
     * */
}
