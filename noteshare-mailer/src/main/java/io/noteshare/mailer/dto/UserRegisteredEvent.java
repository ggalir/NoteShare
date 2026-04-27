package io.noteshare.mailer.dto;

import java.io.Serializable;

public record UserRegisteredEvent(String email, String verificationToken) implements Serializable {}
