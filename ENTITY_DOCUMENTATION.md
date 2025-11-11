# Kinotes Backend Entity Documentation

## Overview
This document describes the Hibernate entity model for the Kinotes application, generated from the SQL schema in `src/main/db/kinotes.sql`.

## Entity Relationship Diagram (Text-Based)

```
┌─────────────────────┐
│       User          │
│ (users table)       │
├─────────────────────┤
│ id: UUID (PK)       │◄──────────┐
│ username: String    │           │
│ fullName: String    │           │
│ createdAt: DateTime │           │
└─────────────────────┘           │
         │                        │
         │ 1:N                    │
         ▼                        │
┌─────────────────────┐           │
│       Note          │           │
│ (notes table)       │           │
├─────────────────────┤           │
│ id: UUID (PK)       │           │
│ title: String       │           │
│ content: Text       │           │
│ imageUrls: JSONB    │           │
│ createdAt: DateTime │           │
│ updatedAt: DateTime │           │
│ userId: UUID (FK)   │───────────┘
└─────────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────────┐
│     Reminder        │
│ (reminders table)   │
├─────────────────────┤
│ id: UUID (PK)       │
│ reminderAt: DateTime│
│ isTriggered: Boolean│
│ createdAt: DateTime │
│ noteId: UUID (FK)   │───────────┐
└─────────────────────┘           │
                                  │
                                  └──► References Note
```

## Entity Classes

### 1. User Entity
**Table:** `users`  
**Primary Key:** `id` (UUID)

#### Special Considerations:
- **Supabase Auth Integration**: The `id` field is NOT auto-generated. It references the Supabase Auth user ID (`auth.users(id)`).
- When creating a user, you must provide the UUID from Supabase Auth.
- The `@GeneratedValue` annotation is intentionally omitted.

#### Fields:
| Field | Type | Column | Constraints | Description |
|-------|------|--------|-------------|-------------|
| id | UUID | id | PK, NOT NULL | Same as Supabase Auth user ID |
| username | String | username | UNIQUE, NOT NULL, max 100 chars | Unique username |
| fullName | String | full_name | max 255 chars | User's full name |
| createdAt | OffsetDateTime | created_at | NOT NULL, auto-generated | Account creation timestamp |

