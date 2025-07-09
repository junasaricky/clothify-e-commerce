package com.ricky.clothingshop.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetLink(String to, String token, String fullName) {
        String link = "https://clothify-e-commerce.onrender.com/reset-password?token=" + token;
        String body = "Hi " + fullName + ",\n\n"
                + "Click the link below to reset your password:\n"
                + link + "\n\n"
                + "This link will expire in 15 minutes.\n\n"
                + "If you did not request a password reset, you can ignore this email.\n\n"
                + "Regards,\nClothify E-Commerce Team";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText(body);

        mailSender.send(message);
    }
}
