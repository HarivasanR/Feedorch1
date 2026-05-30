package com.feedorch1.feedorch1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoryInteractionEvent ( //immutable class

    @NotNull(message = "user id is required")
    Long userId,

    @NotNull(message = "Story ID is required")
    Long storyId,

    @NotNull(message = "Author ID is required")
    Long authorId,    

    @NotNull(message = "duration seconds is required")
    Double durationSeconds,

    @NotBlank(message = "interaction type is required")
    String interactionType
    
) {}
