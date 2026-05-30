package com.feedorch1.feedorch1.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.feedorch1.feedorch1.model.ContentType;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

//record means immutable class
public record ContentRequest (

    @NotNull(message = "Author ID cannot be null")
    Long authorId,

    @NotNull(message = "Content type is required (POST, STORY, REEL)")
    ContentType type,

    @NotBlank(message = "Category cannot be blank")
    String category,

    String mediaUrl,

    @JsonProperty("contentValue")
    @JsonAlias({"content_value", "contentvalue"})
    @NotBlank(message = "Content value text cannot be empty")
    @Size(max = 1000, message = "Content length cannot exceed 1000 characters")
    String contentValue
) {}
//this mentions what the content request format. just like format for post in previous project.