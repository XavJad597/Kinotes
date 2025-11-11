package com.kinotes.kinotes.note.repository;

import com.kinotes.kinotes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Note entity.
 * Provides CRUD operations and custom queries for notes.
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    /**
     * Find all notes by user ID
     * @param userId the user ID
     * @return list of notes belonging to the user
     */
    List<Note> findByUserId(UUID userId);

    /**
     * Find notes by user ID ordered by creation date descending
     * @param userId the user ID
     * @return list of notes ordered by most recent first
     */
    List<Note> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find notes by title containing a search term (case-insensitive)
     * @param userId the user ID
     * @param searchTerm the search term
     * @return list of matching notes
     */
    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Note> searchNotesByTitle(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm);

    /**
     * Count notes by user ID
     * @param userId the user ID
     * @return number of notes
     */
    long countByUserId(UUID userId);
}
