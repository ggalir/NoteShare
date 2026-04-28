package io.noteshare.notes.dto;

import java.time.LocalDateTime;

public record NoteResponse(Long id, Long userId, String title, String content,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {}
