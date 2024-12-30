package com.example.demo.common.service.port;


import org.springframework.stereotype.Component;

/**
 *
 * 이렇게 Clock을 추상화한다
 *
 * 이렇게 하지 않으면 테스트가 불가능함
 * UUID.random이 같은지 검사해야하는데 -> 얘는 호출할때마다 바뀌는데????
 * 그렇기 때문에 가짜 객체를 만들어야하는데 이 과정에 MOCK을 사용해야함
 *
 * 별로다 의존성 역전을 사용하자
 *
 *
 * */
public interface ClockHolder {

    long millis();
}
