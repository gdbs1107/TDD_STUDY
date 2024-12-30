package com.example.demo.mock;

import com.example.demo.user.service.port.MailSender;

public class FakeMailSender implements MailSender {
    @Override
    public void send(String email, String title, String content) {
    }
}
