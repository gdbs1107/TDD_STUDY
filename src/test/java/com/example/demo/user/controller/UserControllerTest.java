package com.example.demo.user.controller;

import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.dto.UserUpdateDto;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {


    @Test
    void 사용자는_특정_유저의_정보를_전달받을_수_있다() throws Exception {
        //given
        UserController userController = UserController.builder()
                .userReadService(new UserReadService() {
                    @Override
                    public User findById(long id) {
                        return null;
                    }

                    @Override
                    public User getByEmail(String email) {
                        return null;
                    }

                    @Override
                    public User getById(long id) {
                        return User.builder()
                                .id(1L)
                                .email("test@test.com")
                                .nickname("test")
                                .address("test")
                                .certificationCode("fadsfvbgtegrwedcfaedwfvfdvdrfakefake")
                                .status(UserStatus.ACTIVE)
                                .lastLoginAt(0L)
                                .build();
                    }
                }).build();

        //when
        ResponseEntity<UserResponse> result = userController.getUserById(1L);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("test@test.com");
        assertThat(result.getBody().getNickname()).isEqualTo("test");
    }

    @Test
    void 존재하지_않는_유저의_아이디로_api를_호출한_경우_404() throws Exception {
        //given
        UserController userController = UserController.builder()
                .userReadService(new UserReadService() {
                    @Override
                    public User findById(long id) {
                        return null;
                    }

                    @Override
                    public User getByEmail(String email) {
                        return null;
                    }

                    @Override
                    public User getById(long id) {
                        throw new ResourceNotFoundException("users",id);
                    }
                }).build();

        //when

        //then
        assertThatThrownBy(()->
                userController.getUserById(13245676)).isInstanceOf(ResourceNotFoundException.class);
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
