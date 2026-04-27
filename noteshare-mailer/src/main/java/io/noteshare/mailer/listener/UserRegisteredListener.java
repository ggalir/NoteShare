package io.noteshare.mailer.listener;

import io.noteshare.mailer.dto.UserRegisteredEvent;
import io.noteshare.mailer.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    private final EmailService emailService;

    public UserRegisteredListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "user.registered")
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailService.sendVerificationEmail(event);
    }
}
