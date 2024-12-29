package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(SpringExtension.class)
@DataJpaTest(showSql = true)
@Sql("/sql/user-repository-test-data.sql")
public class UserJpaRepositoryTest {


    /**
    * userRepository를 테스트하는 코드
     * sql을 미리 작성하여 중복을 방지 할 수 있다

     * @DataJpaTest(showSql = true)
     * 이 코드에 @ExtendWith(SpringExtension.class) 이 포함되어있어서 그냥
     * 저것만 넣어줘도 된다
     *
     *
     * junit의 문법은 따로 공부해야 할 듯
    * */



    @Autowired
    private UserJpaRepository userJpaRepository;



    @Test
    void findByIdAndStatus_로_유저_데이터를_찾아올_수_있다(){

        //given
        //when
        Optional<UserEntity> savedUser = userJpaRepository.findByIdAndStatus(1L, UserStatus.ACTIVE);

        //then
        assertThat(savedUser.isPresent()).isNotNull();

    }



    @Test
    void findByIdAndStatus_데이터가_없으면_optional_Empty_를_내려준다(){

        //given
        //when
        Optional<UserEntity> savedUser = userJpaRepository.findByIdAndStatus(1L, UserStatus.PENDING);

        //then
        assertThat(savedUser.isEmpty()).isTrue();

    }





    @Test
    void findByEmailAndStatus_로_유저_데이터를_찾아올_수_있다(){

        //given
        //when
        Optional<UserEntity> savedUser = userJpaRepository.findByEmailAndStatus("test@test.com", UserStatus.ACTIVE);

        //then
        assertThat(savedUser.isPresent()).isNotNull();

    }



    @Test
    void findByEmailAndStatus_데이터가_없으면_optional_Empty_를_내려준다(){

        //given
        //when
        Optional<UserEntity> savedUser = userJpaRepository.findByEmailAndStatus("test@test.com", UserStatus.PENDING);

        //then
        assertThat(savedUser.isEmpty()).isTrue();

    }
}
