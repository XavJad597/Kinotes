package com.kinotes.kinotes.note.application;

import com.kinotes.kinotes.entity.Note;
import com.kinotes.kinotes.entity.dto.NoteRequest;
import com.kinotes.kinotes.note.repository.NoteRepository;
import com.kinotes.kinotes.note.service.NoteService;
import org.hibernate.service.spi.InjectService;

import java.util.UUID;


public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void createNote(NoteRequest note) {
        Note noteEntry = new Note();
        noteEntry.setTitle(noteEntry.getTitle());
        noteEntry.setContent(noteEntry.getContent());
        noteRepository.save(noteEntry);
    }

    @Override
    public Note getNote(int id) {
        return null;
    }

    @Override
    public void updateNote(Note note) {

    }

    @Override
    public <List> void getNotes() {

    }
}
