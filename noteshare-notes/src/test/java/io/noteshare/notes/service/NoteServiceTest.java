package io.noteshare.notes.service;

import io.noteshare.notes.dto.NoteRequest;
import io.noteshare.notes.dto.NoteResponse;
import io.noteshare.notes.exception.ForbiddenException;
import io.noteshare.notes.model.Note;
import io.noteshare.notes.repository.NoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    NoteRepository noteRepository;

    @InjectMocks
    NoteService noteService;

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID, null, List.of())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createNote_assignsUserIdFromSecurityContext() {
        NoteRequest request = new NoteRequest("Title", "Content");
        when(noteRepository.save(any(Note.class))).thenReturn(noteWith(USER_ID, "Title", "Content"));

        NoteResponse response = noteService.createNote(request);

        assertThat(response.userId()).isEqualTo(USER_ID);
        verify(noteRepository).save(argThat(n -> USER_ID.equals(n.getUserId())));
    }

    @Test
    void getNotes_returnsOnlyCurrentUserNotes() {
        when(noteRepository.findAllByUserId(USER_ID)).thenReturn(List.of(noteWith(USER_ID, "T", "C")));

        List<NoteResponse> notes = noteService.getNotes();

        assertThat(notes).hasSize(1);
        verify(noteRepository).findAllByUserId(USER_ID);
    }

    @Test
    void getNote_successIfCorrectUser() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(noteWith(USER_ID, "T", "C")));

        NoteResponse response = noteService.getNote(1L);

        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void getNote_throwsForbiddenIfWrongUser() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(noteWith(OTHER_USER_ID, "T", "C")));

        assertThrows(ForbiddenException.class, () -> noteService.getNote(1L));
    }

    @Test
    void updateNote_throwsForbiddenIfWrongUser() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(noteWith(OTHER_USER_ID, "T", "C")));

        assertThrows(ForbiddenException.class, () -> noteService.updateNote(1L, new NoteRequest("T", "C")));
    }

    @Test
    void deleteNote_throwsForbiddenIfWrongUser() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(noteWith(OTHER_USER_ID, "T", "C")));

        assertThrows(ForbiddenException.class, () -> noteService.deleteNote(1L));
    }

    private Note noteWith(Long userId, String title, String content) {
        Note note = new Note();
        note.setId(1L);
        note.setUserId(userId);
        note.setTitle(title);
        note.setContent(content);
        return note;
    }
}
