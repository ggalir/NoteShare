package io.noteshare.notes.service;

import io.noteshare.notes.dto.NoteRequest;
import io.noteshare.notes.dto.NoteResponse;
import io.noteshare.notes.exception.ForbiddenException;
import io.noteshare.notes.exception.NoteNotFoundException;
import io.noteshare.notes.model.Note;
import io.noteshare.notes.repository.NoteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    private Long getCurrentUserId() {
        return (Long) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    @Transactional
    public NoteResponse createNote(NoteRequest request) {
        Note note = new Note();
        note.setUserId(getCurrentUserId());
        note.setTitle(request.title());
        note.setContent(request.content());
        return toResponse(noteRepository.save(note));
    }

    public List<NoteResponse> getNotes() {
        return noteRepository.findAllByUserId(getCurrentUserId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public NoteResponse getNote(Long id) {
        Note note = findOrThrow(id);
        if (!note.getUserId().equals(getCurrentUserId())) {
            throw new ForbiddenException();
        }
        return toResponse(note);
    }

    public NoteResponse getNoteInternal(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public NoteResponse updateNote(Long id, NoteRequest request) {
        Note note = findOrThrow(id);
        if (!note.getUserId().equals(getCurrentUserId())) {
            throw new ForbiddenException();
        }
        note.setTitle(request.title());
        note.setContent(request.content());
        return toResponse(noteRepository.save(note));
    }

    @Transactional
    public void deleteNote(Long id) {
        Note note = findOrThrow(id);
        if (!note.getUserId().equals(getCurrentUserId())) {
            throw new ForbiddenException();
        }
        noteRepository.delete(note);
    }

    private Note findOrThrow(Long id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    private NoteResponse toResponse(Note note) {
        return new NoteResponse(note.getId(), note.getUserId(), note.getTitle(), note.getContent(),
                note.getCreatedAt(), note.getUpdatedAt());
    }
}
