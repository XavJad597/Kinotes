package com.kinotes.kinotes.exception;

import java.util.UUID;

/**
 * Exception thrown when a note is not found
 */
public class NoteNotFoundException extends RuntimeException {
    
    public NoteNotFoundException(UUID id) {
        super("Note not found with id: " + id);
    }
    
    public NoteNotFoundException(String message) {
        super(message);
    }
}
