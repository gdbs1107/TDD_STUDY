package com.example.demo.repository.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.repository.UserEntity;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Transactional
public class UserServiceTest {


    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void getByEmail은_액티브_상태인_유저를_찾아올_수_있다(){

        //given
        String email = "test@test.com";

        //when
        UserEntity result = userService.getByEmail(email);

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
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }



    @Test
    void getById은_액티브_상태인_유저를_찾아올_수_있다(){

        //given
        //when
        UserEntity result = userService.getById(11);

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
            UserEntity result = userService.getById(2);
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

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        //when
        UserEntity result = userService.create(userCreateDto);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
    }
}
