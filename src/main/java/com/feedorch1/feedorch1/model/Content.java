package com.feedorch1.feedorch1.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "content")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id")
    private Long authorId; //this is the user who posted it

    @Enumerated(EnumType.STRING)
    private ContentType type; //post, story, reel

    private String category; //actually want multiple tags, will do in a while

    @Column(name = "content_value", columnDefinition = "TEXT")
    @JsonProperty("contentValue")
    @JsonAlias({"content_value", "contentvalue"})
    private String contentValue;
    
    private String mediaUrl;

    private LocalDateTime createdAt = LocalDateTime.now(); //default to now when created
}
