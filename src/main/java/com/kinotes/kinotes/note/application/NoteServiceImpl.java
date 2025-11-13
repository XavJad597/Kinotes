package com.kinotes.kinotes.note.application;

import com.kinotes.kinotes.entity.Note;
import com.kinotes.kinotes.entity.User;
import com.kinotes.kinotes.entity.dto.NoteRequest;
import com.kinotes.kinotes.entity.dto.NoteResponse;
import com.kinotes.kinotes.exception.NoteNotFoundException;
import com.kinotes.kinotes.note.repository.NoteRepository;
import com.kinotes.kinotes.note.service.NoteService;
import com.kinotes.kinotes.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of NoteService interface
 * Provides CRUD operations for notes with proper transaction management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public NoteResponse createNote(NoteRequest noteRequest) {
        logger.info("Creating note with title: {}", noteRequest.getTitle());
        
        Note note = Note.builder()
                .title(noteRequest.getTitle())
                .content(noteRequest.getContent())
                .imageUrls(noteRequest.getImageUrls())
                .build();
        
        // Set user if userId is provided
        if (noteRequest.getUserId() != null) {
            User user = userRepository.findById(noteRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + noteRequest.getUserId()));
            note.setUser(user);
        }
        
        Note savedNote = noteRepository.save(note);
        logger.info("Note created successfully with id: {}", savedNote.getId());
        
        return mapToResponse(savedNote);
    }

    @Override
    public List<NoteResponse> getAllNotes() {
        logger.info("Fetching all notes");
        List<Note> notes = noteRepository.findAll();
        return notes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NoteResponse getNoteById(UUID id) {
        logger.info("Fetching note with id: {}", id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        return mapToResponse(note);
    }

    @Override
    public List<NoteResponse> getNotesByUserId(UUID userId) {
        logger.info("Fetching notes for user id: {}", userId);
        List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NoteResponse updateNote(UUID id, NoteRequest noteRequest) {
        logger.info("Updating note with id: {}", id);
        
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        
        // Update fields
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());
        note.setImageUrls(noteRequest.getImageUrls());
        
        // Update user if userId is provided and different
        if (noteRequest.getUserId() != null) {
            if (note.getUser() == null || !note.getUser().getId().equals(noteRequest.getUserId())) {
                User user = userRepository.findById(noteRequest.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + noteRequest.getUserId()));
                note.setUser(user);
            }
        }
        
        Note updatedNote = noteRepository.save(note);
        logger.info("Note updated successfully with id: {}", updatedNote.getId());
        
        return mapToResponse(updatedNote);
    }

    @Override
    public void deleteNoteById(UUID id) {
        logger.info("Deleting note with id: {}", id);
        
        if (!noteRepository.existsById(id)) {
            throw new NoteNotFoundException(id);
        }
        
        noteRepository.deleteById(id);
        logger.info("Note deleted successfully with id: {}", id);
    }

    @Override
    public List<NoteResponse> searchNotesByTitle(UUID userId, String searchTerm) {
        logger.info("Searching notes for user {} with term: {}", userId, searchTerm);
        List<Note> notes = noteRepository.searchNotesByTitle(userId, searchTerm);
        return notes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps Note entity to NoteResponse DTO
     */
    private NoteResponse mapToResponse(Note note) {
        NoteResponse.NoteResponseBuilder builder = NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .imageUrls(note.getImageUrls())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt());
        
        // Add user information if present
        if (note.getUser() != null) {
            builder.userId(note.getUser().getId())
                   .username(note.getUser().getUsername());
        }
        
        return builder.build();
    }
}
