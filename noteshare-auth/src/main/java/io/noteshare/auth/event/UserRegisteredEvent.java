package io.noteshare.auth.event;

import java.io.Serializable;

public record UserRegisteredEvent(String email, String verificationToken) implements Serializable {}
