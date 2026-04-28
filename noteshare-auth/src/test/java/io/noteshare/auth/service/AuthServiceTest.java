package io.noteshare.auth.service;

import io.noteshare.auth.dto.AuthResponse;
import io.noteshare.auth.dto.LoginRequest;
import io.noteshare.auth.dto.MessageResponse;
import io.noteshare.auth.dto.RegisterRequest;
import io.noteshare.auth.event.UserRegisteredEvent;
import io.noteshare.auth.exception.AccountNotVerifiedException;
import io.noteshare.auth.exception.EmailAlreadyRegisteredException;
import io.noteshare.auth.exception.InvalidCredentialsException;
import io.noteshare.auth.exception.InvalidVerificationTokenException;
import io.noteshare.auth.model.User;
import io.noteshare.auth.repository.UserRepository;
import io.noteshare.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RabbitTemplate rabbitTemplate;
    @Mock JwtUtil jwtUtil;
    @InjectMocks AuthService authService;

    User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@email.com");
        testUser.setPassword("hashed");
        testUser.setVerified(true);
    }

    @Test
    void registerSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        MessageResponse response = authService.register(new RegisterRequest("test@email.com", "password"));

        assertTrue(response.message().contains("check your email"));
        verify(rabbitTemplate).convertAndSend(anyString(), any(UserRegisteredEvent.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerFailsOnDuplicateEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> authService.register(new RegisterRequest("test@email.com", "password")));
    }

    @Test
    void loginSuccess() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(1L)).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("test@email.com", "password"));

        assertEquals("jwt-token", response.token());
    }

    @Test
    void loginFailsWithWrongPassword() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("test@email.com", "wrong")));
    }

    @Test
    void loginFailsIfNotVerified() {
        testUser.setVerified(false);
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);

        assertThrows(AccountNotVerifiedException.class,
                () -> authService.login(new LoginRequest("test@email.com", "password")));
    }

    @Test
    void loginFailsWithUnknownEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("unknown@email.com", "password")));
    }

    @Test
    void verifySuccess() {
        testUser.setVerified(false);
        testUser.setVerificationToken("valid-token");
        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(testUser));

        MessageResponse response = authService.verify("valid-token");

        assertTrue(response.message().contains("verified"));
        assertTrue(testUser.isVerified());
        assertNull(testUser.getVerificationToken());
        verify(userRepository).save(testUser);
    }

    @Test
    void verifyFailsWithInvalidToken() {
        when(userRepository.findByVerificationToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(InvalidVerificationTokenException.class,
                () -> authService.verify("bad-token"));
    }
}
