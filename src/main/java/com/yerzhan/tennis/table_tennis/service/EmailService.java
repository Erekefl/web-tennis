package com.yerzhan.tennis.table_tennis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendRegistrationConfirmationEmail(String to, String token) {
        String subject = "Подтверждение регистрации";
        String text = "Для подтверждения регистрации перейдите по ссылке: \n"
                + "http://localhost:8080/auth/confirm-registration?token=" + token;
        sendEmail(to, subject, text);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Восстановление пароля";
        String text = "Для восстановления пароля перейдите по ссылке: \n"
                + "http://localhost:8080/auth/reset-password?token=" + token;
        sendEmail(to, subject, text);
    }
} 