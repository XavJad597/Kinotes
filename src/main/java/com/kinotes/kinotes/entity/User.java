package com.kinotes.kinotes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User entity representing the users table.
 * Note: This entity references Supabase Auth users.
 * The ID is the same as the Supabase Auth user ID.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Primary key - same as Supabase Auth user ID
     * Not auto-generated since it comes from Supabase Auth
     */
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column(name = "role", length = 50)
    @Builder.Default
    private String role = "user";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * One user can have many notes
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    /**
     * Helper method to add a note
     */
    public void addNote(Note note) {
        notes.add(note);
        note.setUser(this);
    }

    /**
     * Helper method to remove a note
     */
    public void removeNote(Note note) {
        notes.remove(note);
        note.setUser(null);
    }
}
