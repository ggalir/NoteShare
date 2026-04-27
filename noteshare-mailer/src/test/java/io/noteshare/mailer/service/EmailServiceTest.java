package io.noteshare.mailer.service;

import io.noteshare.mailer.dto.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender mailSender;

    EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, "http://localhost:8081/api/auth/verify");
    }

    @Test
    void sendVerificationEmail_sendsToCorrectAddress() {
        UserRegisteredEvent event = new UserRegisteredEvent("user@test.com", "abc-123");

        emailService.sendVerificationEmail(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertNotNull(sent.getTo());
        assertEquals("user@test.com", sent.getTo()[0]);
    }

    @Test
    void sendVerificationEmail_hasCorrectSubject() {
        UserRegisteredEvent event = new UserRegisteredEvent("user@test.com", "abc-123");

        emailService.sendVerificationEmail(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertEquals("Verify your NoteShare account", captor.getValue().getSubject());
    }

    @Test
    void sendVerificationEmail_bodyContainsVerificationLink() {
        UserRegisteredEvent event = new UserRegisteredEvent("user@test.com", "abc-123");

        emailService.sendVerificationEmail(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        String body = captor.getValue().getText();
        assertNotNull(body);
        assertTrue(body.contains("http://localhost:8081/api/auth/verify?token=abc-123"));
    }

    @Test
    void sendVerificationEmail_bodyContainsToken() {
        UserRegisteredEvent event = new UserRegisteredEvent("user@test.com", "unique-token-xyz");

        emailService.sendVerificationEmail(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertTrue(Objects.requireNonNull(captor.getValue().getText()).contains("unique-token-xyz"));
    }
}
