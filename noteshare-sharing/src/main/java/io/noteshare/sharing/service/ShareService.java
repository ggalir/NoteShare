package io.noteshare.sharing.service;

import io.noteshare.sharing.client.NotesClient;
import io.noteshare.sharing.dto.NoteDto;
import io.noteshare.sharing.dto.ShareResponse;
import io.noteshare.sharing.exception.ShareNotFoundException;
import io.noteshare.sharing.model.Share;
import io.noteshare.sharing.repository.ShareRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ShareService {

    private final ShareRepository shareRepository;
    private final NotesClient notesClient;

    public ShareService(ShareRepository shareRepository, NotesClient notesClient) {
        this.shareRepository = shareRepository;
        this.notesClient = notesClient;
    }

    @Transactional
    public ShareResponse createShare(Long noteId) {
        Share share = new Share();
        share.setNoteId(noteId);
        share.setToken(UUID.randomUUID().toString());
        return toResponse(shareRepository.save(share));
    }

    public NoteDto resolveShare(String token) {
        Share share = shareRepository.findByToken(token)
                .orElseThrow(ShareNotFoundException::new);
        return notesClient.getNote(share.getNoteId());
    }

    private ShareResponse toResponse(Share share) {
        return new ShareResponse(share.getId(), share.getNoteId(), share.getToken(), share.getCreatedAt());
    }
}
