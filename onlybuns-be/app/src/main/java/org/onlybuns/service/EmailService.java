package org.onlybuns.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            // Create a MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("onlybuns.app.activation@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content

            // Send the email
            javaMailSender.send(message);
            System.out.println("Email sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            // Log error or handle exception
        }
    }
}
