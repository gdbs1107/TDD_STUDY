package com.example.demo.user.controller;

import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.dto.UserUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    /**
     *
     * H2와 강결합되어 있는 모습 -> 이는 중형 테스트이다
     * 다소 무겁고 유연하지 못함
     *
     * 그럼 당연히 JPA가 필요한 리파지토리에 의존하는 코드를 어떻게 소형 테스트로 전환 할 수 있을까?
     * 의존성 역전! DI가 나올 차례인데
     * 현재 프로젝트 레이어안에서는 이를 구현 할 수 없다
     *
     * 프로젝트에 대규모 변화가 필요한거임
     * JPA 인터페이스의 구현체를 생성하여 대부분의 구현을 담당하게 하고
     * 이를 인터페이스화 시켜서 서비스코드에 대한 리파지토리의 의존성을 대폭 약화시키는거임
     *
     * 근데 왜 그렇게 해야할까? -> 좀 더 근본적인 고민에 다가감
     * 이 부분은 객체지향에 대한 강의를 다시 들어야할 것 같다
     *
     * */
    @Autowired
    private UserJpaRepository userJpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 사용자는_특정_유저의_정보를_전달받을_수_있다() throws Exception {
        //given

        //when

        //then
        mockMvc.perform(get("/api/users/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.nickname").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void 존재하지_않는_유저의_아이디로_api를_호출한_경우_404() throws Exception {
        //given

        //when

        //then
        mockMvc.perform(get("/api/users/121341"))
                .andExpect(status().isNotFound());
                //.andExpect(content().string("오류메세지를 입력하세요")) FIXME
    }


    @Test
    void 사용자는_인증코드로_계정을_활성화_시킬_수_있다() throws Exception {
        //given

        //when

        //then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode","fadsfvbgtegrwedcfaedwfvfdvdr"))
                .andExpect(status().isFound());
        User userEntity = userJpaRepository.findById(2L).get().toModel();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }




    @Test
    void 사용자는_인증코드가_일치하지_않을_경우_권한없음_에러를_반환한다() throws Exception {
        //given

        //when

        //then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode","fadsfvbgtefakefakegrwedcfaedwfvfdvdr"))
                .andExpect(status().isForbidden());
    }


    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고_올_수_있다() throws Exception {
        //given

        //when

        //then
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL","test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.nickname").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.address").value("test"));
    }



    @Test
    void 사용자는_내_정보를_수정할_수_있다() throws Exception {
        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                        .nickname("test-update")
                                .address("test-update")
                                        .build();

        //when

        //then
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL","test@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.nickname").value("test-update"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.address").value("test-update"));
    }
}
