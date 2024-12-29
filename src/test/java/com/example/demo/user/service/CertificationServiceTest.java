package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import com.example.demo.user.service.port.MailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CertificationServiceTest {

    /**
     * 의존성 역전을 통해서 Spring과 독립적인 테스트를 구현
     * 굉장히굉장히 빠르다
     * */

    @Test
    public void 이메일과_컨텐츠가_제대로_만들어져서_보내지는지_테스트(){
        //given
        FakeMailSender mailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(mailSender);

        //when
        certificationService.send("test@test.com",1,"fadsfvbgtegrwedcfaedwr");

        //then
        assertThat(mailSender.email).isEqualTo("test@test.com");

    }
}
