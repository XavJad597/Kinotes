package com.kinotes.kinotes.note.client;

import com.kinotes.kinotes.entity.dto.NoteRequest;
import com.kinotes.kinotes.entity.dto.NoteResponse;
import com.kinotes.kinotes.note.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Note operations
 * Exposes endpoints under /api/notes
 */
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteResource {

    private static final Logger logger = LoggerFactory.getLogger(NoteResource.class);
    private final NoteService noteService;

    /**
     * Create a new note
     * POST /api/notes
     */
    @PostMapping("/create-note")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest noteRequest) {
        logger.info("POST /api/notes - Creating note with title: {}", noteRequest.getTitle());
        NoteResponse response = noteService.createNote(noteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all notes
     * GET /api/notes
     */
    @GetMapping("/all-notes")
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        logger.info("GET /api/notes - Fetching all notes");
        List<NoteResponse> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }

    /**
     * Get a note by ID
     * GET /api/notes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable UUID id) {
        logger.info("GET /api/notes/{} - Fetching note by id", id);
        NoteResponse response = noteService.getNoteById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all notes by user ID
     * GET /api/notes/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NoteResponse>> getNotesByUserId(@PathVariable UUID userId) {
        logger.info("GET /api/notes/user/{} - Fetching notes by user id", userId);
        List<NoteResponse> notes = noteService.getNotesByUserId(userId);
        return ResponseEntity.ok(notes);
    }

    /**
     * Update an existing note
     * PUT /api/notes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable UUID id,
            @Valid @RequestBody NoteRequest noteRequest) {
        logger.info("PUT /api/notes/{} - Updating note", id);
        NoteResponse response = noteService.updateNote(id, noteRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a note by ID
     * DELETE /api/notes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable UUID id) {
        logger.info("DELETE /api/notes/{} - Deleting note", id);
        noteService.deleteNoteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search notes by title
     * GET /api/notes/search?userId={userId}&term={searchTerm}
     */
    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> searchNotesByTitle(
            @RequestParam UUID userId,
            @RequestParam String term) {
        logger.info("GET /api/notes/search - Searching notes for user {} with term: {}", userId, term);
        List<NoteResponse> notes = noteService.searchNotesByTitle(userId, term);
        return ResponseEntity.ok(notes);
    }
}
