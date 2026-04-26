package io.noteshare.auth.controller;

import io.noteshare.auth.dto.AuthResponse;
import io.noteshare.auth.dto.LoginRequest;
import io.noteshare.auth.dto.MessageResponse;
import io.noteshare.auth.dto.RegisterRequest;
import io.noteshare.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean AuthService authService;
    @Autowired
    ObjectMapper mapper;

    @Test
    void registerReturnsCreated() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new MessageResponse("Registration successful. Please check your email to verify your account."));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RegisterRequest("test@email.com", "securepass"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful. Please check your email to verify your account."));
    }

    @Test
    void registerReturnsBadRequestOnInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RegisterRequest("not-an-email", "securepass"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginReturnsOk() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new LoginRequest("test@email.com", "securepass"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void verifyReturnsOk() throws Exception {
        when(authService.verify("valid-token"))
                .thenReturn(new MessageResponse("Account verified. You can now log in."));

        mockMvc.perform(get("/api/auth/verify").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account verified. You can now log in."));
    }
}
