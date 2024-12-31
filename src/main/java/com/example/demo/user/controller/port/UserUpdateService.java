package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.dto.UserUpdateDto;
import org.springframework.transaction.annotation.Transactional;

public interface UserUpdateService {

    @Transactional
    User update(long id, UserUpdateDto userUpdateDto);

}
