package io.noteshare.mailer.service;

import io.noteshare.mailer.dto.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String verifyUrl;

    public EmailService(JavaMailSender mailSender,
                        @Value("${auth.verify.url}") String verifyUrl) {
        this.mailSender = mailSender;
        this.verifyUrl = verifyUrl;
    }

    public void sendVerificationEmail(UserRegisteredEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.email());
        message.setSubject("Verify your NoteShare account");
        message.setText(buildEmailBody(event.verificationToken()));
        mailSender.send(message);
    }

    private String buildEmailBody(String token) {
        return "Welcome to NoteShare!\n\n"
                + "Please verify your account by clicking the link below:\n\n"
                + verifyUrl + "?token=" + token + "\n\n"
                + "If you did not create an account, you can ignore this email.";
    }
}
