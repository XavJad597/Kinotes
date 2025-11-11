package com.kinotes.kinotes.note.client;

import com.kinotes.kinotes.note.service.NoteService;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;
@RestController
@RequestMapping("api/note")
public class NoteResource {

    private final NoteService noteService;

    public NoteResource(final NoteService noteService) {
        this.noteService = noteService;
    }



}
