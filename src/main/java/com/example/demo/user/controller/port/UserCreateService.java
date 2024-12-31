package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.dto.UserCreateDto;
import org.springframework.transaction.annotation.Transactional;

public interface UserCreateService {

    @Transactional
    User create(UserCreateDto userCreateDto);
}
