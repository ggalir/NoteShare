package io.noteshare.sharing.controller;

import io.noteshare.sharing.dto.NoteDto;
import io.noteshare.sharing.dto.ShareResponse;
import io.noteshare.sharing.exception.ShareNotFoundException;
import io.noteshare.sharing.security.JwtUtil;
import io.noteshare.sharing.service.ShareService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShareController.class)
class ShareControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ShareService shareService;

    @MockitoBean
    JwtUtil jwtUtil;

    @Test
    @WithMockUser
    void createShare_returns201() throws Exception {
        ShareResponse response = new ShareResponse(1L, 42L, "test-uuid", LocalDateTime.now());
        when(shareService.createShare(42L)).thenReturn(response);

        mockMvc.perform(post("/api/share/42"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-uuid"))
                .andExpect(jsonPath("$.noteId").value(42));
    }

    @Test
    void resolveShare_returns200() throws Exception {
        NoteDto note = new NoteDto(42L, 1L, "Title", "Content", null, null);
        when(shareService.resolveShare("test-token")).thenReturn(note);

        mockMvc.perform(get("/api/share/test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void resolveShare_returns404ForUnknownToken() throws Exception {
        when(shareService.resolveShare("bad-token")).thenThrow(new ShareNotFoundException());

        mockMvc.perform(get("/api/share/bad-token"))
                .andExpect(status().isNotFound());
    }
}
