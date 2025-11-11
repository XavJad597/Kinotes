package com.kinotes.kinotes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Note entity representing the notes table.
 * Contains title, content, and image URLs stored as JSONB.
 */
@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * Image URLs stored as JSONB array
     * Hibernate 6+ uses @JdbcTypeCode for JSON mapping
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_urls", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Many notes belong to one user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * One note can have many reminders
     */
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    /**
     * Helper method to add a reminder
     */
    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
        reminder.setNote(this);
    }

    /**
     * Helper method to remove a reminder
     */
    public void removeReminder(Reminder reminder) {
        reminders.remove(reminder);
        reminder.setNote(null);
    }
}
