package com.kinotes.kinotes.note.service;

import com.kinotes.kinotes.entity.dto.NoteRequest;
import com.kinotes.kinotes.entity.dto.NoteResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Note CRUD operations
 */
public interface NoteService {

    /**
     * Create a new note
     * @param noteRequest the note data
     * @return the created note response
     */
    NoteResponse createNote(NoteRequest noteRequest);

    /**
     * Get all notes
     * @return list of all notes
     */
    List<NoteResponse> getAllNotes();

    /**
     * Get a note by ID
     * @param id the note ID
     * @return the note response
     */
    NoteResponse getNoteById(UUID id);

    /**
     * Get all notes by user ID
     * @param userId the user ID
     * @return list of notes belonging to the user
     */
    List<NoteResponse> getNotesByUserId(UUID userId);

    /**
     * Update an existing note
     * @param id the note ID
     * @param noteRequest the updated note data
     * @return the updated note response
     */
    NoteResponse updateNote(UUID id, NoteRequest noteRequest);

    /**
     * Delete a note by ID
     * @param id the note ID
     */
    void deleteNoteById(UUID id);

    /**
     * Search notes by title
     * @param userId the user ID
     * @param searchTerm the search term
     * @return list of matching notes
     */
    List<NoteResponse> searchNotesByTitle(UUID userId, String searchTerm);
}
