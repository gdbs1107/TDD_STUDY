package com.example.demo.user.service;

import com.example.demo.user.domain.User;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.dto.UserCreateDto;
import com.example.demo.user.domain.dto.UserUpdateDto;

import java.util.Optional;

import com.example.demo.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userJpaRepository;
    private final CertificationService certificationService;

    public User findById(long id) {
        return userJpaRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    public User getByEmail(String email) {
        return userJpaRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", email));
    }

    public User getById(long id) {
        return userJpaRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Transactional
    public User create(UserCreateDto userCreateDto) {


        User user = User.from(userCreateDto);


        user = userJpaRepository.save(user);
        certificationService.send(userCreateDto.getEmail(),user.getId(),user.getCertificationCode());
        return user;
    }

    @Transactional
    public User update(long id, UserUpdateDto userUpdateDto) {
        User user = getById(id);
        user = user.update(userUpdateDto);

        /**
         *
         * 이렇게 하면 JPA의 중요한 특징인 '변경감지'를 사용 할 수 없게됨
         *
         * JPA와 서비스 사이의 구현체가 끼면서
         * 직접적인 의존성이 줄어들었기 때문
         *
         * 하지만 이는 다른 벤더를 이용했을때 변경감지가 없을 수도 있기 때문에
         * 더 포괄적으로 봤을땐 좋을 수 있음
         *
         * 더 직관적이기도 하고
         *
         * */

        user = userJpaRepository.save(user);
        return user;
    }

    @Transactional
    public void login(long id) {
        User user = userJpaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));

        user = user.login();
        userJpaRepository.save(user);
    }

    @Transactional
    public void verifyEmail(long id, String certificationCode) {

        User user = userJpaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));

        if (!certificationCode.equals(user.getCertificationCode())) {

            throw new CertificationCodeNotMatchedException();
        }

        user = user.certificate(certificationCode);

        userJpaRepository.save(user);
    }


}