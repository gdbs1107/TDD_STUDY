package com.example.demo.user.service;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.user.controller.port.*;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.CertificationCodeNotMatchedException;
import com.example.demo.user.exception.ResourceNotFoundException;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.dto.UserCreateDto;
import com.example.demo.user.domain.dto.UserUpdateDto;

import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
public class UserServiceImpl implements UserCreateService, UserUpdateService, UserReadService, AuthenticationService {

    private final UserRepository userRepository;
    private final CertificationService certificationService;
    private final UuidHolder uuidHolder;
    private final ClockHolder clockHolder;

    @Override
    public User findById(long id) {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", email));
    }


    @Override
    public User getById(long id) {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }


    @Override
    @Transactional
    public User create(UserCreateDto userCreateDto) {


        /**
         *
         *
         * 이렇게 추상화하기 전에는 UUID를 외부에서 주입하여 사용하여 파라미터안에 들어가지 않는 형태였음
         * User.from(userCreateDto); 이런 모습.
         *
         * 하지만 UUID를 직접 추상화하고 구현하면서 (이게 의존성 역전) 파라미터로 UUID 추상화한 클래스를 주입받게 됨
         *
         * 그러면 테스트 할 때도 Stub이나 fake 객체를 만들어서 테스트가 가능해짐
         *
         *
         * */
        User user = User.from(userCreateDto,uuidHolder);


        user = userRepository.save(user);
        certificationService.send(userCreateDto.getEmail(),user.getId(),user.getCertificationCode());
        return user;
    }


    @Override
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

        user = userRepository.save(user);
        return user;
    }


    @Override
    @Transactional
    public void login(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));

        user = user.login(clockHolder);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void verifyEmail(long id, String certificationCode) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));

        if (!certificationCode.equals(user.getCertificationCode())) {

            throw new CertificationCodeNotMatchedException();
        }

        user = user.certificate(certificationCode);

        userRepository.save(user);
    }


}