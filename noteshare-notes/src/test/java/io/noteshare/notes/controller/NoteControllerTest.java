package io.noteshare.notes.controller;

import io.noteshare.notes.dto.NoteRequest;
import io.noteshare.notes.dto.NoteResponse;
import io.noteshare.notes.exception.ForbiddenException;
import io.noteshare.notes.security.JwtUtil;
import io.noteshare.notes.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    NoteService noteService;

    @MockitoBean
    JwtUtil jwtUtil;

    private static final Long USER_ID = 1L;

    private NoteResponse sampleResponse() {
        return new NoteResponse(1L, USER_ID, "Title", "Content", null, null);
    }

    @Test
    @WithMockUser
    void getNotes_returns200() throws Exception {
        when(noteService.getNotes()).thenReturn(List.of());

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser
    void createNote_returns201() throws Exception {
        NoteRequest request = new NoteRequest("Title", "Content");
        when(noteService.createNote(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getNote_returns200() throws Exception {
        when(noteService.getNote(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void updateNote_returns200() throws Exception {
        NoteRequest request = new NoteRequest("Updated", "Content");
        NoteResponse response = new NoteResponse(1L, USER_ID, "Updated", "Content", null, null);
        when(noteService.updateNote(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    @WithMockUser
    void deleteNote_returns204() throws Exception {
        mockMvc.perform(delete("/api/notes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getNote_returns403ForWrongUser() throws Exception {
        when(noteService.getNote(1L)).thenThrow(new ForbiddenException());

        mockMvc.perform(get("/api/notes/1"))
                .andExpect(status().isForbidden());
    }
}
