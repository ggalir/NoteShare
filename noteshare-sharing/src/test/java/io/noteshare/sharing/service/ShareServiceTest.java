package io.noteshare.sharing.service;

import io.noteshare.sharing.client.NotesClient;
import io.noteshare.sharing.dto.NoteDto;
import io.noteshare.sharing.dto.ShareResponse;
import io.noteshare.sharing.exception.ShareNotFoundException;
import io.noteshare.sharing.model.Share;
import io.noteshare.sharing.repository.ShareRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareServiceTest {

    @Mock
    ShareRepository shareRepository;

    @Mock
    NotesClient notesClient;

    @InjectMocks
    ShareService shareService;

    @Test
    void createShare_generatesToken() {
        Long noteId = 42L;
        Share saved = shareWith(noteId, "generated-token");
        when(shareRepository.save(any(Share.class))).thenReturn(saved);

        ShareResponse response = shareService.createShare(noteId);

        assertThat(response.noteId()).isEqualTo(noteId);
        assertThat(response.token()).isNotBlank();
        verify(shareRepository).save(argThat(s -> s.getNoteId().equals(noteId) && s.getToken() != null));
    }

    @Test
    void resolveShare_returnsNoteFromNotesService() {
        Share share = shareWith(42L, "test-token");
        NoteDto note = new NoteDto(42L, 1L, "Title", "Content", null, null);
        when(shareRepository.findByToken("test-token")).thenReturn(Optional.of(share));
        when(notesClient.getNote(42L)).thenReturn(note);

        NoteDto result = shareService.resolveShare("test-token");

        assertThat(result.title()).isEqualTo("Title");
        verify(notesClient).getNote(42L);
    }

    @Test
    void resolveShare_throwsIfTokenNotFound() {
        when(shareRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(ShareNotFoundException.class, () -> shareService.resolveShare("bad-token"));
    }

    private Share shareWith(Long noteId, String token) {
        Share share = new Share();
        share.setId(1L);
        share.setNoteId(noteId);
        share.setToken(token);
        share.setCreatedAt(LocalDateTime.now());
        return share;
    }
}
