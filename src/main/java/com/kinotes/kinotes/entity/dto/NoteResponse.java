package com.kinotes.kinotes.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponse {

    private UUID id;
    private String title;
    private String content;
    
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
    
    private UUID userId;
    private String username;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
