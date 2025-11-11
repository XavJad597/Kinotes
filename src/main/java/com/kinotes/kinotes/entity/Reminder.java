package com.kinotes.kinotes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Reminder entity representing the reminders table.
 * Each reminder is associated with a note and has a scheduled time.
 */
@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reminder_at", nullable = false)
    private OffsetDateTime reminderAt;

    @Column(name = "is_triggered", nullable = false)
    @Builder.Default
    private Boolean isTriggered = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Many reminders belong to one note
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;
}
