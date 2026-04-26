package io.noteshare.auth.service;

import io.noteshare.auth.dto.AuthResponse;
import io.noteshare.auth.dto.LoginRequest;
import io.noteshare.auth.dto.MessageResponse;
import io.noteshare.auth.dto.RegisterRequest;
import io.noteshare.auth.event.UserRegisteredEvent;
import io.noteshare.auth.model.User;
import io.noteshare.auth.repository.UserRepository;
import io.noteshare.auth.security.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static io.noteshare.auth.config.RabbitMQConfig.QUEUE_NAME;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RabbitTemplate rabbitTemplate,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        userRepository.save(user);
        rabbitTemplate.convertAndSend(QUEUE_NAME,
                new UserRegisteredEvent(user.getEmail(), user.getVerificationToken()));
        return new MessageResponse("Registration successful. Please check your email to verify your account.");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Account not verified");
        }
        return new AuthResponse(jwtUtil.generateToken(user.getId()));
    }

    @Transactional
    public MessageResponse verify(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return new MessageResponse("Account verified. You can now log in.");
    }
}
