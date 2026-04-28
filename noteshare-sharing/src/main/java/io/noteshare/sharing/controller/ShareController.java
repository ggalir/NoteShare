package io.noteshare.sharing.controller;

import io.noteshare.sharing.dto.NoteDto;
import io.noteshare.sharing.dto.ShareResponse;
import io.noteshare.sharing.service.ShareService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping("/{noteId}")
    public ResponseEntity<ShareResponse> createShare(@PathVariable Long noteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shareService.createShare(noteId));
    }

    @GetMapping("/{token}")
    public NoteDto resolveShare(@PathVariable String token) {
        return shareService.resolveShare(token);
    }
}