#### Relationships:
- **One-to-Many** with `Note`: A user can have multiple notes
  - Mapped by: `user` field in `Note` entity
  - Cascade: `ALL` (create, update, delete operations cascade to notes)
  - Orphan Removal: `true` (notes are deleted when removed from user's collection)

#### Helper Methods:
- `addNote(Note note)`: Adds a note and maintains bidirectional relationship
- `removeNote(Note note)`: Removes a note and maintains bidirectional relationship

---

### 2. Note Entity
**Table:** `notes`  
**Primary Key:** `id` (UUID, auto-generated)

#### Fields:
| Field | Type | Column | Constraints | Description |
|-------|------|--------|-------------|-------------|
| id | UUID | id | PK, NOT NULL, auto-generated | Primary key |
| title | String | title | NOT NULL, max 255 chars | Note title |
| content | String | content | TEXT | Note content |
| imageUrls | List\<String\> | image_urls | JSONB, default [] | Array of Supabase Storage URLs |
| createdAt | OffsetDateTime | created_at | NOT NULL, auto-generated | Creation timestamp |
| updatedAt | OffsetDateTime | updated_at | NOT NULL, auto-updated | Last update timestamp |

#### Special Considerations:
- **JSONB Mapping**: The `imageUrls` field uses `@JdbcTypeCode(SqlTypes.JSON)` for Hibernate 6+ compatibility
- **Timestamps**: Uses `@CreationTimestamp` and `@UpdateTimestamp` for automatic timestamp management
- **Lazy Loading**: The `user` relationship uses `FetchType.LAZY` for performance

#### Relationships:
- **Many-to-One** with `User`: Each note belongs to one user
  - Join Column: `user_id`
  - Fetch Type: `LAZY`
  - Nullable: `false` (a note must have a user)

- **One-to-Many** with `Reminder`: A note can have multiple reminders
  - Mapped by: `note` field in `Reminder` entity
  - Cascade: `ALL`
  - Orphan Removal: `true`

#### Helper Methods:
- `addReminder(Reminder reminder)`: Adds a reminder and maintains bidirectional relationship
- `removeReminder(Reminder reminder)`: Removes a reminder and maintains bidirectional relationship

---

### 3. Reminder Entity
**Table:** `reminders`  
**Primary Key:** `id` (UUID, auto-generated)

#### Fields:
| Field | Type | Column | Constraints | Description |
|-------|------|--------|-------------|-------------|
| id | UUID | id | PK, NOT NULL, auto-generated | Primary key |
| reminderAt | OffsetDateTime | reminder_at | NOT NULL | When the reminder should trigger |
| isTriggered | Boolean | is_triggered | NOT NULL, default false | Whether reminder has been triggered |
| createdAt | OffsetDateTime | created_at | NOT NULL, auto-generated | Creation timestamp |

#### Relationships:
- **Many-to-One** with `Note`: Each reminder belongs to one note
  - Join Column: `note_id`
  - Fetch Type: `LAZY`
  - Nullable: `false` (a reminder must have a note)

---

## Repository Interfaces

### UserRepository
Extends `JpaRepository<User, UUID>`

**Custom Methods:**
- `Optional<User> findByUsername(String username)` - Find user by username
- `boolean existsByUsername(String username)` - Check if username exists

### NoteRepository
Extends `JpaRepository<Note, UUID>`

**Custom Methods:**
- `List<Note> findByUserId(UUID userId)` - Get all notes for a user
- `List<Note> findByUserIdOrderByCreatedAtDesc(UUID userId)` - Get notes ordered by creation date
- `List<Note> searchNotesByTitle(UUID userId, String searchTerm)` - Search notes by title (case-insensitive)
- `long countByUserId(UUID userId)` - Count user's notes

### ReminderRepository
Extends `JpaRepository<Reminder, UUID>`

**Custom Methods:**
- `List<Reminder> findByNoteId(UUID noteId)` - Get all reminders for a note
- `List<Reminder> findDueReminders(OffsetDateTime now)` - Get untriggered reminders that are due
- `List<Reminder> findByUserId(UUID userId)` - Get all reminders for a user's notes
- `List<Reminder> findUpcomingRemindersByUserId(UUID userId, OffsetDateTime now)` - Get upcoming reminders for a user

---

## Schema Alignment Analysis

### ✅ Correctly Mapped

1. **Table Names**: All entities use correct snake_case table names matching SQL schema
2. **Primary Keys**: All use UUID type with appropriate generation strategies
3. **Foreign Keys**: All relationships properly mapped with `@JoinColumn`
4. **Timestamps**: Using `@CreationTimestamp` and `@UpdateTimestamp` for automatic management
5. **Cascade Operations**: Properly configured to match SQL `ON DELETE CASCADE`
6. **JSONB Support**: `imageUrls` field correctly mapped using `@JdbcTypeCode(SqlTypes.JSON)`

### ⚠️ Important Notes

1. **User ID Generation**:
   - SQL: `id UUID PRIMARY KEY REFERENCES auth.users(id)`
   - Entity: No `@GeneratedValue` - ID must come from Supabase Auth
   - **Action Required**: When creating users, always provide the Supabase Auth UUID

2. **Timezone Handling**:
   - SQL: `TIMESTAMP WITH TIME ZONE`
   - Entity: `OffsetDateTime` (correct mapping for timezone-aware timestamps)

3. **JSONB Array**:
   - SQL: `image_urls JSONB DEFAULT '[]'::jsonb`
   - Entity: `List<String>` with `@JdbcTypeCode(SqlTypes.JSON)`
   - Hibernate will automatically serialize/deserialize the list to/from JSON

### 🔧 Recommendations

1. **Enable Lombok Annotation Processing**: Ensure Lombok is properly configured in your IDE
2. **Database Configuration**: Set up `application.properties` or `application.yml` with PostgreSQL connection details
3. **Supabase Integration**: Create a service layer to handle user creation that integrates with Supabase Auth
4. **Validation**: Consider adding `@Valid` annotations and validation constraints (e.g., `@NotBlank`, `@Size`)
5. **DTOs**: Create Data Transfer Objects for API requests/responses to avoid exposing entity internals

---

## Example Usage

### Creating a User (with Supabase Auth)
```java
// After authenticating with Supabase and getting the user ID
UUID supabaseUserId = // ... from Supabase Auth
User user = User.builder()
    .id(supabaseUserId)  // Must provide the Supabase Auth ID
    .username("johndoe")
    .fullName("John Doe")
    .build();
userRepository.save(user);
```

### Creating a Note
```java
User user = userRepository.findById(userId).orElseThrow();
Note note = Note.builder()
    .title("My First Note")
    .content("This is the content of my note")
    .imageUrls(List.of("https://storage.supabase.co/image1.jpg"))
    .user(user)
    .build();
noteRepository.save(note);
```

### Creating a Reminder
```java
Note note = noteRepository.findById(noteId).orElseThrow();
Reminder reminder = Reminder.builder()
    .reminderAt(OffsetDateTime.now().plusDays(1))
    .note(note)
    .build();
reminderRepository.save(reminder);
```

### Querying Due Reminders
```java
List<Reminder> dueReminders = reminderRepository.findDueReminders(OffsetDateTime.now());
dueReminders.forEach(reminder -> {
    // Send notification
    reminder.setIsTriggered(true);
    reminderRepository.save(reminder);
});
```

---

## Dependencies Required

All required dependencies are already present in `pom.xml`:
- ✅ Spring Boot Starter Data JPA
- ✅ PostgreSQL Driver
- ✅ Lombok
- ✅ Jackson (for JSON processing)
- ✅ Supabase Java Client

---

## Next Steps

1. **Configure Database Connection**: Set up `application.properties` with your Supabase PostgreSQL connection string
2. **Create Service Layer**: Implement business logic in service classes
3. **Create DTOs**: Define request/response objects for your REST API
4. **Create Controllers**: Implement REST endpoints
5. **Add Validation**: Use Bean Validation annotations
6. **Implement Supabase Auth Integration**: Create authentication/authorization logic
7. **Add Exception Handling**: Implement global exception handlers
8. **Write Tests**: Create unit and integration tests

---

Generated on: 2025-11-02  
Schema Version: 1.0  
Hibernate Version: 6.x (Jakarta Persistence API)
