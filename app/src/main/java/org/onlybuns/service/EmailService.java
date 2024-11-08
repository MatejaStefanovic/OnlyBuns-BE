package org.onlybuns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("onlybuns.app.activation@gmail.com");  // Your email address
        message.setTo(toEmail);                   // Recipient's email address
        message.setSubject(subject);              // Subject of the email
        message.setText(body);                    // Body of the email

        javaMailSender.send(message);             // Send the email
    }

}
