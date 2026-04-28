package io.noteshare.sharing.dto;

import java.time.LocalDateTime;

public record NoteDto(Long id, Long userId, String title, String content,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {}
