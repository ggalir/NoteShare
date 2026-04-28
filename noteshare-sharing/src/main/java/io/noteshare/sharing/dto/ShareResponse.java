package io.noteshare.sharing.dto;

import java.time.LocalDateTime;

public record ShareResponse(Long id, Long noteId, String token, LocalDateTime createdAt) {}
