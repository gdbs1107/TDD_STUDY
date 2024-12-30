package com.example.demo.mock;

import com.example.demo.common.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClockHolderTest implements ClockHolder {

    private final long millis;

    @Override
    public long millis() {
        return millis;
    }
}
