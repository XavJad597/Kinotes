package com.kinotes.kinotes.repository;

import com.kinotes.kinotes.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Reminder entity.
 * Provides CRUD operations and custom queries for reminders.
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    /**
     * Find all reminders for a specific note
     * @param noteId the note ID
     * @return list of reminders for the note
     */
    List<Reminder> findByNoteId(UUID noteId);

    /**
     * Find all untriggered reminders that are due (reminder_at <= now)
     * @param now the current timestamp
     * @return list of due reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.isTriggered = false AND r.reminderAt <= :now")
    List<Reminder> findDueReminders(@Param("now") OffsetDateTime now);

    /**
     * Find all reminders for a user's notes
     * @param userId the user ID
     * @return list of reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.note.user.id = :userId ORDER BY r.reminderAt ASC")
    List<Reminder> findByUserId(@Param("userId") UUID userId);

    /**
     * Find upcoming reminders for a user (not triggered, future dates)
     * @param userId the user ID
     * @param now the current timestamp
     * @return list of upcoming reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.note.user.id = :userId AND r.isTriggered = false AND r.reminderAt > :now ORDER BY r.reminderAt ASC")
    List<Reminder> findUpcomingRemindersByUserId(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);
}
