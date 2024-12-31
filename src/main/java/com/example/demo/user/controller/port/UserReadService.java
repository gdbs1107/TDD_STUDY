package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;

public interface UserReadService {

    User findById(long id);

    User getByEmail(String email);

    User getById(long id);
}
