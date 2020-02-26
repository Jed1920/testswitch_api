package com.testswitch_api.testswitchapi.Services

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService constructor(
        var emailSender: JavaMailSender
) {

    fun sendSimpleMessage(to: String, subject: String, text: String) {
        var message = SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}