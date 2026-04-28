package io.noteshare.mailer.listener;

import io.noteshare.mailer.dto.UserRegisteredEvent;
import io.noteshare.mailer.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRegisteredListenerTest {

    @Mock
    EmailService emailService;

    @InjectMocks
    UserRegisteredListener listener;

    @Test
    void handleUserRegistered_callsEmailServiceWithEvent() {
        UserRegisteredEvent event = new UserRegisteredEvent("user@test.com", "abc-123");

        listener.handleUserRegistered(event);

        verify(emailService).sendVerificationEmail(event);
    }


}
