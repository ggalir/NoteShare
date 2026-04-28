package io.noteshare.sharing.client;

import io.noteshare.sharing.dto.NoteDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotesClient {

    private final RestClient restClient;

    public NotesClient(RestClient notesRestClient) {
        this.restClient = notesRestClient;
    }

    public NoteDto getNote(Long noteId) {
        return restClient.get()
                .uri("/api/notes/{id}/internal", noteId)
                .retrieve()
                .body(NoteDto.class);
    }
}
