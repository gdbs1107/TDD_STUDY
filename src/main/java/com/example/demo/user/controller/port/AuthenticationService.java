package com.example.demo.user.controller.port;

import org.springframework.transaction.annotation.Transactional;

public interface AuthenticationService {


    @Transactional
    void login(long id);

    @Transactional
    void verifyEmail(long id, String certificationCode);
}
