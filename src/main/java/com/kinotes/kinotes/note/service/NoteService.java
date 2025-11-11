package com.kinotes.kinotes.note.service;

import com.kinotes.kinotes.entity.Note;
import com.kinotes.kinotes.entity.dto.NoteRequest;

public interface NoteService {

    void createNote(NoteRequest note);

    Note getNote(int id);

    void updateNote(Note note);

    <List> void getNotes ();
}
