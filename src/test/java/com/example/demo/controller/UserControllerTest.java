package com.example.demo.controller;

import com.example.demo.UserEntity;
import com.example.demo.UserRepository;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserUpdateDto;
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

    @Autowired
    private UserRepository userRepository;
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
        UserEntity userEntity = userRepository.findById(2L).get();
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
