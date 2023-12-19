package com.project.library.security.otp;

import com.project.library.exception.BadRequestException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOTPEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);

        javaMailSender.send(message);
    }
    public void sendOTP(String email, String OTP,String reason)
            throws UnsupportedEncodingException, BadRequestException, MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("testing.dev.monty@outlook.com", "Library Registration");
        helper.setTo(email);

        String subject = "Here's your One Time Password (OTP)";

        String content = "<p>Hello " + email + "</p>"
                + "<p>Please use this One Time Password "
                + "to "+reason+":</p>"
                + "<p><b>" + OTP + "</b></p>"
                + "<br>"
                + "<p>Note: this OTP is valid for one use only!</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }
}

